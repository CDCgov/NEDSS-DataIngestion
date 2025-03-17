package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.cdc.nbs.deduplication.data_elements.exception.DataElementConfigurationException;
import gov.cdc.nbs.deduplication.data_elements.repository.DataElementConfigurationRepository;
import org.springframework.stereotype.Service;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;

@Service
public class DataElementsService {

    private final DataElementConfigurationRepository repository;

    public DataElementsService(DataElementConfigurationRepository repository) {
        this.repository = repository;
    }

    public void saveDataElementConfiguration(DataElementsDTO dataElementsDTO) {
        try {
            // Call the repository to save the configuration
            repository.saveDataElementConfiguration(dataElementsDTO);
        } catch (JsonProcessingException e) {
            // Handle the exception: log, wrap it, or rethrow as a runtime exception
            throw new DataElementConfigurationException("Failed to process JSON while saving data element configuration", e);
        }
    }
}
