package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AlgorithmControllerTest {

    @Mock
    private AlgorithmService algorithmService;

    @InjectMocks
    private AlgorithmController algorithmController;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(algorithmController).build();
    }

    @Test
    void testConfigureMatching() {
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        List<Pass> passes = List.of(new Pass(
                "TestPass",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria,
                List.of()
        ));

        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                passes
        );

        doNothing().when(algorithmService).configureMatching(request);

        algorithmController.configureMatching(request);

        verify(algorithmService, times(1)).configureMatching(request);
    }

    @Test
    void testGetMatchingConfiguration() {
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        List<Pass> passes = List.of(new Pass(
                "TestPass",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria, // Updated to Map<String, Boolean>
                List.of()
        ));

        when(algorithmService.getMatchingConfiguration()).thenReturn(passes);

        Map<String, List<Pass>> actualResponse = algorithmController.getMatchingConfiguration();

        assertNotNull(actualResponse);
        assertTrue(actualResponse.containsKey("passes"));
        assertEquals(passes, actualResponse.get("passes"));
    }

    @Test
    void testUpdateAlgorithm() {
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        List<Pass> passes = List.of(new Pass(
                "TestPass",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria, // Updated to Map<String, Boolean>
                List.of()
        ));

        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                passes
        );

        doNothing().when(algorithmService).updateDibbsConfigurations(request);

        algorithmController.updateAlgorithm(request);

        verify(algorithmService, times(1)).updateDibbsConfigurations(request);
    }

    @Test
    void testExportConfiguration() throws IOException {
        List<Pass> mockPasses = List.of(new Pass("pass1", "description", "0.1", "0.9",
                Map.of("FIRST_NAME", true, "LAST_NAME", false), List.of()));

        when(algorithmService.getMatchingConfiguration()).thenReturn(mockPasses);

        doReturn("[{\"name\":\"pass1\",\"description\":\"description\",\"lowerBound\":\"0.1\",\"upperBound\":\"0.9\",\"blockingCriteria\":{\"FIRST_NAME\":true,\"LAST_NAME\":false},\"matchingCriteria\":[]}]")
                .when(objectMapper).writeValueAsString(mockPasses);

        ResponseEntity<InputStreamResource> response = algorithmController.exportConfiguration();

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0).contains("attachment; filename="));
    }

    @Test
    void testImportConfiguration() throws Exception {
        // Prepare mock data
        MatchingConfigRequest mockConfigRequest = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of()
        );

        doNothing().when(algorithmService).saveMatchingConfiguration(mockConfigRequest);

        String jsonRequest = objectMapper.writeValueAsString(mockConfigRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/deduplication/import-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Configuration imported successfully."));

        verify(algorithmService).saveMatchingConfiguration(mockConfigRequest);
    }
}
