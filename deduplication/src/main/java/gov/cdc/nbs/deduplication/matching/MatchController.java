package gov.cdc.nbs.deduplication.matching;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;

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
