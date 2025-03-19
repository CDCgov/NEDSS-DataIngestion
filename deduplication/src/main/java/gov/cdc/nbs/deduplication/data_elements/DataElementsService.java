package gov.cdc.nbs.deduplication.data_elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElement;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElements;
import gov.cdc.nbs.deduplication.data_elements.exception.DataElementConfigurationException;
import gov.cdc.nbs.deduplication.data_elements.repository.DataElementConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

    public void saveDataElementConfiguration(DataElements dataElements) {
        try {
            // Step 1: Fetch current algorithm configuration using RestClient
            JsonNode algorithmConfig = fetchAlgorithmConfiguration();

            // Step 2: Update `log_odds` and `thresholds` in `kwargs`
            JsonNode passes = algorithmConfig.path("passes");
            if (passes.isArray()) {
                for (JsonNode pass : passes) {
                    JsonNode kwargs = pass.path("kwargs");
                    updateKwargs(kwargs, dataElements);
                }
            }

            // Step 3: Save to repository
            repository.saveDataElementConfiguration(dataElements);

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
    // Does not call this function-- why?
    public void updateAlgorithmConfiguration(JsonNode algorithmConfig) throws DataElementConfigurationException {
        try {
            // Serialize the algorithmConfig to JSON
            String updatedConfigJson = objectMapper.writeValueAsString(algorithmConfig);
            log.info("Serialized Updated Algorithm Configuration: {}", updatedConfigJson);

            // Sending the request using RestClient
            var responseEntity = recordLinkageClient.put()
                    .uri("/algorithm/dibbs-enhanced")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updatedConfigJson)
                    .retrieve()
                    .toEntity(String.class);

            if (responseEntity != null) {
                log.info("Response Status Code: {}", responseEntity.getStatusCode());
                if (responseEntity.getBody() != null) {
                    log.info("Response Body: {}", responseEntity.getBody());
                }
            } else {
                log.error("Received null response from the server.");
            }

            if (responseEntity != null && !responseEntity.getStatusCode().is2xxSuccessful()) {
                log.error("Error: Failed to update algorithm configuration. Status: {}", responseEntity.getStatusCode());
                throw new DataElementConfigurationException("Failed to update algorithm configuration, status: " + responseEntity.getStatusCode());
            }

            log.info("Successfully updated algorithm configuration");
        } catch (JsonProcessingException e) {
            log.error("Error serializing algorithm configuration to JSON", e);
            throw new DataElementConfigurationException("Error serializing algorithm configuration to JSON", e);
        } catch (Exception e) {
            log.error("Failed to update algorithm configuration", e);
            throw new DataElementConfigurationException("Failed to update algorithm configuration", e);
        }
    }

    // Update kwargs with the new DataElements
    public void updateKwargs(JsonNode kwargs, DataElements dataElements) {
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

        // Map DataElements fields to API field names (?)
        updateField(thresholdsNode, logOddsNode, "firstName", dataElements.firstName());
        updateField(thresholdsNode, logOddsNode, "lastName", dataElements.lastName());
        updateField(thresholdsNode, logOddsNode, "dateOfBirth", dataElements.dateOfBirth());
        updateField(thresholdsNode, logOddsNode, "currentSex", dataElements.currentSex());
        updateField(thresholdsNode, logOddsNode, "race", dataElements.race());
        updateField(thresholdsNode, logOddsNode, "suffix", dataElements.suffix());
        updateField(thresholdsNode, logOddsNode, "streetAddress1", dataElements.streetAddress1());
        updateField(thresholdsNode, logOddsNode, "city", dataElements.city());
        updateField(thresholdsNode, logOddsNode, "state", dataElements.state());
        updateField(thresholdsNode, logOddsNode, "zip", dataElements.zip());
        updateField(thresholdsNode, logOddsNode, "county", dataElements.county());
        updateField(thresholdsNode, logOddsNode, "telephone", dataElements.telephone());
        updateField(thresholdsNode, logOddsNode, "telecom", dataElements.telecom());
        updateField(thresholdsNode, logOddsNode, "email", dataElements.email());
        updateField(thresholdsNode, logOddsNode, "accountNumber", dataElements.accountNumber());
        updateField(thresholdsNode, logOddsNode, "driversLicenseNumber", dataElements.driversLicenseNumber());
        updateField(thresholdsNode, logOddsNode, "medicaidNumber", dataElements.medicaidNumber());
        updateField(thresholdsNode, logOddsNode, "medicalRecordNumber", dataElements.medicalRecordNumber());
        updateField(thresholdsNode, logOddsNode, "medicareNumber", dataElements.medicareNumber());
        updateField(thresholdsNode, logOddsNode, "nationalUniqueIdentifier", dataElements.nationalUniqueIdentifier());
        updateField(thresholdsNode, logOddsNode, "patientExternalIdentifier", dataElements.patientExternalIdentifier());
        updateField(thresholdsNode, logOddsNode, "patientInternalIdentifier", dataElements.patientInternalIdentifier());
        updateField(thresholdsNode, logOddsNode, "personNumber", dataElements.personNumber());
        updateField(thresholdsNode, logOddsNode, "socialSecurity", dataElements.socialSecurity());
        updateField(thresholdsNode, logOddsNode, "visaPassport", dataElements.visaPassport());
        updateField(thresholdsNode, logOddsNode, "wicIdentifier", dataElements.wicIdentifier());
    }

    private void updateField(ObjectNode thresholdsNode, ObjectNode logOddsNode, String fieldName, DataElement dataElement) {
        if (dataElement.active()) {
            thresholdsNode.put(fieldName, dataElement.threshold());
            logOddsNode.put(fieldName, dataElement.logOdds());
        }
    }
}
