package gov.cdc.nbs.deduplication.algorithm.pass;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.exception.ConfigurationParsingException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfigurationResponse;

@Component
public class MatchConfigurationResolver {
  private final JdbcTemplate template;
  private final ObjectMapper mapper;

  private static final String QUERY = """
      SELECT
        TOP 1 configuration
      FROM
        match_configuration
        ORDER BY add_time desc;
                 """;

  public MatchConfigurationResolver(final JdbcTemplate template, final ObjectMapper mapper) {
    this.template = template;
    this.mapper = mapper;
  }

  public MatchConfigurationResponse resolveCurrent() {
    final List<String> configurations = template.queryForList(QUERY, String.class);
    if (configurations.isEmpty()) {
      return new MatchConfigurationResponse(null);
    }
    try {
      return new MatchConfigurationResponse(
          mapper.readValue(configurations.get(0), MatchConfiguration.class));
    } catch (JsonProcessingException e) {
      throw new ConfigurationParsingException();
    }

  }
}
