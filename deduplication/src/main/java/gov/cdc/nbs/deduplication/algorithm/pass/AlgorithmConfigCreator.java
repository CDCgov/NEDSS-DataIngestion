package gov.cdc.nbs.deduplication.algorithm.pass;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.exception.ConfigurationParsingException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration;

@Component
public class AlgorithmConfigCreator {
  private final JdbcTemplate template;
  private final ObjectMapper mapper;

  private static final String QUERY = """
      INSERT INTO
        match_configuration (configuration)
      VALUES
        (?)
      """;

  public AlgorithmConfigCreator(
      @Qualifier("deduplicationTemplate") final JdbcTemplate template,
      final ObjectMapper mapper) {
    this.template = template;
    this.mapper = mapper;
  }

  public void create(MatchConfiguration configuration) {
    try {
      template.update(QUERY, mapper.writeValueAsString(configuration));
    } catch (JsonProcessingException e) {
      throw new ConfigurationParsingException();
    }
  }
}
