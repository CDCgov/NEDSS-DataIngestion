package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class AlgorithmControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlgorithmService algorithmService;

    @InjectMocks
    private AlgorithmController algorithmController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(algorithmController).build();
    }

    @Test
    void testConfigureMatching() throws Exception {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("Test Config");

        mockMvc.perform(post("/api/deduplication/configure-matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Capture the argument passed to the service
        ArgumentCaptor<MatchingConfigRequest> captor = ArgumentCaptor.forClass(MatchingConfigRequest.class);
        verify(algorithmService, times(1)).configureMatching(captor.capture());

        // Assert the captured request has the expected label
        assertEquals("Test Config", captor.getValue().getLabel());
    }


    @Test
    void testGetMatchingConfiguration() throws Exception {
        MatchingConfigRequest mockResponse = new MatchingConfigRequest();
        mockResponse.setLabel("Test Config");

        when(algorithmService.getMatchingConfiguration()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/deduplication/matching-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Test Config"));

        verify(algorithmService, times(1)).getMatchingConfiguration();
    }

    @Test
    void testUpdateAlgorithm() throws Exception {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("Updated Config");

        mockMvc.perform(post("/api/deduplication/update-algorithm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Capture the argument passed to the service
        ArgumentCaptor<MatchingConfigRequest> captor = ArgumentCaptor.forClass(MatchingConfigRequest.class);
        verify(algorithmService, times(1)).updateDibbsConfigurations(captor.capture());

        // Assert the values inside the captured object
        assertEquals("Updated Config", captor.getValue().getLabel());
    }
}
