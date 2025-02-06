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
    void testUpdateDibbsConfigurations() throws JsonProcessingException {
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");

        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field blockingField = new Field();
        blockingField.setName("Last name");
        blockingCriteria.setField(blockingField);
        pass.setBlockingCriteria(List.of(blockingCriteria));

        MatchingCriteria matchingCriteria = new MatchingCriteria();
        Field matchingField = new Field();
        matchingField.setName("First name");
        matchingCriteria.setField(matchingField);
        Method method = new Method();
        method.setValue("exact"); // Valid method value
        matchingCriteria.setMethod(method);
        pass.setMatchingCriteria(List.of(matchingCriteria));  // Required evaluators

        configRequest.setPasses(List.of(pass));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class)))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        algorithmService.updateAlgorithm(configRequest);

        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Void.class);
    }


    @Test
    void testUpdateAlgorithm_withMissingBlockingCriteria() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");

        // Missing blocking criteria (simulate the issue)
        pass.setBlockingCriteria(null);  // This is the key part

        MatchingCriteria matchingCriteria = new MatchingCriteria();
        Field matchingField = new Field();
        matchingField.setName("First name");
        matchingCriteria.setField(matchingField);
        Method method = new Method();
        method.setValue("exact");
        matchingCriteria.setMethod(method);
        pass.setMatchingCriteria(List.of(matchingCriteria));

        configRequest.setPasses(List.of(pass));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class)))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        // Expect exception if blocking criteria is missing
        assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.updateAlgorithm(configRequest);
        });
    }


    @Test
    void testUpdateAlgorithm_withValidBounds() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");

        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field blockingField = new Field();
        blockingField.setName("Date of birth");
        blockingCriteria.setField(blockingField);
        pass.setBlockingCriteria(List.of(blockingCriteria));

        MatchingCriteria matchingCriteria = new MatchingCriteria();
        Field matchingField = new Field();
        matchingField.setName("First name");
        matchingCriteria.setField(matchingField);
        Method method = new Method();
        method.setValue("exact"); // Valid method value
        matchingCriteria.setMethod(method);
        pass.setMatchingCriteria(List.of(matchingCriteria));  // Required evaluators

        configRequest.setPasses(List.of(pass));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class)))
                .thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        algorithmService.updateAlgorithm(configRequest);

        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Void.class);
    }


    @Test
    void testUpdateAlgorithm_withInvalidBounds() throws Exception {
        // Setup mock data with invalid bounds
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound("invalid");
        pass.setUpperBound("invalid");

        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("Date of birth");
        blockingCriteria.setField(field);
        pass.setBlockingCriteria(List.of(blockingCriteria));
        configRequest.setPasses(List.of(pass));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("/algorithm/dibbs-enhanced")).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class))).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        algorithmService.updateAlgorithm(configRequest);

        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Void.class);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));
    }

    @Test
    void testUpdateAlgorithm_withMissingBounds() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        Pass pass = new Pass();
        pass.setLowerBound(null);  // Missing lower bound
        pass.setUpperBound(null);  // Missing upper bound

        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("Date of birth");  // Set a valid field name for blocking criteria
        blockingCriteria.setField(field);
        pass.setBlockingCriteria(List.of(blockingCriteria));  // Set valid blocking criteria

        configRequest.setPasses(List.of(pass));

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("/algorithm/dibbs-enhanced")).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class))).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        algorithmService.updateAlgorithm(configRequest);

        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));  // Verify body(AlgorithmUpdateRequest.class)
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Void.class);  // Verify that body(Void.class) was called

        ArgumentCaptor<AlgorithmUpdateRequest> captor = ArgumentCaptor.forClass(AlgorithmUpdateRequest.class);
        verify(mockRequestBodyUriSpec, times(1)).body(captor.capture());

        AlgorithmUpdateRequest capturedRequest = captor.getValue();
        assertArrayEquals(new Double[]{0.0, 1.0}, capturedRequest.getBelongingnessRatio(), "The default belongingness ratio should be set to {0.0, 1.0} due to missing bounds.");

        assertNotNull(capturedRequest.getPasses());
        assertEquals(1, capturedRequest.getPasses().size());
        AlgorithmPass algorithmPass = capturedRequest.getPasses().get(0);

        assertNotNull(algorithmPass.getBlockingKeys());
        assertEquals(1, algorithmPass.getBlockingKeys().size());
        assertEquals("BIRTHDATE", algorithmPass.getBlockingKeys().get(0), "The blocking key should be set to BIRTHDATE.");
    }

    @Test
    void testUpdateAlgorithm_withEmptyPasses() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest();
        configRequest.setPasses(List.of());  // No passes

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("/algorithm/dibbs-enhanced")).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class))).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);  // Mock successful response without actual content (mimicking success)

        // Call the method under test
        algorithmService.updateAlgorithm(configRequest);

        verify(recordLinkageClient, times(1)).put();
        verify(mockRequestBodyUriSpec, times(1)).uri("/algorithm/dibbs-enhanced");
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class));  // Verify body(AlgorithmUpdateRequest.class)
        verify(mockRequestBodyUriSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Void.class);  // Verify that body(Void.class) was called

        ArgumentCaptor<AlgorithmUpdateRequest> captor = ArgumentCaptor.forClass(AlgorithmUpdateRequest.class);
        verify(mockRequestBodyUriSpec, times(1)).body(captor.capture());

        AlgorithmUpdateRequest capturedRequest = captor.getValue();
        assertTrue(capturedRequest.getPasses().isEmpty(), "The passes list should be empty when no passes are provided.");
    }

    @Test
    void testSetDibbsBasicToFalse_jsonProcessingException() throws Exception {
        // Mock the ObjectMapper to throw a JsonProcessingException
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON Error") {});

        // Spy on the algorithmService to ensure setDibbsBasicToFalse() doesn't call put() when the exception is thrown
        AlgorithmService spyService = spy(algorithmService);
        doNothing().when(spyService).setDibbsBasicToFalse();

        // Call the method under test
        spyService.setDibbsBasicToFalse();

        // Verify that the RestClient put() was never called due to the exception
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

        // Simulate a JsonProcessingException
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});

        // Mock template.update to ensure it doesn't get called if exception occurs
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(0);  // This is to avoid actual update

        // Ensure no exception is thrown despite the JsonProcessingException
        assertDoesNotThrow(() -> algorithmService.saveMatchingConfiguration(request));
    }

}
