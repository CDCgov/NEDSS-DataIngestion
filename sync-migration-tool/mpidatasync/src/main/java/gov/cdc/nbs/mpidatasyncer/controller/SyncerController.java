package gov.cdc.nbs.mpidatasyncer.controller;

import gov.cdc.nbs.mpidatasyncer.service.activemq.PersonSyncHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SyncerController {

  private final PersonSyncHandler readProducerService;

  @GetMapping("/api/migration/start")
  public String startMigration() {
    try {
      readProducerService.migratePersonData();
      return "Migration process started successfully.";
    } catch (Exception e) {
      return "Failed to start migration: " + e.getMessage();
    }
  }
}
