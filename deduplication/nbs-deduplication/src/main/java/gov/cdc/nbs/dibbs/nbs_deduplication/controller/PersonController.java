package gov.cdc.nbs.dibbs.nbs_deduplication.controller;


import gov.cdc.nbs.dibbs.nbs_deduplication.model.MatchPersonRequest;
import gov.cdc.nbs.dibbs.nbs_deduplication.service.DibbsMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class PersonController {

  private final DibbsMatchService dibbsMatchService;

  @PostMapping("person/match")
  public ResponseEntity<String> match(@RequestBody MatchPersonRequest request)
      throws InterruptedException {
    return dibbsMatchService.match(request);
  }

}
