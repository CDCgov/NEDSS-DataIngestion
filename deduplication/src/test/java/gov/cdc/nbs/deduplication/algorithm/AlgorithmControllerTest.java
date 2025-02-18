package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.util.List;
import java.util.Map;

import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.support.ClassicRequestBuilder.post;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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