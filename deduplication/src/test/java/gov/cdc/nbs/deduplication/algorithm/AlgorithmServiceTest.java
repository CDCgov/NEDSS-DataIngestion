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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

class AlgorithmServiceTest {

    @Mock private RestClient recordLinkageClient;
    @Mock private NamedParameterJdbcTemplate template;
    @InjectMocks private AlgorithmService algorithmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConfigureMatching() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("test");

        // Test configureMatching logic
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);  // Correct usage for update method

        doNothing().when(recordLinkageClient).put();  // Correct usage for void methods

        algorithmService.configureMatching(request);

        verify(template, times(1)).update(anyString(), any(SqlParameterSource.class));
        verify(recordLinkageClient, times(1)).put();
    }

    @Test
    void testSaveMatchingConfiguration() throws JsonProcessingException {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("test");

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(request)).thenReturn("{\"label\":\"test\"}");  // Fix to mock writeValueAsString correctly
        doNothing().when(template).update(anyString(), any(SqlParameterSource.class));  // Correct for void method

        algorithmService.saveMatchingConfiguration(request);

        verify(objectMapper, times(1)).writeValueAsString(request);
    }

    @Test
    void testGetMatchingConfiguration() {
        String sql = "SELECT TOP 1 configuration FROM match_configuration ORDER BY add_time DESC";
        String mockConfigJson = "{\"label\":\"test\"}";

        when(template.queryForObject(eq(sql), any(MapSqlParameterSource.class), eq(String.class))).thenReturn(mockConfigJson);

        MatchingConfigRequest result = algorithmService.getMatchingConfiguration();

        assertNotNull(result);
        assertEquals("test", result.getLabel());
    }

    @Test
    void testUpdateDibbsConfigurations() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        doNothing().when(recordLinkageClient).put();  // Correct for void method

        algorithmService.updateDibbsConfigurations(request);

        verify(recordLinkageClient, times(2)).put();  // Verify PUT request is called twice
    }

    @Test
    void testSetDibbsBasicToFalse(){
        AlgorithmUpdateRequest updateRequest = new AlgorithmUpdateRequest();
        updateRequest.setLabel("dibbs-basic");

        doNothing().when(recordLinkageClient).put();  // Correct for void method
        when(template.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);  // Correct for non-void method

        algorithmService.setDibbsBasicToFalse();

        verify(recordLinkageClient, times(1)).put();  // Verify PUT request is called once
    }

    @Test
    void testUpdateAlgorithm() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        Pass pass = new Pass();
        pass.setLowerBound("0.2");
        pass.setUpperBound("0.8");
        request.setPasses(List.of(pass));

        doNothing().when(recordLinkageClient).put();  // Correct for void method

        algorithmService.updateAlgorithm(request);

        verify(recordLinkageClient, times(1)).put();  // Verify PUT request is called once
    }

    @Test
    void testUpdateAlgorithmWithInvalidBounds() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("dibbs-enhanced");

        Pass pass = new Pass();
        pass.setLowerBound("invalid");
        pass.setUpperBound("invalid");
        request.setPasses(List.of(pass));

        doNothing().when(recordLinkageClient).put();  // Correct for void method

        algorithmService.updateAlgorithm(request);

        verify(recordLinkageClient, times(1)).put();  // Verify PUT request is called once
    }
}
