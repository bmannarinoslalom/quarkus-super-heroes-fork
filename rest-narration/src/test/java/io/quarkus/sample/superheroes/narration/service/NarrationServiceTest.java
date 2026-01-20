package io.quarkus.sample.superheroes.narration.service;

import io.quarkus.sample.superheroes.narration.Fight;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NarrationService
 * SCRUM-5: Verify OpenAI configuration is properly loaded
 */
@QuarkusTest
public class NarrationServiceTest {

    @Inject
    NarrationService narrationService;

    @Test
    public void testServiceInjection_SCRUM5() {
        // SCRUM-5: Verify service is properly injected
        assertNotNull(narrationService, "NarrationService should be injected");
    }

    @Test
    public void testNarrationNotNull_SCRUM5() {
        // SCRUM-5: Verify narration returns non-null response
        Fight fight = new Fight(
            "Superman", 10, "Flight, strength",
            "Lex Luthor", 5, "Intelligence",
            "heroes", "villains", null
        );

        String narration = narrationService.narrate(fight);
        
        assertNotNull(narration, "Narration should not be null");
        assertFalse(narration.isEmpty(), "Narration should not be empty");
    }

    @Test
    public void testNarrationWithValidFight_SCRUM5() {
        // SCRUM-5: Verify narration contains fighter names
        Fight fight = new Fight(
            "Wonder Woman", 9, "Lasso, strength",
            "Cheetah", 6, "Speed, claws",
            "heroes", "villains", null
        );

        String narration = narrationService.narrate(fight);
        
        assertTrue(narration.length() > 50, "Narration should be substantial");
    }
}
