package io.quarkus.sample.superheroes.fight.service;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for fight narration
 * SCRUM-5: Verify narration integration works end-to-end
 */
@QuarkusIntegrationTest
public class FightNarrationIT {

    @Test
    public void testFightWithNarration_SCRUM5() {
        // SCRUM-5: Verify fight endpoint returns narration when configured
        given()
            .contentType("application/json")
            .body("""
                {
                    "hero": {"name": "Superman", "level": 10, "powers": "Flight"},
                    "villain": {"name": "Lex Luthor", "level": 5, "powers": "Intelligence"},
                    "location": {"name": "Metropolis", "description": "City"}
                }
                """)
        .when()
            .post("/api/fights")
        .then()
            .statusCode(200)
            .body("narration", notNullValue())
            .body("narration", not(emptyOrNullString()));
    }

    @Test
    public void testNarrationEndpoint_SCRUM5() {
        // SCRUM-5: Verify direct narration endpoint works
        given()
            .contentType("application/json")
            .body("""
                {
                    "winnerName": "Batman",
                    "winnerLevel": 9,
                    "winnerPowers": "Gadgets",
                    "loserName": "Joker",
                    "loserLevel": 7,
                    "loserPowers": "Chaos"
                }
                """)
        .when()
            .post("/api/fights/narrate")
        .then()
            .statusCode(200)
            .body(not(emptyOrNullString()));
    }
}
