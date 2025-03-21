package gov.cdc.nbs.deduplication.algorithm.dataelements;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.exception.DataElementModificationException;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;

@Component
public class DataElementsService {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    public DataElementsService(
            @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template,
            ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    static final String SELECT_CURRENT_DATA_ELEMENTS = """
            SELECT TOP 1 configuration
            FROM data_element_configuration
            ORDER BY add_time DESC
            """;

    static final String INSERT_DATA_ELEMENTS = """
            INSERT INTO data_element_configuration (configuration)
            VALUES (:configuration)
            """;

    public DataElements getCurrentDataElements() {
        List<String> results = template.getJdbcTemplate().queryForList(SELECT_CURRENT_DATA_ELEMENTS, String.class);
        if (results.isEmpty()) {
            return null;
        } else {
            try {
                return mapper.readValue(results.get(0), DataElements.class);
            } catch (JsonProcessingException e) {
                throw new DataElementModificationException("Failed to parse data elements");
            }
        }
    }

    public DataElements save(DataElements dataElements) {
        try {
            String stringValue = mapper.writeValueAsString(dataElements);
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("configuration", stringValue);
            template.update(INSERT_DATA_ELEMENTS, params);

        } catch (JsonProcessingException e) {
            throw new DataElementModificationException("Failed to save data elements");
        }
        return getCurrentDataElements();
    }

}
