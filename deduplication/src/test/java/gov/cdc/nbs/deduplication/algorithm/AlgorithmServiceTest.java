package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmConfigMapper;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
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
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmServiceTest {

    @Mock
    private RestClient recordLinkageClient;
    @Mock
    private NamedParameterJdbcTemplate template;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private AlgorithmService algorithmService;
    @Mock
    private AlgorithmConfigMapper algorithmConfigMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetDibbsBasicToFalse() {
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        algorithmService.setDibbsBasicToFalse();

        verify(recordLinkageClient, times(1)).put();
    }

    @Test
    void testGetMatchingConfiguration() throws JsonProcessingException {
        String sql = "SELECT TOP 1 configuration FROM match_configuration ORDER BY add_time DESC";
        String mockConfigJson = "{\"label\":\"test\"}";

        when(template.queryForObject(eq(sql), any(MapSqlParameterSource.class), eq(String.class))).thenReturn(mockConfigJson);
        when(objectMapper.readValue(mockConfigJson, MatchingConfigRequest.class))
                .thenReturn(new MatchingConfigRequest("test", "Test Description", true, true, List.of()));

        List<Pass> result = algorithmService.getMatchingConfiguration();

        assertNotNull(result);
        assertEquals(0, result.size()); // Since the mock config has no passes
    }

    @Test
    void testConfigureMatching() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);
        List<Pass> passes = List.of(new Pass("TestPass", "Description", "0.1", "0.9", blockingCriteria, List.of()));

        MatchingConfigRequest request = new MatchingConfigRequest("testLabel", "testDescription", true, true, passes);

        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        algorithmService.configureMatching(request);

        verify(template, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    void testUpdateDibbsConfigurations() throws JsonProcessingException {
        AlgorithmService spyAlgorithmService = spy(algorithmService);

        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);
        List<Pass> passes = List.of(new Pass("passName", "description", "0.2", "0.9", blockingCriteria, List.of()));

        MatchingConfigRequest configRequest = new MatchingConfigRequest("testLabel", "testDescription", true, true, passes);

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"label\": \"dibbs-enhanced\"}");

        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(any())).thenReturn(mockRequestBodyUriSpec);

        spyAlgorithmService.updateDibbsConfigurations(configRequest);

        verify(spyAlgorithmService, times(1)).setDibbsBasicToFalse();
        verify(spyAlgorithmService, times(1)).updateAlgorithm(configRequest);
    }

    @Test
    void testUpdateAlgorithm_withMissingBlockingCriteria() {
        MatchingConfigRequest configRequest = new MatchingConfigRequest(
                "testLabel", "testDescription", true, true,
                List.of(new Pass("passName", "description", "0.2", "0.9", null, List.of()))
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.updateAlgorithm(configRequest);
        });

        assertEquals("Blocking criteria cannot be null or empty", exception.getMessage());
        verify(recordLinkageClient, never()).put();
    }

    @Test
    void testGetMatchingConfiguration_EmptyResult() {
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), eq(String.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        List<Pass> result = algorithmService.getMatchingConfiguration();
        assertNotNull(result);
        assertEquals(0, result.size()); // Should return an empty list when DB is empty
    }

    @Test
    void testUpdateAlgorithmJsonProcessingException() throws Exception {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);
        Pass pass = new Pass("TestPass", "Description", "0.1", "0.9", blockingCriteria, List.of());

        MatchingConfigRequest request = new MatchingConfigRequest("Test Label", "Test Description", true, true, List.of(pass));

        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());

        assertDoesNotThrow(() -> algorithmService.updateAlgorithm(request));
    }

    @Test
    void testFetchDefaultConfiguration_Success() {
        when(recordLinkageClient.get().uri("/algorithm/dibbs-enhanced").retrieve().body(AlgorithmUpdateRequest.class))
                .thenReturn(new AlgorithmUpdateRequest("defaultLabel", "defaultDesc", true, true, new Double[]{0.1, 0.9}, List.of()));

        when(algorithmConfigMapper.mapAlgorithmUpdateRequestToMatchingConfigRequest(any()))
                .thenReturn(new MatchingConfigRequest("defaultLabel", "defaultDesc", true, true, List.of()));

        List<Pass> result = algorithmService.getMatchingConfiguration();

        assertNotNull(result);
        assertEquals(0, result.size()); // Since no passes are defined
    }

    @Test
    void testFetchDefaultConfiguration_ApiFailure() {
        when(recordLinkageClient.get().uri("/algorithm/dibbs-enhanced").retrieve().body(AlgorithmUpdateRequest.class))
                .thenThrow(new RuntimeException("API error"));

        List<Pass> result = algorithmService.getMatchingConfiguration();

        assertNotNull(result);
        assertEquals(0, result.size()); // Should return empty list on API failure
    }
}

