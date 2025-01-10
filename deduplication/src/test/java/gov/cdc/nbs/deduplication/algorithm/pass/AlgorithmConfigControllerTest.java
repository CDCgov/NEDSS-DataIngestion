package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.Pass;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.BlockingCriteria;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.SelectableCriteria;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration.MatchingCriteria;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfigurationResponse;

@ExtendWith(MockitoExtension.class)
class AlgorithmConfigControllerTest {

  @Mock
  private AlgorithmConfigResolver resolver;

  @Mock
  private AlgorithmConfigCreator creator;

  @InjectMocks
  private AlgorithmConfigController controller;

  private static final MatchConfigurationResponse CONFIG = new MatchConfigurationResponse(
      new MatchConfiguration(
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
                  0.9))));

  @Test
  void should_resolve() {
    when(resolver.resolveCurrent()).thenReturn(CONFIG);
    MatchConfigurationResponse response = controller.getConfiguration();
    assertThat(response).isEqualTo(CONFIG);
  }

  @Test
  void should_save_and_resolve() {
    when(resolver.resolveCurrent()).thenReturn(CONFIG);
    MatchConfigurationResponse response = controller.save(CONFIG.configuration());

    verify(creator, times(1)).create(CONFIG.configuration());
    assertThat(response).isEqualTo(CONFIG);
  }
}
