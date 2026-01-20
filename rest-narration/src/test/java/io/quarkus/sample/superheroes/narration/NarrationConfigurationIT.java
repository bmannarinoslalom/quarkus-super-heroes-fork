package io.quarkus.sample.superheroes.narration;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for SCRUM-5: OpenAI configuration
 * Verifies narration service properly integrates with OpenAI when configured
 */
@QuarkusIntegrationTest
public class NarrationConfigurationIT {

    @Test
    public void testNarrationWithOpenAIConfig_SCRUM5() {
        // SCRUM-5: Verify narration endpoint returns non-fallback response when OpenAI is configured
        given()
            .contentType("application/json")
            .body("""
                {
                    "winnerName": "Superman",
                    "winnerLevel": 10,
                    "winnerPowers": "Flight, strength",
                    "loserName": "Lex Luthor",
                    "loserLevel": 5,
                    "loserPowers": "Intelligence"
                }
                """)
        .when()
            .post("/api/narration")
        .then()
            .statusCode(200)
            .body(not(containsString("fallback")))
            .body(not(emptyOrNullString()));
    }

    @Test
    public void testNarrationServiceHealth_SCRUM5() {
        // SCRUM-5: Verify narration service is healthy with OpenAI configuration
        given()
        .when()
            .get("/api/narration/hello")
        .then()
            .statusCode(200);
    }
}
