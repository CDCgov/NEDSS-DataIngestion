package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Disabled("Ignoring test due to issue with mocking WebClient")
    @Test
    void testSaveDataElementConfiguration_Success() throws Exception {
        JsonNode mockJsonResponse = mock(JsonNode.class);
        JsonNode passesNode = mock(JsonNode.class);

        when(mockJsonResponse.get("passes")).thenReturn(passesNode);
        when(passesNode.isArray()).thenReturn(true);
        when(passesNode.size()).thenReturn(0);

        when(objectMapper.readTree(anyString())).thenReturn(mockJsonResponse);

        when(recordLinkageClient.get()).thenReturn(mockRequestHeadersUriSpec);  // Mock get()
        when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersUriSpec);  // Mock uri()
        when(mockRequestHeadersUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));  // Mock retrieve()
        when(mockRequestHeadersUriSpec.retrieve().body(String.class)).thenReturn("{ \"passes\": [] }");  // Mock body()

        DataElementsDTO dataElementsDTO = new DataElementsDTO(Map.of("firstName", new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8)));

        service.saveDataElementConfiguration(dataElementsDTO);

        verify(recordLinkageClient, times(1)).get();
        verify(mockRequestHeadersUriSpec, times(1)).uri(anyString());
        verify(mockRequestHeadersUriSpec, times(1)).retrieve();
        verify(mockRequestHeadersUriSpec.retrieve(), times(1)).body(String.class);

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
