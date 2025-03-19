package gov.cdc.nbs.deduplication.data_elements.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.data_elements.DataElementsService;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElement;
import gov.cdc.nbs.deduplication.data_elements.dto.DataElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DataElementConfigurationRepository {

    private final NamedParameterJdbcTemplate template;
    private static final Logger log = LoggerFactory.getLogger(DataElementsService.class);

    // Constructor injection for NamedParameterJdbcTemplate
    public DataElementConfigurationRepository(@Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public void saveDataElementConfiguration(DataElements dataElements) throws JsonProcessingException {
        String configurationJson = buildConfigurationJson(dataElements);

        // SQL query with a named parameter for the configuration
        String sql = "INSERT INTO deduplication.dbo.data_element_configuration (configuration, add_time) " +
                "VALUES (:configuration, GETDATE())";

        // Parameters for the SQL query
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("configuration", configurationJson);

        // Execute the update
        template.update(sql, params);
    }

    public String buildConfigurationJson(DataElements dataElements) throws JsonProcessingException {
        // Convert the data elements to a list of maps and serialize to JSON
        List<Map<String, Object>> validElements = new ArrayList<>();

        // Iterate over each field in the DataElements record
        for (var entry : dataElements.getClass().getDeclaredFields()) {
            try {
                // Get the field name (likee "firstName")
                String fieldName = entry.getName();

                // Access the value of the field (that is, the DataElement instance)
                DataElement dataElement = (DataElement) entry.get(dataElements);

                // Only proceed if the DataElement is active and contains valid oddsRatio and threshold
                // ??
                if (dataElement.active() && dataElement.oddsRatio() != null && dataElement.threshold() != null) {
                    Map<String, Object> elementMap = new HashMap<>();
                    elementMap.put("field", fieldName);
                    elementMap.put("active", dataElement.active());
                    elementMap.put("oddsRatio", dataElement.oddsRatio());
                    elementMap.put("logOdds", dataElement.logOdds());
                    elementMap.put("threshold", dataElement.threshold());

                    validElements.add(elementMap);
                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field in DataElements", e);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(validElements);
    }


}
