package io.quarkus.sample.superheroes.fight.config;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for narration service configuration
 * SCRUM-5: Verify narration client configuration is properly set up
 */
@QuarkusTest
public class NarrationClientConfigTest {

    @Inject
    FightConfig fightConfig;

    @Test
    public void testNarrationFallbackConfigured_SCRUM5() {
        // SCRUM-5: Verify fallback narration is configured
        assertNotNull(fightConfig.narration(), "Narration config should exist");
        assertNotNull(fightConfig.narration().fallbackNarration(), 
            "Fallback narration should be configured");
    }
}
