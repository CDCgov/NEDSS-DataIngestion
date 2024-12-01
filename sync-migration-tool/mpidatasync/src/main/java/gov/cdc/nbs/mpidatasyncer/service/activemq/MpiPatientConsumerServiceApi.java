package gov.cdc.nbs.mpidatasyncer.service.activemq;

import gov.cdc.nbs.mpidatasyncer.constants.ProcessingQueue;
import gov.cdc.nbs.mpidatasyncer.exception.PatientInsertionException;
import gov.cdc.nbs.mpidatasyncer.helper.MigrationHelper;
import gov.cdc.nbs.mpidatasyncer.model.LinkerSeedRequest;
import gov.cdc.nbs.mpidatasyncer.service.linker.LinkerApiService;
import gov.cdc.nbs.mpidatasyncer.service.logs.LogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class MpiPatientConsumerServiceApi {

  private final LogService logService;
  private final LinkerApiService linkerApiService;
  private final MigrationHelper migrationStateHelper;

  private int total = 0;
  private int success = 0;
  private int failure = 0;

  @Transactional
  @JmsListener(destination = ProcessingQueue.PATIENT_INSERT_QUEUE_API)
  public void insertPatient(List<LinkerSeedRequest.Cluster> clusters) {
    LinkerSeedRequest.Cluster cluster =clusters.get(0);
    try {
      total+= cluster.records().size();
      LinkerSeedRequest linkerSeedRequest = new LinkerSeedRequest(clusters);
      ResponseEntity<String> response = linkerApiService.seed(linkerSeedRequest);
      if (response.getStatusCode().is2xxSuccessful()) {
        success+= cluster.records().size();
        for(LinkerSeedRequest.Record patientRecord : cluster.records()) {
          String infoMessage = "Successfully inserted patient with ID: " + patientRecord.external_id();
          logService.logSuccess(infoMessage);
        }
      } else {
        throw new PatientInsertionException("Failed to insert Person with id: "+cluster.external_person_id());
      }
    } catch (Exception e) {
      failure+= cluster.records().size();
      for(LinkerSeedRequest.Record patentRecord : cluster.records()) {
        String errorMessage =
            "Failed to insert patient with ID: " + patentRecord.external_id() + " - Error: " + e.getMessage();
        logService.logError(errorMessage, e);
      }
    }
    Long lastPatentUid = Long.valueOf(cluster.records().getLast().external_id());
    if (migrationStateHelper.getMaxPersonUid().equals(lastPatentUid)) {
      logService.logInfo("Finished migration.");
      logService.logInfo("total persons: " + total);
      logService.logInfo("Success: " + success);
      logService.logInfo("failure: " + failure);
    }
  }
}
