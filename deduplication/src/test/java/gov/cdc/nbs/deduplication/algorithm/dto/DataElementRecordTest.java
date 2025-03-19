package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataElementRecordTest {

    @Test
    void testDataElementRecordConstructorAndGetters() {
        // Sample data
        String field = "firstName";
        double oddsRatio = 0.8;
        double logOdds = 0.5;
        double threshold = 0.7;

        // Create DataElementRecord
        DataElementRecord dataElement = new DataElementRecord(field, oddsRatio, logOdds, threshold);

        // Check if the object was created correctly
        assertEquals(field, dataElement.field());
        assertEquals(oddsRatio, dataElement.oddsRatio());
        assertEquals(logOdds, dataElement.logOdds());
        assertEquals(threshold, dataElement.threshold());
    }
}
