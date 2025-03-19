package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.cdc.nbs.deduplication.data_elements.exception.DataElementConfigurationException;
import gov.cdc.nbs.deduplication.data_elements.repository.DataElementConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class DataElementsService {

    private static final Logger log = LoggerFactory.getLogger(DataElementsService.class);

    private final DataElementConfigurationRepository repository;
    private final RestClient recordLinkageClient;
    private final ObjectMapper objectMapper;

    public DataElementsService(
            DataElementConfigurationRepository repository,
            @Qualifier("recordLinkageRestClient") RestClient recordLinkageClient,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.recordLinkageClient = recordLinkageClient;
        this.objectMapper = objectMapper;
    }

    public void saveDataElementConfiguration(DataElementsDTO dataElementsDTO) {
        try {
            // Step 1: Fetch current algorithm configuration using RestClient
            String algorithmConfigJson = recordLinkageClient.get()
                    .uri("/algorithm/dibbs-enhanced")
                    .retrieve()
                    .body(String.class);

            if (algorithmConfigJson == null) {
                throw new DataElementConfigurationException("Failed to retrieve current algorithm configuration.");
            }

            JsonNode algorithmConfig = objectMapper.readTree(algorithmConfigJson);

            // Step 2: Update `log_odds` and `thresholds` in `kwargs`
            JsonNode passes = algorithmConfig.path("passes");
            if (passes.isArray()) {
                for (JsonNode pass : passes) {
                    JsonNode kwargs = pass.path("kwargs");
                    updateKwargs(kwargs, dataElementsDTO.dataElements());
                }
            }

            // Step 3: Save to repository
            repository.saveDataElementConfiguration(dataElementsDTO);

            // Step 4: Log and send the updated configuration to the PUT API using RestClient
            String updatedConfigJson = objectMapper.writeValueAsString(algorithmConfig);

            recordLinkageClient.put()
                    .uri("/algorithm/dibbs-enhanced")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updatedConfigJson)
                    .retrieve()
                    .body(Void.class);

        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON while updating algorithm configuration", e);
        } catch (Exception e) {
            throw new DataElementConfigurationException("Failed to update algorithm configuration", e);
        }
    }

    private void updateKwargs(JsonNode kwargs, Map<String, DataElementsDTO.DataElementConfig> dataElements) {
        // Map DataElementsDTO fields to API field names
        Map<String, String> fieldMapping = Map.of(
                "firstName", "FIRST_NAME",
                "lastName", "LAST_NAME",
                "dateOfBirth", "BIRTHDATE",
                "email", "EMAIL"
        );

        ObjectNode thresholdsNode = (ObjectNode) kwargs.path("thresholds");
        ObjectNode logOddsNode = (ObjectNode) kwargs.path("log_odds");

        // Remove fields that are not active
        thresholdsNode.retain();
        logOddsNode.retain();

        dataElements.forEach((key, value) -> {
            if (fieldMapping.containsKey(key)) {
                String apiField = fieldMapping.get(key);
                if (value.active()) {
                    // Update only active fields
                    thresholdsNode.put(apiField, value.threshold());
                    logOddsNode.put(apiField, value.logOdds());
                }
            }
        });

        log.info("Updated thresholds: {}", thresholdsNode);
        log.info("Updated log_odds: {}", logOddsNode);
    }
}