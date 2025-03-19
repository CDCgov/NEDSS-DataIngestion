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
            JsonNode algorithmConfig = fetchAlgorithmConfiguration();

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
            updateAlgorithmConfiguration(algorithmConfig);

        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON while updating algorithm configuration", e);
        } catch (Exception e) {
            throw new DataElementConfigurationException("Failed to update algorithm configuration", e);
        }
    }

    // Step 1: Fetch current algorithm configuration
    public JsonNode fetchAlgorithmConfiguration() throws DataElementConfigurationException, JsonProcessingException {
        try {
            String algorithmConfigJson = recordLinkageClient.get()
                    .uri("/algorithm/dibbs-enhanced")
                    .retrieve()
                    .body(String.class);

            if (algorithmConfigJson == null) {
                throw new DataElementConfigurationException("Failed to retrieve current algorithm configuration.");
            }

            return objectMapper.readTree(algorithmConfigJson);
        } catch (RuntimeException e) {
            throw new DataElementConfigurationException("Failed to retrieve current algorithm configuration.", e);
        }
    }

    // Step 4: Send updated configuration to the PUT API
    public void updateAlgorithmConfiguration(JsonNode algorithmConfig) throws DataElementConfigurationException, JsonProcessingException {
        String updatedConfigJson = objectMapper.writeValueAsString(algorithmConfig);

        recordLinkageClient.put()
                .uri("/algorithm/dibbs-enhanced")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(updatedConfigJson)
                .retrieve()
                .body(Void.class);
    }

    public void updateKwargs(JsonNode kwargs, Map<String, DataElementsDTO.DataElementConfig> dataElements) {
        if (!(kwargs instanceof ObjectNode)) {
            throw new IllegalArgumentException("kwargs must be an ObjectNode");
        }
        ObjectNode kwargsObject = (ObjectNode) kwargs;

        // Get or create the thresholds and log_odds nodes
        ObjectNode thresholdsNode = (ObjectNode) kwargsObject.get("thresholds");
        if (thresholdsNode == null) {
            thresholdsNode = kwargsObject.putObject("thresholds"); // Ensure it's created
        }

        ObjectNode logOddsNode = (ObjectNode) kwargsObject.get("log_odds");
        if (logOddsNode == null) {
            logOddsNode = kwargsObject.putObject("log_odds"); // Ensure it's created
        }

        // Map DataElementsDTO fields to API field names
        Map<String, String> fieldMapping = Map.ofEntries(
                Map.entry("firstName", "FIRST_NAME"),
                Map.entry("lastName", "LAST_NAME"),
                Map.entry("dateOfBirth", "BIRTHDATE"),
                Map.entry("currentSex", "SEX"),
                Map.entry("race", "RACE"),
                Map.entry("suffix", "SUFFIX"),
                Map.entry("streetAddress1", "ADDRESS"),
                Map.entry("city", "CITY"),
                Map.entry("state", "STATE"),
                Map.entry("zip", "ZIPCODE"),
                Map.entry("county", "COUNTY"),
                Map.entry("telephone", "PHONE"),
                Map.entry("telecom", "PHONE"),
                Map.entry("email", "EMAIL"),
                Map.entry("accountNumber", "AN"),
                Map.entry("driversLicenseNumber", "DL"),
                Map.entry("medicaidNumber", "MA"),
                Map.entry("medicalRecordNumber", "MR"),
                Map.entry("medicareNumber", "MC"),
                Map.entry("nationalUniqueIdentifier", "NI"),
                Map.entry("patientExternalIdentifier", "PI"),
                Map.entry("patientInternalIdentifier", "PT"),
                Map.entry("personNumber", "PN"),
                Map.entry("socialSecurity", "SS"),
                Map.entry("visaPassport", "VS"),
                Map.entry("wicIdentifier", "WC")
        );

        ObjectNode finalThresholdsNode = thresholdsNode;
        ObjectNode finalLogOddsNode = logOddsNode;
        dataElements.forEach((key, value) -> {
            if (fieldMapping.containsKey(key)) {
                String apiField = fieldMapping.get(key);
                if (value.active()) {
                    finalThresholdsNode.put(apiField, value.threshold());
                    finalLogOddsNode.put(apiField, value.logOdds());
                }
            }
        });
    }
}