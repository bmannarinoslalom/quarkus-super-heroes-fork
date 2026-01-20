package io.quarkus.sample.superheroes.narration.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import io.quarkus.sample.superheroes.narration.Fight;
import io.quarkus.sample.superheroes.narration.Fight.FightLocation;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkiverse.wiremock.devservice.WireMockConfigKey;
import io.restassured.RestAssured;

/**
 * Integration test that verifies OpenAI configuration via environment variables.
 * <p>
 * This test validates that the docker-compose configuration pattern works:
 * <ul>
 *   <li>QUARKUS_LANGCHAIN4J_OPENAI_ENABLE_INTEGRATION=true enables the integration</li>
 *   <li>QUARKUS_LANGCHAIN4J_OPENAI_API_KEY passes the API key to requests</li>
 * </ul>
 * </p>
 *
 * @see <a href="https://github.com/quarkusio/quarkus-super-heroes/blob/main/deploy/docker-compose/java21.yml">docker-compose configuration</a>
 */
@QuarkusIntegrationTest
@TestProfile(OpenAiEnvConfigIT.CustomApiKeyTestProfile.class)
@ConnectWireMock
@DisabledIf(value = "azureOpenAiEnabled", disabledReason = "Azure OpenAI profile is enabled")
class OpenAiEnvConfigIT {

  /**
   * Custom API key used to verify environment variable configuration works.
   * This simulates what docker-compose does with QUARKUS_LANGCHAIN4J_OPENAI_API_KEY.
   */
  static final String CUSTOM_API_KEY = "sk-test-custom-key-from-env";

  private static final String HERO_NAME = "Super Baguette";
  private static final int HERO_LEVEL = 42;
  private static final String HERO_POWERS = "Eats baguette in less than a second";
  private static final String VILLAIN_NAME = "Super Chocolatine";
  private static final int VILLAIN_LEVEL = 43;
  private static final String VILLAIN_POWERS = "Transforms chocolatine into pain au chocolat";
  private static final String DEFAULT_LOCATION_NAME = "Gotham City";
  private static final String DEFAULT_LOCATION_DESCRIPTION = "An American city rife with corruption and crime, the home of its iconic protector Batman.";

  private static final Fight FIGHT = new Fight(
    VILLAIN_NAME, VILLAIN_LEVEL, VILLAIN_POWERS,
    HERO_NAME, HERO_LEVEL, HERO_POWERS,
    "villains", "heroes",
    new FightLocation(DEFAULT_LOCATION_NAME, DEFAULT_LOCATION_DESCRIPTION)
  );

  private static final String EXPECTED_NARRATION = "In the gritty streets of Gotham City, a clash of epic proportions unfolded.";

  WireMock wireMock;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach() {
    this.wireMock.resetToDefaultMappings();
  }

  static boolean azureOpenAiEnabled() {
    return "azure-openai".equals(System.getenv("QUARKUS_TEST_INTEGRATION_TEST_PROFILE")) ||
      "azure-openai".equals(System.getProperty("quarkus.test.integration-test-profile"));
  }

  /**
   * Verifies that a custom API key configured via environment variable
   * (QUARKUS_LANGCHAIN4J_OPENAI_API_KEY) is correctly passed in the
   * Authorization header to OpenAI requests.
   * <p>
   * This validates the docker-compose configuration pattern:
   * <pre>
   * environment:
   *   QUARKUS_LANGCHAIN4J_OPENAI_API_KEY: ${OPENAI_API_KEY:-}
   * </pre>
   * </p>
   */
  @Test
  void shouldUseCustomApiKeyFromEnvironment() {
    // Setup WireMock to return a successful response
    this.wireMock.register(
      post(urlPathEqualTo("/v1/chat/completions"))
        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + CUSTOM_API_KEY))
        .willReturn(okJson("""
          {
            "id": "chatcmpl-123",
            "object": "chat.completion",
            "created": 1677652288,
            "model": "gpt-4o-mini",
            "choices": [{
              "index": 0,
              "message": {
                "role": "assistant",
                "content": "%s"
              },
              "finish_reason": "stop"
            }],
            "usage": {"prompt_tokens": 9, "completion_tokens": 12, "total_tokens": 21}
          }
          """.formatted(EXPECTED_NARRATION)))
    );

    // Make a request to trigger OpenAI call
    given()
      .body(FIGHT)
      .contentType(JSON)
      .accept(TEXT)
      .when().post("/api/narration").then()
        .statusCode(OK.getStatusCode())
        .contentType(TEXT)
        .body(is(EXPECTED_NARRATION));

    // Verify the custom API key was used in the Authorization header
    this.wireMock.verifyThat(
      1,
      postRequestedFor(urlPathEqualTo("/v1/chat/completions"))
        .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + CUSTOM_API_KEY))
    );
  }

  /**
   * Verifies that when QUARKUS_LANGCHAIN4J_OPENAI_ENABLE_INTEGRATION=true,
   * the service actually makes calls to the OpenAI endpoint instead of
   * returning fallback responses.
   */
  @Test
  void shouldEnableOpenAiIntegrationViaEnvironment() {
    // Setup WireMock to capture the request
    this.wireMock.register(
      post(urlPathEqualTo("/v1/chat/completions"))
        .willReturn(okJson("""
          {
            "id": "chatcmpl-123",
            "object": "chat.completion",
            "created": 1677652288,
            "model": "gpt-4o-mini",
            "choices": [{
              "index": 0,
              "message": {
                "role": "assistant",
                "content": "%s"
              },
              "finish_reason": "stop"
            }],
            "usage": {"prompt_tokens": 9, "completion_tokens": 12, "total_tokens": 21}
          }
          """.formatted(EXPECTED_NARRATION)))
    );

    // Make a request
    given()
      .body(FIGHT)
      .contentType(JSON)
      .accept(TEXT)
      .when().post("/api/narration").then()
        .statusCode(OK.getStatusCode());

    // Verify OpenAI endpoint was called (integration is enabled)
    this.wireMock.verifyThat(
      1,
      postRequestedFor(urlPathEqualTo("/v1/chat/completions"))
    );
  }

  /**
   * Test profile that simulates docker-compose environment variable configuration.
   * Maps to:
   * <pre>
   * environment:
   *   QUARKUS_LANGCHAIN4J_OPENAI_ENABLE_INTEGRATION: "true"
   *   QUARKUS_LANGCHAIN4J_OPENAI_API_KEY: ${OPENAI_API_KEY:-}
   * </pre>
   */
  public static class CustomApiKeyTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      var hostname = Boolean.getBoolean("quarkus.container-image.build") ? "host.docker.internal" : "localhost";

      return Map.of(
        // Simulates: QUARKUS_LANGCHAIN4J_OPENAI_ENABLE_INTEGRATION=true
        "quarkus.langchain4j.openai.enable-integration", "true",
        // Simulates: QUARKUS_LANGCHAIN4J_OPENAI_API_KEY=${OPENAI_API_KEY:-}
        "quarkus.langchain4j.openai.api-key", CUSTOM_API_KEY,
        // Point to WireMock for testing
        "quarkus.langchain4j.openai.base-url", "http://%s:${%s}/v1/".formatted(hostname, WireMockConfigKey.PORT),
        "quarkus.langchain4j.openai.log-requests", "true",
        "quarkus.langchain4j.openai.log-responses", "true",
        "quarkus.langchain4j.openai.timeout", "3s",
        "quarkus.langchain4j.openai.max-retries", "1"
      );
    }
  }
}
