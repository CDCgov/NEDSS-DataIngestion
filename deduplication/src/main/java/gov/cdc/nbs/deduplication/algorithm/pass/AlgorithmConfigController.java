package gov.cdc.nbs.deduplication.algorithm.pass;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfiguration;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchConfigurationResponse;

@RestController
@RequestMapping("/api/deduplication/configuration")
public class AlgorithmConfigController {
  private final AlgorithmConfigResolver resolver;
  private final AlgorithmConfigCreator creator;

  public AlgorithmConfigController(
      final AlgorithmConfigResolver resolver,
      final AlgorithmConfigCreator creator) {
    this.resolver = resolver;
    this.creator = creator;
  }

  @GetMapping
  public MatchConfigurationResponse getConfiguration() {
    return resolver.resolveCurrent();
  }

  @PostMapping
  public MatchConfigurationResponse save(@RequestBody MatchConfiguration configuration) {
    creator.create(configuration);
    return resolver.resolveCurrent();
  }

}
