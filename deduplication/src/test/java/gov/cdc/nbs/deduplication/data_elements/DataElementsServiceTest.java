package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import gov.cdc.nbs.deduplication.data_elements.exception.DataElementConfigurationException;
import gov.cdc.nbs.deduplication.data_elements.repository.DataElementConfigurationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DataElementsServiceTest {

    @Mock
    private DataElementConfigurationRepository repository;

    @InjectMocks
    private DataElementsService service;

    @Test
    void testSaveDataElementConfiguration() throws DataElementConfigurationException, JsonProcessingException {
        // Given
        DataElementsDTO dataElementsDTO = new DataElementsDTO(Map.of("firstName", new DataElementsDTO.DataElementConfig(true, 1.5, 0.5, 0.8)));

        // When
        service.saveDataElementConfiguration(dataElementsDTO);

        // Then
        Mockito.verify(repository, Mockito.times(1)).saveDataElementConfiguration(dataElementsDTO);
    }
}