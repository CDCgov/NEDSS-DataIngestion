package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.dto.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.dto.Kwargs;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmConfigMapper;
import gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmRequestMapper;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AlgorithmService {
    private final RestClient recordLinkageClient;
    private final NamedParameterJdbcTemplate template;
    private static final Logger log = LoggerFactory.getLogger(AlgorithmService.class);

    public AlgorithmService(
            @Qualifier("recordLinkageRestClient") final RestClient recordLinkageClient,
            @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
        this.recordLinkageClient = recordLinkageClient;
        this.template = template;
    }

    public void configureMatching(MatchingConfigRequest request) {
        saveMatchingConfiguration(request);
        updateAlgorithm(request);
    }

    public void saveMatchingConfiguration(MatchingConfigRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonConfig = objectMapper.writeValueAsString(request);
            String sql = "INSERT INTO match_configuration (configuration) VALUES (:configuration)";
            SqlParameterSource params = new MapSqlParameterSource().addValue("configuration", jsonConfig);
            template.update(sql, params);
        } catch (Exception e) {
            log.error("Unexpected error while saving matching configuration", e);
        }
    }

    public List<Pass> getMatchingConfiguration() {
        String sql = "SELECT TOP 1 configuration FROM match_configuration ORDER BY add_time DESC";
        try {
            String jsonConfig = template.queryForObject(sql, new MapSqlParameterSource(), String.class);
            log.info("Fetched JSON Config: {}", jsonConfig);

            if (jsonConfig == null || jsonConfig.isEmpty()) {
                log.info("Config was null or empty, fetching default configuration.");
                return fetchDefaultConfiguration();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            MatchingConfigRequest configRequest = objectMapper.readValue(jsonConfig, MatchingConfigRequest.class);
            log.info("Parsed MatchingConfigRequest: {}", configRequest);

            return configRequest.passes() != null ? configRequest.passes() : List.of();
        } catch (EmptyResultDataAccessException e) {
            log.warn("No matching configuration found in database. Fetching default.");
            return fetchDefaultConfiguration();
        } catch (Exception e) {
            log.error("Error retrieving matching configuration", e);
            return List.of();
        }
    }


    public void updateDibbsConfigurations(MatchingConfigRequest configRequest) {
        setDibbsBasicToFalse();
        updateAlgorithm(configRequest);
    }

    public void setDibbsBasicToFalse() {
        try {
            AlgorithmUpdateRequest updateRequest = new AlgorithmUpdateRequest(
                    "dibbs-basic",
                    "The DIBBs Default Algorithm. Based on field experimentation and " +
                            "statistical analysis, this deterministic two-pass algorithm combines " +
                            "geographical and personal information to maximize linkage quality " +
                            "while minimizing false positives",
                    false,
                    true,
                    new Double[]{0.0, 1.0},
                    List.of(new AlgorithmPass(
                            List.of("BIRTHDATE", "ADDRESS", "ZIP"),
                            List.of(new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match")),
                            "func:recordlinker.linking.matchers.rule_match",
                            new Kwargs(null, null, null, null)
                    ))
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(updateRequest);
            log.info("Sending PUT request to /algorithm/dibbs-basic: {}", jsonRequest);

            recordLinkageClient.put()
                    .uri("/algorithm/dibbs-basic")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updateRequest)
                    .retrieve()
                    .body(Void.class);

            log.info("Dibbs-basic configuration set to is_default = false.");
        } catch (JsonProcessingException e) {
            log.error("Error converting update request to JSON for dibbs-basic", e);
        } catch (Exception e) {
            log.error("Error while updating dibbs-basic configuration", e);
        }
    }

    public void updateAlgorithm(MatchingConfigRequest configRequest) {
        if (configRequest.passes() == null || configRequest.passes().isEmpty()) {
            throw new IllegalArgumentException("Passes cannot be null or empty");
        }

        for (Pass pass : configRequest.passes()) {
            if (pass.blockingCriteria() == null || pass.blockingCriteria().isEmpty()) {
                throw new IllegalArgumentException("Blocking criteria cannot be null or empty");
            }

            // Validate bounds for each pass
            try {
                Double.parseDouble(pass.lowerBound());
                Double.parseDouble(pass.upperBound());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid bounds values: lowerBound and upperBound must be valid numbers", e);
            }
        }

        AlgorithmUpdateRequest algorithmRequest = AlgorithmRequestMapper.mapToAlgorithmRequest(
                new MatchingConfiguration(
                        null,
                        configRequest.label(),
                        configRequest.description(),
                        configRequest.isDefault(),
                        configRequest.passes(),
                        new Double[]{0.0, 1.0}
                )
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(algorithmRequest);
            log.info("Sending PUT request to /algorithm/dibbs-enhanced: {}", jsonRequest);

            recordLinkageClient.put()
                    .uri("/algorithm/dibbs-enhanced")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(algorithmRequest)
                    .retrieve()
                    .body(Void.class);

            log.info("Algorithm updated successfully.");
        } catch (Exception e) {
            log.error("Failed to update algorithm", e);
        }
    }

    private List<Pass> fetchDefaultConfiguration() {
        try {
            AlgorithmUpdateRequest response = recordLinkageClient.get()
                    .uri("/algorithm/dibbs-enhanced")
                    .retrieve()
                    .body(AlgorithmUpdateRequest.class);

            MatchingConfigRequest configRequest = AlgorithmConfigMapper.mapAlgorithmUpdateRequestToMatchingConfigRequest(response);
            return configRequest.passes();
        } catch (Exception e) {
            log.error("Failed to fetch default algorithm configuration", e);
            return List.of();
        }
    }

}
