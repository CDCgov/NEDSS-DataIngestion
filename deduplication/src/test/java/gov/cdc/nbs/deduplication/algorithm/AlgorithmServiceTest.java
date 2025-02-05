package gov.cdc.nbs.deduplication.algorithm;

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
}
