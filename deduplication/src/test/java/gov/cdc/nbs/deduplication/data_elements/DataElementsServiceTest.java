package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import gov.cdc.nbs.deduplication.data_elements.exception.DataElementConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DataElementsServiceTest {

    @Mock
    private RestClient recordLinkageClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataElementsService service;

    @Mock
    private RestClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDataElementConfiguration_Success() throws Exception {
        // Mocking the JsonNode response
        JsonNode mockJsonResponse = mock(JsonNode.class);
        JsonNode passesNode = mock(JsonNode.class);
        JsonNode passNode = mock(JsonNode.class);
        JsonNode kwargsNode = mock(JsonNode.class);
        ObjectNode thresholdsNode = mock(ObjectNode.class);
        ObjectNode logOddsNode = mock(ObjectNode.class);

        // Mock the "passes" field to return a list with one pass
        when(mockJsonResponse.get("passes")).thenReturn(passesNode);
        when(passesNode.isArray()).thenReturn(true);
        when(passesNode.size()).thenReturn(1);  // Ensure that it returns a valid array
        when(passesNode.get(0)).thenReturn(passNode);  // Mock first "pass" node

        // Mock the "kwargs" and its children (thresholds and log_odds)
        when(passNode.get("kwargs")).thenReturn(kwargsNode);
        when(kwargsNode.path("thresholds")).thenReturn(thresholdsNode);
        when(kwargsNode.path("log_odds")).thenReturn(logOddsNode);

        // Mock the ObjectMapper to return the mock response
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonResponse);

        // Mock the WebClient (RestClient) and the HTTP call
        when(recordLinkageClient.get()).thenReturn(mockRequestHeadersUriSpec);  // Mock GET request
        when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersUriSpec);  // Mock URI
        when(mockRequestHeadersUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));  // Mock retrieve()
        when(mockRequestHeadersUriSpec.retrieve().body(String.class)).thenReturn("{ \"passes\": [{\"kwargs\": {\"thresholds\": {}, \"log_odds\": {}}}] }");  // Mock response body

        // Create a sample DataElementsDTO object
        DataElementsDTO dataElementsDTO = new DataElementsDTO(Map.of("firstName", new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8)));

        // Execute the method
        service.saveDataElementConfiguration(dataElementsDTO);

        // Verify that the WebClient methods were called correctly
        verify(recordLinkageClient, times(1)).get();
        verify(mockRequestHeadersUriSpec, times(1)).uri(anyString());
        verify(mockRequestHeadersUriSpec, times(1)).retrieve();
        verify(mockRequestHeadersUriSpec.retrieve(), times(1)).body(String.class);

        // Verify that the thresholds and log_odds were updated for the mock data element
        verify(thresholdsNode, times(1)).put("FIRST_NAME", 0.8); // threshold value
        verify(logOddsNode, times(1)).put("FIRST_NAME", 1.5); // logOdds value
    }



    @Test
    void testSaveDataElementConfiguration_JsonProcessingException() throws JsonProcessingException {
        // Given
        DataElementsDTO dataElementsDTO = new DataElementsDTO(Map.of("firstName", new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8)));

        when(objectMapper.readTree(anyString())).thenThrow(new JsonProcessingException("Invalid JSON") {});

        // When & Then
        DataElementConfigurationException exception = assertThrows(DataElementConfigurationException.class, () ->
                service.saveDataElementConfiguration(dataElementsDTO)
        );

        assertEquals("Failed to update algorithm configuration", exception.getMessage());
        verify(recordLinkageClient, never()).put();
    }

    @Test
    void testSaveDataElementConfiguration_ApiFailure() {
        // Mock the GET request to throw an exception
        when(recordLinkageClient.get()).thenThrow(new RuntimeException("API Failure"));

        DataElementsDTO dataElementsDTO = new DataElementsDTO(Map.of("firstName", new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8)));

        // Verify the exception
        assertThrows(RuntimeException.class, () -> {
            service.saveDataElementConfiguration(dataElementsDTO);
        });
    }
}
