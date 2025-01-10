package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.exception.ConfigurationParsingException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.BlockingCriteria;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.MatchingCriteria;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.Pass;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.SelectableCriteria;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfigurationResponse;

@ExtendWith(MockitoExtension.class)
class AlgorithmConfigResolverTest {

  @Mock
  private JdbcTemplate template;

  @Spy
  private ObjectMapper mapper = new ObjectMapper();

  @InjectMocks
  private AlgorithmConfigResolver resolver;

  private static final MatchConfiguration CONFIG = new MatchConfiguration(
      List.of(
          new Pass(
              "pass1",
              "pass description",
              true,
              List.of(new BlockingCriteria(
                  new SelectableCriteria("LAST_NAME", "Last name"),
                  new SelectableCriteria("FIRST_FOUR", "First four"))),
              List.of(new MatchingCriteria(
                  new SelectableCriteria("ADDRESS", "Address"),
                  new SelectableCriteria("LAST_FOUR", "Exact"))),
              0.5,
              0.9)));

  @Test
  void should_resolve() throws JsonProcessingException {
    String configString = mapper.writeValueAsString(CONFIG);
    List<String> configList = List.of(configString, "oldConfig");

    when(template.queryForList(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(configList);

    MatchConfigurationResponse configurationResponse = resolver.resolveCurrent();

    assertThat(configurationResponse.configuration()).isEqualTo(CONFIG);
  }

  @Test
  void should_not_resolve_empty() {
    List<String> configList = new ArrayList<>();

    when(template.queryForList(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(configList);

    MatchConfigurationResponse configurationResponse = resolver.resolveCurrent();

    assertThat(configurationResponse.configuration()).isNull();
  }

  @Test
  void should_not_resolve_parsing_exception() throws JsonProcessingException {
    List<String> configList = List.of("config1");
    when(template.queryForList(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(configList);
    doThrow(JsonProcessingException.class).when(mapper).readValue("config1", MatchConfiguration.class);

    assertThrows(ConfigurationParsingException.class, () -> resolver.resolveCurrent());

  }
}
