package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.DataElementRecord;
import gov.cdc.nbs.deduplication.algorithm.dto.ExportConfigRecord;
import gov.cdc.nbs.deduplication.algorithm.dto.MatchingConfigRecord;
import gov.cdc.nbs.deduplication.algorithm.exception.ExportConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlgorithmServiceTest {

    private AlgorithmService algorithmService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Mock the ObjectMapper
        objectMapper = mock(ObjectMapper.class);
        // Inject the mocked ObjectMapper into the AlgorithmService
        algorithmService = new AlgorithmService(objectMapper);
    }

    @Test
    void testGenerateExportJson() throws Exception {
        // Create a mock ExportConfigRecord
        ExportConfigRecord exportConfig = new ExportConfigRecord(
                List.of(new DataElementRecord("firstName", 0.8, 0.5, 0.7)),
                List.of(new MatchingConfigRecord(
                        "Pass 1", "Description of Pass 1",
                        List.of("firstName", "lastName"),
                        List.of(List.of("firstName", "jarowinkler")), "0.5", "1.0", true
                ))
        );

        byte[] mockJsonBytes = "{\"dataElements\":[{\"field\":\"firstName\",\"oddsRatio\":0.8,\"logOdds\":0.5,\"threshold\":0.7}],\"matchingConfiguration\":[{\"passName\":\"Pass 1\",\"description\":\"Description of Pass 1\",\"blockingCriteria\":[\"firstName\",\"lastName\"],\"matchingCriteria\":[[\"firstName\",\"jarowinkler\"]],\"lowerBound\":\"0.5\",\"upperBound\":\"1.0\",\"active\":true}]}".getBytes(); // Adjust this to match the real mock output

        // Mock ObjectMapper's writeValueAsBytes method to return mock JSON bytes
        when(objectMapper.writeValueAsBytes(exportConfig)).thenReturn(mockJsonBytes);

        // Call the method and verify that it returns the expected result
        byte[] result = algorithmService.generateExportJson(exportConfig);

        assertArrayEquals(mockJsonBytes, result);
    }


    @Test
    void testGenerateExportJsonThrowsException() throws JsonProcessingException {
        // Create a mock ExportConfigRecord
        ExportConfigRecord exportConfig = new ExportConfigRecord(
                List.of(new DataElementRecord("firstName", 0.8, 0.5, 0.7)),
                List.of(new MatchingConfigRecord(
                        "Pass 1", "Description of Pass 1",
                        List.of("firstName", "lastName"),
                        List.of(List.of("firstName", "jarowinkler")), "0.5", "1.0", true
                ))
        );

        // Simulate an exception thrown by ObjectMapper
        when(objectMapper.writeValueAsBytes(exportConfig)).thenThrow(new JsonProcessingException("Test exception") {});

        // Verify that an ExportConfigurationException is thrown when generating JSON
        ExportConfigurationException exception = assertThrows(ExportConfigurationException.class, () -> {
            algorithmService.generateExportJson(exportConfig);
        });

        assertEquals("Error while exporting configuration", exception.getMessage());
    }
}
