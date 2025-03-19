package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import gov.cdc.nbs.deduplication.data_elements.exception.DataElementConfigurationException;
import gov.cdc.nbs.deduplication.data_elements.repository.DataElementConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DataElementsServiceTest {

    @Mock
    private RestClient recordLinkageClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataElementsService dataElementsService;

    @Mock
    private DataElementConfigurationRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchAlgorithmConfiguration_success() throws Exception {
        // Arrange
        String algorithmConfigJson = "{ \"passes\": [] }";
        JsonNode expectedNode = mock(JsonNode.class);

        // Mock RestClient GET chain
        RestClient.RequestHeadersUriSpec mockUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(recordLinkageClient.get()).thenReturn(mockUriSpec);
        when(mockUriSpec.uri("/algorithm/dibbs-enhanced")).thenReturn(mockUriSpec);
        when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec); // Retrieve method

        when(mockResponseSpec.body(String.class)).thenReturn(algorithmConfigJson);

        when(objectMapper.readTree(algorithmConfigJson)).thenReturn(expectedNode);

        // Act
        JsonNode result = dataElementsService.fetchAlgorithmConfiguration();

        // Assert
        assertNotNull(result);
        assertEquals(expectedNode, result);
    }

    @Test
    void testFetchAlgorithmConfiguration_failure() throws DataElementConfigurationException {
        // Arrange
        RestClient.RequestHeadersUriSpec mockUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(recordLinkageClient.get()).thenReturn(mockUriSpec);
        when(mockUriSpec.uri("/algorithm/dibbs-enhanced")).thenThrow(new RuntimeException("API error"));

        // Act & Assert
        DataElementConfigurationException exception = assertThrows(DataElementConfigurationException.class, () -> {
            dataElementsService.fetchAlgorithmConfiguration();
        });
        assertEquals("Failed to retrieve current algorithm configuration.", exception.getMessage());
    }

    @Test
    void testUpdateAlgorithmConfiguration_success() throws Exception {
        // Arrange
        JsonNode mockAlgorithmConfig = mock(JsonNode.class);
        String updatedConfigJson = "{ \"passes\": [] }";

        when(objectMapper.writeValueAsString(mockAlgorithmConfig)).thenReturn(updatedConfigJson);

        // Mock RestClient PUT chain
        RestClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(recordLinkageClient.put()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("/algorithm/dibbs-enhanced")).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(updatedConfigJson)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));

        // Act
        dataElementsService.updateAlgorithmConfiguration(mockAlgorithmConfig);

        // Assert
        verify(recordLinkageClient, times(1)).put();
    }

    @Test
    void testUpdateKwargs() {
        // Arrange
        ObjectMapper objectMapperTest = new ObjectMapper();
        ObjectNode kwargs = objectMapperTest.createObjectNode();

        kwargs.putObject("thresholds");
        kwargs.putObject("log_odds");

        Map<String, DataElementsDTO.DataElementConfig> dataElements = new HashMap<>();

        // Mock active data element
        DataElementsDTO.DataElementConfig activeConfig = new DataElementsDTO.DataElementConfig(true, 0.5, 1.0, 0.3);
        DataElementsDTO.DataElementConfig inactiveConfig = new DataElementsDTO.DataElementConfig(false, 0.7, 1.2, 0.3);

        // Map data elements to API field names
        dataElements.put("firstName", activeConfig);  // Should be updated
        dataElements.put("lastName", inactiveConfig); // Should NOT be updated

        // Act
        dataElementsService.updateKwargs(kwargs, dataElements);

        // Extract threshold and log_odds nodes
        JsonNode thresholdsNode = kwargs.path("thresholds");
        JsonNode logOddsNode = kwargs.path("log_odds");

        // Assert: Check values for active elements
        assertEquals(0.3, thresholdsNode.get("FIRST_NAME").asDouble(), 0.001);
        assertEquals(1.0, logOddsNode.get("FIRST_NAME").asDouble(), 0.001);

        // Assert: Ensure inactive elements are NOT updated
        assertFalse(thresholdsNode.has("LAST_NAME"));
        assertFalse(logOddsNode.has("LAST_NAME"));
    }

    @Test
    void testUpdateKwargs_handlesNonObjectNode() {
        JsonNode invalidNode = mock(JsonNode.class);
        Map<String, DataElementsDTO.DataElementConfig> dataElements = new HashMap<>();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dataElementsService.updateKwargs(invalidNode, dataElements);
        });

        assertEquals("kwargs must be an ObjectNode", exception.getMessage());
    }

    @Test
    void testUpdateKwargs_createsMissingNodes() {
        ObjectNode kwargs = new ObjectMapper().createObjectNode();
        Map<String, DataElementsDTO.DataElementConfig> dataElements = new HashMap<>();

        dataElementsService.updateKwargs(kwargs, dataElements);

        assertTrue(kwargs.has("thresholds"));
        assertTrue(kwargs.has("log_odds"));
    }

    @Test
    void testUpdateKwargs_noChangesWithEmptyDataElements() {
        ObjectNode kwargs = new ObjectMapper().createObjectNode();
        kwargs.putObject("thresholds");
        kwargs.putObject("log_odds");

        dataElementsService.updateKwargs(kwargs, Collections.emptyMap());

        assertEquals(0, kwargs.get("thresholds").size());
        assertEquals(0, kwargs.get("log_odds").size());
    }

    @Test
    void testUpdateKwargs_withFieldMapping() {
        // Arrange
        ObjectMapper objectMapperTest = new ObjectMapper();
        ObjectNode kwargs = objectMapperTest.createObjectNode(); // Ensure it's a valid ObjectNode

        kwargs.set("thresholds", objectMapperTest.createObjectNode());
        kwargs.set("log_odds", objectMapperTest.createObjectNode());

        Map<String, DataElementsDTO.DataElementConfig> dataElements = new HashMap<>();

        // Mock data elements
        DataElementsDTO.DataElementConfig activeConfig = new DataElementsDTO.DataElementConfig(true, 0.5, 1.0, 0.3);
        DataElementsDTO.DataElementConfig inactiveConfig = new DataElementsDTO.DataElementConfig(false, 0.7, 1.2, 0.3);
        DataElementsDTO.DataElementConfig notMappedConfig = new DataElementsDTO.DataElementConfig(true, 0.5, 1.0, 0.3); // Key not in mapping

        // Map data elements to API field names
        dataElements.put("firstName", activeConfig);  // Should be updated (active and mapped)
        dataElements.put("lastName", inactiveConfig); // Should NOT be updated (inactive)
        dataElements.put("notMapped", notMappedConfig); // Should NOT be updated (not in field mapping)

        // Act
        dataElementsService.updateKwargs(kwargs, dataElements);

        // Extract threshold and log_odds nodes
        JsonNode thresholdsNode = kwargs.path("thresholds");
        JsonNode logOddsNode = kwargs.path("log_odds");

        // Assert: Check values for active elements (firstName should be updated)
        assertEquals(0.3, thresholdsNode.get("FIRST_NAME").asDouble(), 0.001);
        assertEquals(1.0, logOddsNode.get("FIRST_NAME").asDouble(), 0.001);

        // Assert: Ensure inactive elements (lastName) are NOT updated
        assertFalse(thresholdsNode.has("LAST_NAME"));
        assertFalse(logOddsNode.has("LAST_NAME"));

        // Assert: Ensure non-mapped elements (notMapped) are NOT updated
        assertFalse(thresholdsNode.has("NOT_MAPPED"));
        assertFalse(logOddsNode.has("NOT_MAPPED"));
    }

    @Test
    void testSaveDataElementConfiguration_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        DataElementsDTO dataElementsDTO = mock(DataElementsDTO.class);

        when(objectMapper.readTree(anyString())).thenThrow(new JsonProcessingException("Invalid JSON") {});

        // Act & Assert
        assertThrows(DataElementConfigurationException.class, () -> {
            dataElementsService.saveDataElementConfiguration(dataElementsDTO);
        });
    }

    @Test
    void testSaveDataElementConfiguration_ApiFailure() {
        when(recordLinkageClient.get()).thenThrow(new RuntimeException("API Failure"));

        DataElementsDTO dataElementsDTO = mock(DataElementsDTO.class);

        // Verify the exception
        assertThrows(RuntimeException.class, () -> {
            dataElementsService.saveDataElementConfiguration(dataElementsDTO);
        });
    }

    @Test
    void testSaveDataElementConfiguration_fetchFailure() {
        when(recordLinkageClient.get()).thenThrow(new RuntimeException("API failure"));
        DataElementsDTO dataElementsDTO = mock(DataElementsDTO.class);

        assertThrows(DataElementConfigurationException.class, () -> {
            dataElementsService.saveDataElementConfiguration(dataElementsDTO);
        });
    }

    @Test
    void testSaveDataElementConfiguration_updateFailure() throws JsonProcessingException {
        String algorithmJson = "{ \"passes\": [] }";
        JsonNode mockAlgorithmConfig = new ObjectMapper().readTree(algorithmJson);

        when(recordLinkageClient.get()).thenReturn(mock(RestClient.RequestHeadersUriSpec.class));
        when(recordLinkageClient.get().uri("/algorithm/dibbs-enhanced")).thenReturn(mock(RestClient.RequestHeadersUriSpec.class));
        when(recordLinkageClient.get().uri("/algorithm/dibbs-enhanced").retrieve()).thenReturn(mock(RestClient.ResponseSpec.class));
        when(recordLinkageClient.get().uri("/algorithm/dibbs-enhanced").retrieve().body(String.class)).thenReturn(algorithmJson);
        when(objectMapper.readTree(algorithmJson)).thenReturn(mockAlgorithmConfig);
        doThrow(new RuntimeException("PUT API failure")).when(recordLinkageClient).put();

        DataElementsDTO dataElementsDTO = mock(DataElementsDTO.class);

        assertThrows(DataElementConfigurationException.class, () -> {
            dataElementsService.saveDataElementConfiguration(dataElementsDTO);
        });
    }

}
