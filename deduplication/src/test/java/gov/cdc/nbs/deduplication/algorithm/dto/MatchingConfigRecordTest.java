package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class MatchingConfigRecordTest {

    @Test
    void testMatchingConfigRecordConstructorAndGetters() {
        // Sample data
        String passName = "Pass 1";
        String description = "Description of Pass 1";
        List<String> blockingCriteria = List.of("firstName", "lastName");
        List<List<String>> matchingCriteria = List.of(
                List.of("firstName", "exact"),
                List.of("lastName", "jarowinkler")
        );
        String lowerBound = "0.5";
        String upperBound = "1.0";
        boolean active = true;

        // Create MatchingConfigRecord
        MatchingConfigRecord matchingConfig = new MatchingConfigRecord(
                passName, description, blockingCriteria, matchingCriteria, lowerBound, upperBound, active
        );

        // Check if the object was created correctly
        assertEquals(passName, matchingConfig.passName());
        assertEquals(description, matchingConfig.description());
        assertEquals(blockingCriteria, matchingConfig.blockingCriteria());
        assertEquals(matchingCriteria, matchingConfig.matchingCriteria());
        assertEquals(lowerBound, matchingConfig.lowerBound());
        assertEquals(upperBound, matchingConfig.upperBound());
        assertTrue(matchingConfig.active());
    }
}
