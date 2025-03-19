package gov.cdc.nbs.deduplication.data_elements.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class DataElementsDTOTest {

    @Test
    void testDataElementsDTOConstructorAndGetters() {
        // Arrange
        DataElementsDTO.DataElementConfig config = new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8);
        Map<String, DataElementsDTO.DataElementConfig> dataElementsMap = Map.of("firstName", config);

        // Act
        DataElementsDTO dto = new DataElementsDTO(dataElementsMap);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.dataElements().size());
        assertTrue(dto.dataElements().containsKey("firstName"));
        assertEquals(config, dto.dataElements().get("firstName"));
    }

    @Test
    void testDataElementConfigFields() {
        // Arrange
        DataElementsDTO.DataElementConfig config = new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8);

        // Act & Assert
        assertTrue(config.active());
        assertEquals(1.5, config.oddsRatio());
        assertEquals(0.5, config.logOdds());
        assertEquals(0.8, config.threshold());
    }

    @Test
    void testDataElementsDTOEquality() {
        // Arrange
        DataElementsDTO.DataElementConfig config1 = new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8);
        DataElementsDTO.DataElementConfig config2 = new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8);
        Map<String, DataElementsDTO.DataElementConfig> map1 = Map.of("firstName", config1);
        Map<String, DataElementsDTO.DataElementConfig> map2 = Map.of("firstName", config2);

        // Act
        DataElementsDTO dto1 = new DataElementsDTO(map1);
        DataElementsDTO dto2 = new DataElementsDTO(map2);

        // Assert
        assertEquals(dto1, dto2);
    }

    @Test
    void testEmptyDataElementsDTO() {
        // Arrange
        DataElementsDTO dto = new DataElementsDTO(Map.of());

        // Act & Assert
        assertNotNull(dto);
        assertTrue(dto.dataElements().isEmpty());
    }
}
