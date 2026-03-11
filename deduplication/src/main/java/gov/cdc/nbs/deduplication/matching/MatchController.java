package gov.cdc.nbs.deduplication.matching;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {

  private final MatchService matchService;

  public MatchController(final MatchService matchService) {
    this.matchService = matchService;
  }

  @PostMapping("/match")
  public MatchResponse checkForPatientMatch(@RequestBody PersonMatchRequest request) {
    return matchService.match(request);
  }
}
