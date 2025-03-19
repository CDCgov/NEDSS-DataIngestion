package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportConfigRecordTest {

    @Test
    void testExportConfigRecordConstructorAndGetters() {
        DataElementRecord dataElement1 = new DataElementRecord("firstName", 0.8, 0.5, 0.7);
        DataElementRecord dataElement2 = new DataElementRecord("lastName", 0.9, 0.6, 0.8);

        // Sample data for MatchingConfigRecord
        MatchingConfigRecord matchingConfig1 = new MatchingConfigRecord(
                "Pass 1", "Description of Pass 1",
                List.of("firstName", "lastName"),
                List.of(List.of("firstName", "jarowinkler")), "0.5", "1.0", true
        );

        // Create ExportConfigRecord
        ExportConfigRecord exportConfig = new ExportConfigRecord(
                List.of(dataElement1, dataElement2), List.of(matchingConfig1)
        );

        assertEquals(2, exportConfig.dataElements().size());
        assertEquals("firstName", exportConfig.dataElements().get(0).field());
        assertEquals(0.8, exportConfig.dataElements().get(0).oddsRatio());
        assertEquals("Pass 1", exportConfig.matchingConfiguration().get(0).passName());
    }
}
