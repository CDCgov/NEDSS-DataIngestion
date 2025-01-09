package gov.cdc.nbs.deduplication.algorithm.dataelements;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration;
import gov.cdc.nbs.deduplication.algorithm.exception.ConfigurationParsingException;

@Component
public class DataElementCreator {
  private final JdbcTemplate template;
  private final ObjectMapper mapper;

  private static final String QUERY = """
      INSERT INTO
        data_element_configuration (configuration)
      VALUES
        (?)
      """;

  public DataElementCreator(final JdbcTemplate template, final ObjectMapper mapper) {
    this.template = template;
    this.mapper = mapper;
  }

  public void create(DataElementConfiguration configuration) {
    try {
      template.update(QUERY, mapper.writeValueAsString(configuration));
    } catch (JsonProcessingException e) {
      throw new ConfigurationParsingException();
    }
  }

}
