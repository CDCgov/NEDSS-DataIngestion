package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
class AlgorithmConfigCreatorTest {

  @Mock
  private JdbcTemplate template;

  @Spy
  private ObjectMapper mapper = new ObjectMapper();

  @InjectMocks
  private AlgorithmConfigCreator creator;

  @Test
  void should_create() throws JsonProcessingException {
    MatchConfiguration config = new MatchConfiguration(
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

    when(mapper.writeValueAsString(config)).thenReturn("stringValue");

    creator.create(config);

    verify(template, times(1)).update(Mockito.anyString(), Mockito.eq("stringValue"));
  }

  @Test
  void should_not_create() throws JsonProcessingException {
    when(mapper.writeValueAsString(null)).thenThrow(JsonProcessingException.class);
    assertThrows(ConfigurationParsingException.class, () -> creator.create(null));

  }
}
