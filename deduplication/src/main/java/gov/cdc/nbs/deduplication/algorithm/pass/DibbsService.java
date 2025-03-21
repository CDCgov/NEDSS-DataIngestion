package gov.cdc.nbs.deduplication.algorithm.pass;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm;

@Component
public class DibbsService {

    private final RestClient client;
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    public DibbsService(
            @Qualifier("recordLinkageRestClient") RestClient client,
            @Qualifier("mpiNamedTemplate") NamedParameterJdbcTemplate template) {
        this.client = client;
        this.template = template;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    private static final String QUERY_LABEL_COUNT = """
            SELECT
                count(id)
            FROM
                algorithm
            WHERE
                label = :label
                """;

    private static final String SET_DEFAULT = """
            UPDATE algorithm
            SET
            is_default = CASE
                WHEN label = :label THEN 1
                ELSE 0
            END;
            """;

    // Saves the algorithm and ensures it is set as active
    public void save(DibbsAlgorithm algorithm) {
        if (algorithmExists(algorithm.label())) {
            update(algorithm);
        } else {
            create(algorithm);
        }

        setDefault(algorithm.label());
    }

    private boolean algorithmExists(final String label) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("label", label);
        Integer count = template.queryForObject(QUERY_LABEL_COUNT, params, Integer.class);
        return count != null && count > 0;
    }

    private void setDefault(String label) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("label", label);
        template.update(SET_DEFAULT, params);
    }

    private void update(DibbsAlgorithm algorithm) {
        try {
            String body = mapper.writeValueAsString(algorithm);
            client.put()
                    .uri("/algorithm/" + algorithm.label())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (JsonProcessingException | RestClientException e) {
            throw new PassModificationException("Failed to update DibbsAlgorithm");
        }
    }

    private void create(DibbsAlgorithm algorithm) {
        try {
            String body = mapper.writeValueAsString(algorithm);
            client.post()
                    .uri("/algorithm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (JsonProcessingException | RestClientException e) {
            throw new PassModificationException("Failed to save DibbsAlgorithm");
        }
    }

}
