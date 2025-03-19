package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.ExportConfigRecord;
import gov.cdc.nbs.deduplication.algorithm.exception.ExportConfigurationException;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    private final ObjectMapper objectMapper;

    // Constructor to inject ObjectMapper
    public AlgorithmService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // This method will process the UI data and convert it into JSON
    public byte[] generateExportJson(ExportConfigRecord exportConfig) {
        try {
            return objectMapper.writeValueAsBytes(exportConfig);
        } catch (JsonProcessingException e) {
            throw new ExportConfigurationException("Error while exporting configuration", e);
        }
    }
}