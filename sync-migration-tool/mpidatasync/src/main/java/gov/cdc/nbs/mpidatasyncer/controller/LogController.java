package gov.cdc.nbs.mpidatasyncer.controller;


import gov.cdc.nbs.mpidatasyncer.model.LogRequest;
import gov.cdc.nbs.mpidatasyncer.service.logs.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LogController {

  private final LogService logService;

  @PostMapping("/api/logs")
  public List<String> getLogs(@RequestBody LogRequest request) {
    return logService.fetchLogs(request.start(), request.end());
  }
}
