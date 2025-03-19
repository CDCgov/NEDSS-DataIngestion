package gov.cdc.nbs.deduplication.data_elements.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DataElementConfigurationRepository {

    private final NamedParameterJdbcTemplate template;

    // Constructor injection for NamedParameterJdbcTemplate
    public DataElementConfigurationRepository(@Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public void saveDataElementConfiguration(DataElementsDTO dataElementsDTO) throws JsonProcessingException {
        // Convert the configuration to JSON (reuse buildConfigurationJson method)
        String configurationJson = buildConfigurationJson(dataElementsDTO.dataElements());

        // SQL query with a named parameter for the configuration
        String sql = "INSERT INTO deduplication.dbo.data_element_configuration (configuration, add_time) " +
                "VALUES (:configuration, GETDATE())";

        // Parameters for the SQL query
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("configuration", configurationJson);

        // Execute the update
        template.update(sql, params);
    }

    // Helper method to convert the configuration to JSON (reuse from earlier)
    public String buildConfigurationJson(Map<String, DataElementsDTO.DataElementConfig> dataElements) throws  JsonProcessingException {
        // Convert the data elements to a list of maps and serialize to JSON
        List<Map<String, Object>> validElements = dataElements.entrySet().stream()
                .filter(entry -> entry.getValue().active() &&
                        entry.getValue().oddsRatio() != null &&
                        entry.getValue().threshold() != null)
                .map(entry -> {
                    Map<String, Object> elementMap = new HashMap<>();
                    elementMap.put("field", entry.getKey());
                    elementMap.put("active", entry.getValue().active());
                    elementMap.put("oddsRatio", entry.getValue().oddsRatio());
                    elementMap.put("logOdds", entry.getValue().logOdds());
                    elementMap.put("threshold", entry.getValue().threshold());
                    return elementMap;
                })
                .toList();

        // Convert the list to JSON string (e.g., using Jackson or Gson)
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(validElements);
    }
}
