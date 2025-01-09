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
public class MatchController {
  private final MatchConfigurationResolver resolver;
  private final MatchConfigurationCreator creator;

  public MatchController(
      final MatchConfigurationResolver resolver,
      final MatchConfigurationCreator creator) {
    this.resolver = resolver;
    this.creator = creator;
  }

  @GetMapping
  public MatchConfigurationResponse getDataElements() {
    return resolver.resolveCurrent();
  }

  @PostMapping
  public MatchConfigurationResponse save(@RequestBody MatchConfiguration configuration) {
    creator.create(configuration);
    return resolver.resolveCurrent();
  }

}
