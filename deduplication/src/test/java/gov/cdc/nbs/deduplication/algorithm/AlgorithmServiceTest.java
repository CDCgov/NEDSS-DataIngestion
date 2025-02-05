package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class AlgorithmServiceTest {

    @Mock
    private RestClient recordLinkageClient;

    @Mock
    private NamedParameterJdbcTemplate template;

    @InjectMocks
    private AlgorithmService algorithmService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the RestClient PUT request
        RestClient.RequestBodyUriSpec mockRequest = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequest);
        when(mockRequest.uri(anyString())).thenReturn(mockRequest);
        when(mockRequest.contentType(any())).thenReturn(mockRequest);
        when(mockRequest.accept(any())).thenReturn(mockRequest);
        when(mockRequest.body(any())).thenReturn(mockRequest);
        when(mockRequest.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));
        when(mockRequest.retrieve().body(Void.class)).thenReturn(null);
    }

    @Test
    void testConfigureMatching() throws Exception {
        // Prepare mock MatchingConfigRequest
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        // Execute configureMatching which internally calls saveMatchingConfiguration and updateAlgorithm
        algorithmService.configureMatching(request);

        // Verify interactions with template and recordLinkageClient
        verify(template, times(1)).update(anyString(), any(Map.class));
        verify(recordLinkageClient, times(2)).put();  // Verify PUT requests for dibbs-basic and dibbs-enhanced
    }

    @Test
    void testGetMatchingConfiguration() throws Exception {
        // Mock the database call for getting configuration
        String mockConfigJson = objectMapper.writeValueAsString(new MatchingConfigRequest());
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), eq(String.class))).thenReturn(mockConfigJson);

        // Call getMatchingConfiguration
        MatchingConfigRequest config = algorithmService.getMatchingConfiguration();

        // Verify that the returned config has the expected label
        assertNotNull(config);
    }

    @Test
    void testUpdateDibbsConfigurations(){
        // Prepare mock MatchingConfigRequest
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        // Execute updateDibbsConfigurations which internally calls setDibbsBasicToFalse and updateAlgorithm
        algorithmService.updateDibbsConfigurations(request);

        // Verify interactions with recordLinkageClient
        verify(recordLinkageClient, times(2)).put();  // Verify PUT requests for dibbs-basic and dibbs-enhanced
    }

    @Test
    void testExceptionHandlingWhenSavingConfiguration() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        // Mock JsonProcessingException during saveMatchingConfiguration
        doThrow(new JsonProcessingException("Test exception") {}).when(template).update(anyString(), any(SqlParameterSource.class));
        // Call the method and assert that the exception is handled properly
        assertThrows(RuntimeException.class, () -> {
            algorithmService.saveMatchingConfiguration(request);
        });
    }
}
