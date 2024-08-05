package gov.cdc.nbs.dibbs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import gov.cdc.nbs.dibbs.model.PersonContainer;
import gov.cdc.nbs.dibbs.service.DibbsMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

  private final DibbsMatchService dibbsMatchService;

  @PostMapping ("/match")
  public ResponseEntity<JsonNode> match(@RequestBody PersonContainer personContainer) throws IOException, InterruptedException {
    return dibbsMatchService.match(personContainer);
  }



}
