package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        // Add blocking criteria to the pass
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("Date of birth"); // Set a valid field name
        blockingCriteria.setField(field);
        pass.setBlockingCriteria(List.of(blockingCriteria)); // Set valid blocking criteria

        request.setPasses(List.of(pass)); // Set valid passes here

        // Mock RestClient behavior for setDibbsBasicToFalse()
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Mock database update for setDibbsBasicToFalse()
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        // Spy on algorithmService to verify updateAlgorithm is called
        AlgorithmService spyService = spy(algorithmService);
        doNothing().when(spyService).updateAlgorithm(any(MatchingConfigRequest.class));

        // Call the method to test
        spyService.updateDibbsConfigurations(request);

        // Verify that Step 1 (setDibbsBasicToFalse) was called
        verify(template, times(1)).update(anyString(), any(SqlParameterSource.class));

        // Verify that Step 2 (updateAlgorithm) was called
        verify(spyService, times(1)).updateAlgorithm(request);
    }


    @Test
    void testUpdateAlgorithm_withMissingBlockingCriteria() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");
        configRequest.setPasses(List.of(pass));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        try (MockedStatic<LoggerFactory> loggerFactoryMock = Mockito.mockStatic(LoggerFactory.class)) {
            Logger mockLogger = mock(Logger.class);
            loggerFactoryMock.when(() -> LoggerFactory.getLogger(AlgorithmRequestMapper.class)).thenReturn(mockLogger);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                algorithmService.updateAlgorithm(configRequest);
            });

            assertEquals("Blocking keys are required for each pass.", exception.getMessage());

            verify(mockLogger, times(1)).error(eq("Blocking keys are required for each pass."), any(IllegalArgumentException.class));

            verify(recordLinkageClient, never()).put();
        }
    }

    @Test
    void testUpdateAlgorithm_withValidBounds() throws Exception {
        // Setup mock data with valid bounds
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");
        configRequest.setPasses(List.of(pass));

        // Mock ObjectMapper behavior
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient's behavior to cover the chained methods
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));
        when(mockRequestBodyUriSpec.body(Void.class)).thenReturn(mockRequestBodyUriSpec);  // Covering the .body(Void.class) line

        // Call the method under test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient's request chain
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockRequestBodyUriSpec, times(1)).body(Void.class);  // Verify body(Void.class) was called
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
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient's behavior to cover the chained methods
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));
        when(mockRequestBodyUriSpec.body(Void.class)).thenReturn(mockRequestBodyUriSpec);  // Covering the .body(Void.class) line

        // Call the method under test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient's request chain
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockRequestBodyUriSpec, times(1)).body(Void.class);  // Verify body(Void.class) was called

        // Verify that the default belongingness ratio was set (0.0, 1.0) due to invalid bounds
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
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
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient's behavior to cover the chained methods
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));
        when(mockRequestBodyUriSpec.body(Void.class)).thenReturn(mockRequestBodyUriSpec);  // Covering the .body(Void.class) line

        // Call the method under test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient's request chain
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockRequestBodyUriSpec, times(1)).body(Void.class);  // Verify body(Void.class) was called

        // Verify that the default belongingness ratio was set (0.0, 1.0) due to missing bounds
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
    }

    @Test
    void testUpdateAlgorithm_withEmptyPasses() throws Exception {
        // Setup mock data with empty passes
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        configRequest.setPasses(List.of());  // No passes

        // Mock ObjectMapper behavior
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient's behavior to cover the chained methods
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));
        when(mockRequestBodyUriSpec.body(Void.class)).thenReturn(mockRequestBodyUriSpec);  // Covering the .body(Void.class) line

        // Call the method under test
        algorithmService.updateAlgorithm(configRequest);

        // Verify interactions with RestClient's request chain
        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockRequestBodyUriSpec, times(1)).body(Void.class);  // Verify body(Void.class) was called
    }


    @Test
    void testSetDibbsBasicToFalse_jsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON Error") {});

        algorithmService.setDibbsBasicToFalse();

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

    @Test
    void testSaveMatchingConfiguration_jsonProcessingException() throws Exception {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("TestConfig");

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});

        assertDoesNotThrow(() -> algorithmService.saveMatchingConfiguration(request));

        verify(template, never()).update(anyString(), any(SqlParameterSource.class));
    }

}
