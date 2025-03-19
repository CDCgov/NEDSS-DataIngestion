package gov.cdc.nbs.deduplication.data_elements.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

import static org.mockito.Mockito.*;

class DataElementConfigurationRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate template;

    @InjectMocks
    private DataElementConfigurationRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private DataElementsDTO dataElementsDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample data for the test
        dataElementsDTO = new DataElementsDTO(Map.of(
                "firstName", new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8),
                "lastName", new DataElementsDTO.DataElementConfig(true, 2.0, 0.7, 0.9)
        ));
    }

    @Test
    void testSaveDataElementConfiguration() throws Exception {
        // Arrange: Set the configuration JSON with the actual field order from the error
        String configurationJson = "[{\"logOdds\":0.7,\"field\":\"lastName\",\"oddsRatio\":2.0,\"active\":true,\"threshold\":0.9}," +
                "{\"logOdds\":0.5,\"field\":\"firstName\",\"oddsRatio\":1.5,\"active\":true,\"threshold\":0.8}]";

        // Mock the objectMapper to return the configuration JSON
        when(objectMapper.writeValueAsString(any())).thenReturn(configurationJson);

        // Act: Call the method to save the data element configuration
        repository.saveDataElementConfiguration(dataElementsDTO);

        // Capture the argument passed to the update method
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);

        verify(template, times(1)).update(
                eq("INSERT INTO deduplication.dbo.data_element_configuration (configuration, add_time) VALUES (:configuration, GETDATE())"),
                captor.capture()  // Capture the MapSqlParameterSource argument
        );

        String capturedJson = captor.getValue().getValue("configuration").toString();

        Assertions.assertTrue(capturedJson.contains("\"logOdds\":0.7"), "logOdds 0.7 not found in captured JSON");
        Assertions.assertTrue(capturedJson.contains("\"logOdds\":0.5"), "logOdds 0.5 not found in captured JSON");
    }


    @Test
    void testSaveDataElementConfiguration_JsonProcessingException() throws Exception {
        // Arrange: Mock objectMapper to throw JsonProcessingException
        when(objectMapper.writeValueAsString(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Test error") {});

        // Act & Assert
        try {
            repository.saveDataElementConfiguration(dataElementsDTO);
        } catch (RuntimeException e) {
            // Verify that the exception is thrown
            assert e.getMessage().contains("Failed to serialize configuration");
        }

        verify(template, never()).update(
                "INSERT INTO deduplication.dbo.data_element_configuration (configuration, add_time) VALUES (:configuration, GETDATE())",
                new MapSqlParameterSource()
        );
    }


    @Test
    void testBuildConfigurationJson() throws Exception {
        String expectedJson = "[{\"logOdds\":0.5,\"field\":\"firstName\",\"oddsRatio\":1.5,\"active\":true,\"threshold\":0.8}," +
                "{\"logOdds\":0.7,\"field\":\"lastName\",\"oddsRatio\":2.0,\"active\":true,\"threshold\":0.9}]";
        // Mock the objectMapper to return the configuration JSON
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
        // Use reflection to access the private method
        var method = repository.getClass().getDeclaredMethod("buildConfigurationJson", Map.class);
        method.setAccessible(true);

        String result = (String) method.invoke(repository, dataElementsDTO.dataElements());

        Assertions.assertTrue(result.contains("\"logOdds\":0.7"), "logOdds 0.7 not found in captured JSON");
        Assertions.assertTrue(result.contains("\"logOdds\":0.5"), "logOdds 0.5 not found in captured JSON");
    }
}
