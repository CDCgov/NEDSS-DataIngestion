package gov.cdc.nbs.deduplication.algorithm.pass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.DataElementsService;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.AlgorithmException;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

@Component
public class PassService {
    private final NamedParameterJdbcTemplate template;
    private final DataElementsService dataElementsService;
    private final DibbsService dibbsService;
    private final ObjectMapper mapper;
    private final AlgorithmMapper algorithmMapper = new AlgorithmMapper("nbs");

    public PassService(
            @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template,
            final DataElementsService dataElementsService,
            final DibbsService dibbsService,
            final ObjectMapper mapper) {
        this.template = template;
        this.dataElementsService = dataElementsService;
        this.dibbsService = dibbsService;
        this.mapper = mapper;
    }

    static final String SELECT_CURRENT_CONFIG = """
            SELECT TOP 1 configuration
            FROM match_configuration
            ORDER BY add_time DESC
            """;

    static final String INSERT_CONFIG = """
            INSERT INTO match_configuration (configuration)
            VALUES (:configuration)
            """;

    // retrieves the active algorithm from the deduplication database.
    // If none is present, return an empty Algorithm
    public Algorithm getCurrentAlgorithm() {
        List<String> results = template.getJdbcTemplate().queryForList(SELECT_CURRENT_CONFIG, String.class);
        if (results.isEmpty()) {
            return new Algorithm(new ArrayList<>());
        } else {
            try {
                return mapper.readValue(results.get(0), Algorithm.class);
            } catch (JsonProcessingException e) {
                throw new AlgorithmException("Failed to parse algorithm");
            }
        }
    }

    // Adds a new pass to the current configuration
    public Algorithm save(Pass pass) {
        // get current algorithm
        Algorithm configuration = getCurrentAlgorithm();

        // get current max pass Id
        long maxPassId = configuration.passes()
                .stream()
                .max(Comparator.comparing(Pass::id))
                .map(Pass::id)
                .orElse(0l);

        // set id of new pass
        Pass withId = new Pass(maxPassId + 1, pass);

        // add pass to list
        configuration.passes().add(withId);

        // save
        saveAlgorithm(configuration);

        return getCurrentAlgorithm();
    }

    // updates an existing pass
    public Algorithm update(long id, Pass pass) {
        // get current algorithm
        Algorithm configuration = getCurrentAlgorithm();

        // find index of entry with id
        int index = configuration.passes().stream().map(Pass::id).toList().indexOf(id);

        if (index == -1) {
            throw new PassModificationException("Failed to find pass with Id: " + id);
        }

        // replace index with new pass
        Pass withId = new Pass(id, pass);
        configuration.passes().set(index, withId);

        // save
        saveAlgorithm(configuration);
        return getCurrentAlgorithm();
    }

    public Algorithm delete(long id) {
        // get current algorithm
        Algorithm configuration = getCurrentAlgorithm();

        // find index of entry with id
        int index = configuration.passes().stream().map(Pass::id).toList().indexOf(id);

        if (index == -1) {
            throw new PassModificationException("Failed to find pass with Id: " + id);
        }

        configuration.passes().remove(index);
        saveAlgorithm(configuration);
        return getCurrentAlgorithm();
    }

    public void saveDibbsAlgorithm() {
        Algorithm algorithm = getCurrentAlgorithm();
        if (algorithm != null && !algorithm.passes().isEmpty()) {
            saveDibbsAlgorithm(algorithm);
        }
    }

    // persists algorithm to deduplication database
    public void saveAlgorithm(Algorithm algorithm) {
        saveDibbsAlgorithm(algorithm);

        try {
            String stringValue = mapper.writeValueAsString(algorithm);
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("configuration", stringValue);
            template.update(INSERT_CONFIG, params);

        } catch (JsonProcessingException e) {
            throw new PassModificationException("Failed to save pass");
        }
    }

    // convert algorithm to DIBBs format and make API request to save
    private void saveDibbsAlgorithm(Algorithm algorithm) {
        DataElements dataElements = dataElementsService.getCurrentDataElements();
        if (dataElements == null) {
            throw new PassModificationException("Data elements must first be configured");
        }

        DibbsAlgorithm dibbsAlgorithm = algorithmMapper.map(algorithm, dataElements);
        dibbsService.save(dibbsAlgorithm);
    }

}
