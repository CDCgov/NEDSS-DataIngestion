package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.http.MediaType;
import java.util.List;

class AlgorithmServiceTest {

    @Mock private RestClient recordLinkageClient;
    @Mock private NamedParameterJdbcTemplate template;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private AlgorithmService algorithmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetDibbsBasicToFalse() {
        AlgorithmUpdateRequest updateRequest = new AlgorithmUpdateRequest();
        updateRequest.setLabel("dibbs-basic");

        // Mocking the non-void method chain for 'put()'
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Mocking template update method
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        algorithmService.setDibbsBasicToFalse();

        verify(recordLinkageClient, times(1)).put();
    }

    @Test
    void testGetMatchingConfiguration() {
        String sql = "SELECT TOP 1 configuration FROM match_configuration ORDER BY add_time DESC";
        String mockConfigJson = "{\"label\":\"test\"}";

        // Use when(...).thenReturn(...) for methods that return values
        when(template.queryForObject(eq(sql), any(MapSqlParameterSource.class), eq(String.class))).thenReturn(mockConfigJson);

        MatchingConfigRequest result = algorithmService.getMatchingConfiguration();

        assertNotNull(result);
        assertEquals("test", result.getLabel());
    }

    @Test
    void testConfigureMatching() {
        // Setup mock data
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("test");

        // Initialize passes to avoid NullPointerException
        Pass pass = new Pass();
        pass.setLowerBound("0.2");
        pass.setUpperBound("0.8");

        // Add blocking criteria to the pass
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("Date of birth");
        blockingCriteria.setField(field);
        pass.setBlockingCriteria(List.of(blockingCriteria));

        request.setPasses(List.of(pass));

        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1); // Simulate successful database update

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Call the method to test
        algorithmService.configureMatching(request);

        verify(template, times(1)).update(anyString(), any(SqlParameterSource.class)); // Verifying saveMatchingConfiguration call
        verify(recordLinkageClient, times(1)).put(); // Verifying updateAlgorithm call
    }

    @Test
    void testUpdateDibbsConfigurations() {
        // Setup mock data
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        // Initialize passes to avoid NullPointerException
        Pass pass = new Pass();
        pass.setLowerBound("0.2");
        pass.setUpperBound("0.8");

        // Add blocking criteria to the pass (this is required to avoid NullPointerException)
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("BIRTHDATE"); // Set a valid field name
        blockingCriteria.setField(field);
        pass.setBlockingCriteria(List.of(blockingCriteria)); // Set valid blocking criteria

        request.setPasses(List.of(pass)); // Set valid passes here

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);

        when(mockRequestBodyUriSpec.uri(eq("/algorithm/dibbs-basic"), any(Object[].class))).thenReturn(mockRequestBodyUriSpec);  // Make uri() call specific
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);

        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);

        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1); // Simulate successful database update

        // Call the method to test
        algorithmService.updateDibbsConfigurations(request);

        verify(recordLinkageClient, times(2)).put(); // Verifying updateAlgorithm call
        verify(template, times(1)).update(anyString(), any(SqlParameterSource.class)); // Verifying setDibbsBasicToFalse call
    }

    @Test
    void testUpdateAlgorithm_withValidBounds() throws Exception {
        // Setup mock data
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");
        configRequest.setPasses(List.of(pass));

        // Mock ObjectMapper behavior
        AlgorithmUpdateRequest algorithmUpdateRequest = mock(AlgorithmUpdateRequest.class);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Call the method to test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));

        // Verify that the belongingness ratio was set correctly
        verify(algorithmUpdateRequest).setBelongingnessRatio(new Double[]{0.1, 0.9});
    }

    @Test
    void testUpdateAlgorithm_withInvalidBounds() throws Exception {
        // Setup mock data with invalid bounds
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("invalid");
        pass.setUpperBound("invalid");
        configRequest.setPasses(List.of(pass));

        // Mock ObjectMapper behavior
        AlgorithmUpdateRequest algorithmUpdateRequest = mock(AlgorithmUpdateRequest.class);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Call the method to test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));

        // Verify that the default belongingness ratio was set (0.0, 1.0) due to invalid bounds
        verify(algorithmUpdateRequest).setBelongingnessRatio(new Double[]{0.0, 1.0});
    }

    @Test
    void testUpdateAlgorithm_withMissingBounds() throws Exception {
        // Setup mock data with missing bounds
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound(null);
        pass.setUpperBound(null);
        configRequest.setPasses(List.of(pass));

        // Mock ObjectMapper behavior
        AlgorithmUpdateRequest algorithmUpdateRequest = mock(AlgorithmUpdateRequest.class);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Call the method to test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));

        // Verify that the default belongingness ratio was set (0.0, 1.0) due to missing bounds
        verify(algorithmUpdateRequest).setBelongingnessRatio(new Double[]{0.0, 1.0});
    }

    @Test
    void testSetDibbsBasicToFalse_jsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON Error") {});

        algorithmService.setDibbsBasicToFalse();

        // Verify that REST client interaction never happens due to JSON error
        verify(recordLinkageClient, never()).put();
    }

    @Test
    void testGetMatchingConfiguration_genericException() {
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), eq(String.class)))
                .thenThrow(new RuntimeException("DB Error"));

        MatchingConfigRequest result = algorithmService.getMatchingConfiguration();

        assertNull(result, "Expected null when database throws an error");
    }

    @Test
    void testGetMatchingConfiguration_emptyResultException() {
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), eq(String.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        MatchingConfigRequest result = algorithmService.getMatchingConfiguration();

        assertNull(result, "Expected null when no matching config found");
    }

}
