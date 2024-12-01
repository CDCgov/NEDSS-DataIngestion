package gov.cdc.nbs.mpidatasyncer.service.activemq;

import gov.cdc.nbs.mpidatasyncer.constants.ProcessingQueue;
import gov.cdc.nbs.mpidatasyncer.entity.mpi.MPIPatient;
import gov.cdc.nbs.mpidatasyncer.helper.MigrationHelper;
import gov.cdc.nbs.mpidatasyncer.model.MPIPatientDto;
import gov.cdc.nbs.mpidatasyncer.repository.mpi.MpiPatientRepository;
import gov.cdc.nbs.mpidatasyncer.service.logs.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpiPatientConsumerService {

  private final MpiPatientRepository mpiPatientRepository;
  private final LogService logService;
  private final MigrationHelper migrationStateHelper;
  private int total = 0;
  private int success = 0;
  private int failure = 0;

  @Value("${sync.mode}")
  private boolean syncMode;

  @Transactional
  @JmsListener(destination = ProcessingQueue.PATIENT_INSERT_QUEUE)
  public void insertPatient(MPIPatientDto mpiPatientDto) {
    total++;
    try {
      mpiPatientRepository.save(new MPIPatient(mpiPatientDto));
      success++;
      String infoMessage = "Successfully inserted patient with ID: " + mpiPatientDto.externalPatientId();
      logService.logSuccess(infoMessage);
    } catch (Exception e) {
      failure++;
      String errorMessage =
          "Failed to insert patient with ID: " + mpiPatientDto.externalPatientId() + " - Error: " + e.getMessage();
      logService.logError(errorMessage, e);
    }
      if (!syncMode && migrationStateHelper.getMaxPersonUid().equals(Long.valueOf(mpiPatientDto.externalPatientId()))) {
        logService.logInfo("Finished migration.");
        logService.logInfo("total: " + total);
        logService.logInfo("Success: " + success);
        logService.logInfo("failure: " + failure);
    }
  }



  @Transactional
  @JmsListener(destination = ProcessingQueue.PATIENT_UPDATE_QUEUE)
  public void updatePatient(MPIPatientDto mpiPatientDto) {
    try {
      Optional<MPIPatient> existingPatient =
          mpiPatientRepository.findByExternalPatientId(mpiPatientDto.externalPatientId());
      if (existingPatient.isPresent()) {
        MPIPatient mpiPatient = existingPatient.get();
        mpiPatient.setData(mpiPatientDto.jsonData());
        mpiPatientRepository.save(mpiPatient);
        String infoMessage = "Successfully updated record with ID: " + mpiPatientDto.externalPatientId();
        logService.logSuccess(infoMessage);
      } else {
        String warnMessage =
            "Failed to update patient with ID: " + mpiPatientDto.externalPatientId() + " - Patient id not found";
        logService.logWarn(warnMessage);
      }
    } catch (Exception e) {
      String errorMessage =
          "Failed to update patient with ID: " + mpiPatientDto.externalPatientId() + " - Error: " + e.getMessage();
      logService.logError(errorMessage, e);
    }
  }


}
