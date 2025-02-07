package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

        // Mock RestClient behavior
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Mock template update method
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        algorithmService.setDibbsBasicToFalse();

        verify(recordLinkageClient, times(1)).put();
    }


    @Test
    void testGetMatchingConfiguration() {
        String sql = "SELECT TOP 1 configuration FROM match_configuration ORDER BY add_time DESC";
        String mockConfigJson = "{\"label\":\"test\"}";

        when(template.queryForObject(eq(sql), any(MapSqlParameterSource.class), eq(String.class))).thenReturn(mockConfigJson);

        MatchingConfigRequest result = algorithmService.getMatchingConfiguration();

        assertNotNull(result);
        assertEquals("test", result.label());
    }

    @Test
    void testConfigureMatching() {
        // Setup mock data with non-empty blockingCriteria
        MatchingConfigRequest request = new MatchingConfigRequest(
                "testLabel", "testDescription", true, true,
                List.of(new Pass("name", "description", "0.2", "0.8",
                        List.of(new BlockingCriteria(new Field("FIRST_NAME", "STRING"), new Method("exact", "matcher"))), // Valid blocking criteria
                        List.of()))
        );

        // Simulate database update by returning 1 (indicating one row affected)
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        // Mock RestClient behavior
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class))).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);  // Simulating a void API call

        // Call the method to test
        algorithmService.configureMatching(request);

        // Verify interactions
        verify(template, times(1)).update(anyString(), any(SqlParameterSource.class)); // Verify DB update
        verify(recordLinkageClient, times(1)).put(); // Verify REST client call
        verify(mockRequestBodyUriSpec, times(1)).uri(anyString()); // Ensure URI is set
        verify(mockRequestBodyUriSpec, times(1)).contentType(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).accept(MediaType.APPLICATION_JSON);
        verify(mockRequestBodyUriSpec, times(1)).body(any(AlgorithmUpdateRequest.class)); // Ensure correct body is passed
        verify(mockRequestBodyUriSpec, times(1)).retrieve(); // Ensure retrieve is called
        verify(mockResponseSpec, times(1)).body(Void.class); // Ensure the response is processed correctly
    }

    @Test
    void testUpdateDibbsConfigurations() throws JsonProcessingException {
        // Setup matching config request with valid blocking criteria
        MatchingConfigRequest configRequest = new MatchingConfigRequest(
                "testLabel", "testDescription", true, true,
                List.of(new Pass("passName", "description", "0.2", "0.9",
                        List.of(new BlockingCriteria(new Field("FIRST_NAME", "STRING"), new Method("exact", "matcher"))),  // Valid blocking criteria
                        List.of()))  // matchingCriteria (empty in this case)
        );

        // Mock ObjectMapper behavior
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
        verify(mockResponseSpec, times(1)).body(Void.class); // Verify body(Void.class) was called on response spec
    }

    @Test
    void testUpdateAlgorithm_withMissingBlockingCriteria() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest(
                "testLabel", "testDescription", true, true,
                List.of(new Pass("passName", "description", "0.2", "0.9", null, List.of()))
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class))).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        // Expect IllegalArgumentException to be thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.updateAlgorithm(configRequest);
        });

        // Verify that the exception message matches expected output
        assertEquals("Blocking criteria cannot be null or empty", exception.getMessage());

        // Ensure no API call is made when validation fails
        verify(recordLinkageClient, never()).put();
    }


    @Test
    void testUpdateAlgorithm_withValidBounds() throws Exception {
        MatchingConfigRequest configRequest = new MatchingConfigRequest(
                "testLabel", "testDescription", true, true,
                List.of(new Pass("passName", "description", "0.2", "0.9",
                        List.of(new BlockingCriteria(new Field("LAST_NAME", "STRING"), new Method("exact", "matcher"))),  // Valid blocking criteria
                        List.of())) // matchingCriteria (empty in this case)
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
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
    }

    @Test
    void testUpdateAlgorithm_withInvalidBounds() throws Exception {
        // Setup the MatchingConfigRequest with invalid bounds (non-numeric)
        MatchingConfigRequest configRequest = new MatchingConfigRequest(
                "testLabel", "testDescription", true, true,
                List.of(new Pass("passName", "description", "invalid", "invalid",
                        List.of(new BlockingCriteria(new Field("FIRST_NAME", "STRING"), new Method("exact", "matcher"))),  // Valid blocking criteria
                        List.of())) // matchingCriteria (empty in this case)
        );

        // Mocking ObjectMapper's behavior
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        // Mock RestClient behavior
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);  // Ensuring put() returns mockRequestBodyUriSpec
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any(AlgorithmUpdateRequest.class))).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Void.class)).thenReturn(null);

        // Expecting a RuntimeException due to invalid bounds
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            algorithmService.updateAlgorithm(configRequest);
        });

        // Verify the exception message
        assertEquals("Invalid bounds values: lowerBound and upperBound must be valid numbers", exception.getMessage()); // Ensure that the correct error message is thrown

        // Ensure no API call is made when bounds are invalid
        verify(recordLinkageClient, never()).put();

    }
    @Test
    void testSaveMatchingConfigurationJsonProcessingException() throws Exception {
        // Setup
        RestClient mockRestClient = mock(RestClient.class);
        NamedParameterJdbcTemplate mockTemplate = mock(NamedParameterJdbcTemplate.class);
        AlgorithmService service = new AlgorithmService(mockRestClient, mockTemplate);
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);

        MatchingConfigRequest request = new MatchingConfigRequest("Test Label", "Test Description", true, true, List.of());

        // Simulate JsonProcessingException
        doThrow(JsonProcessingException.class).when(mockObjectMapper).writeValueAsString(any());

        // Act & Assert
        assertDoesNotThrow(() -> service.saveMatchingConfiguration(request));
    }

    @Test
    void testGetMatchingConfigurationEmptyResultDataAccessException() throws Exception {
        // Setup
        RestClient mockRestClient = mock(RestClient.class);
        NamedParameterJdbcTemplate mockTemplate = mock(NamedParameterJdbcTemplate.class);
        AlgorithmService service = new AlgorithmService(mockRestClient, mockTemplate);

        // Simulate EmptyResultDataAccessException
        when(mockTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(String.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertNull(service.getMatchingConfiguration());
    }

    @Test
    void testUpdateAlgorithmJsonProcessingException() throws Exception {
        // Setup
        RestClient mockRestClient = mock(RestClient.class);
        NamedParameterJdbcTemplate mockTemplate = mock(NamedParameterJdbcTemplate.class);
        AlgorithmService service = new AlgorithmService(mockRestClient, mockTemplate);
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);

        // Provide a valid MatchingConfigRequest with a non-empty 'passes' list and valid 'blockingCriteria'
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9",
                List.of(new BlockingCriteria(new Field("someField", "fieldName"), new Method("someMethod", "matcher"))), // Non-empty blockingCriteria with two String arguments
                List.of());
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );

        // Simulate JsonProcessingException
        doThrow(JsonProcessingException.class).when(mockObjectMapper).writeValueAsString(any());

        // Act & Assert: Expect the JsonProcessingException to be thrown
        assertDoesNotThrow(() -> service.updateAlgorithm(request));
    }



    @Test
    void testUpdateAlgorithmGeneralException(){
        // Setup
        RestClient mockRestClient = mock(RestClient.class);
        NamedParameterJdbcTemplate mockTemplate = mock(NamedParameterJdbcTemplate.class);
        AlgorithmService service = new AlgorithmService(mockRestClient, mockTemplate);

        // Provide an empty 'passes' list to trigger the IllegalArgumentException
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of()
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.updateAlgorithm(request), "Passes cannot be null or empty");
    }

}
