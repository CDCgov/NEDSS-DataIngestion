package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.DataElementRecord;
import gov.cdc.nbs.deduplication.algorithm.dto.ExportConfigRecord;
import gov.cdc.nbs.deduplication.algorithm.dto.MatchingConfigRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlgorithmControllerTest {

    @Mock
    private AlgorithmService algorithmService;

    @InjectMocks
    private AlgorithmController algorithmController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(algorithmController).build();
    }

    @Test
    void testExportConfiguration() throws Exception {
        List<DataElementRecord> dataElements = List.of(
                new DataElementRecord("firstName", 0.8, 0.9, 0.75),
                new DataElementRecord("lastName", 1.2, 1.3, 0.85)
        );

        // Mocking MatchingConfigRecord with real values
        List<MatchingConfigRecord> matchingConfiguration = List.of(
                new MatchingConfigRecord(
                        "pass1",
                        "Pass 1 description",
                        List.of("firstName", "lastName"),
                        List.of(
                                List.of("firstName", "Exact"),
                                List.of("lastName", "jarowinkler")
                        ),
                        "0.5",
                        "0.9",
                        true
                ),
                new MatchingConfigRecord(
                        "pass2",
                        "Pass 2 description",
                        List.of("address"),
                        List.of(
                                List.of("address", "jarowinkler")
                        ),
                        "0.3",
                        "0.8",
                        false
                )
        );

        ExportConfigRecord exportConfigRecord = new ExportConfigRecord(dataElements, matchingConfiguration);

        // mocking  to return the expected byte array
        byte[] mockJsonBytes = objectMapper.writeValueAsBytes(exportConfigRecord);
        when(algorithmService.generateExportJson(exportConfigRecord)).thenReturn(mockJsonBytes);

        // Perform the POST request to the export endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/api/deduplication/export-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exportConfigRecord)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=deduplication_config.json"))
                .andExpect(MockMvcResultMatchers.content().bytes(mockJsonBytes));

        // Verify that the service method was called once with the correct argument
        verify(algorithmService, times(1)).generateExportJson(exportConfigRecord);
    }
}
