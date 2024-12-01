package gov.cdc.nbs.mpidatasyncer.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.mpidatasyncer.entity.mpi.MPIPatient;
import gov.cdc.nbs.mpidatasyncer.entity.mpi.MPIPerson;
import gov.cdc.nbs.mpidatasyncer.model.MPIPatientDto;
import gov.cdc.nbs.mpidatasyncer.repository.mpi.MpiPatientRepository;
import gov.cdc.nbs.mpidatasyncer.repository.mpi.MpiPersonRepository;
import gov.cdc.nbs.mpidatasyncer.service.logs.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

  private final MpiPatientRepository mpiPatientRepository;
  private final MpiPersonRepository mpiPersonRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${sync.mode}")
  private boolean syncMode;

  @Value("${kafka.consumer.enabled}")
  private boolean isKafkaConsumerEnabled;

  private final LogService logService;

   @KafkaListener(topics = "test.NBS_ODSE.dbo.Person", groupId = "mpiDataSyncerGroup")
  public void consumeMessage(String message) {
    if (!(syncMode && isKafkaConsumerEnabled)) {
      log.info("Kafka consumer is disabled by configuration.");
      return;
    }
    try {
      JsonNode payloadNode = parsePayload(message);
      if (payloadNode.isMissingNode()) {
        log.warn("Message payload is missing : {}", message);
        return;
      }
      String operation = payloadNode.path("op").asText();
      handleOperation(operation, payloadNode);

    } catch (JsonProcessingException e) {
      log.error("Failed to parse message: {}", message, e);
    } catch (Exception e) {
      log.error("Unexpected error while processing message: {}", message, e);
    }
  }

  private JsonNode parsePayload(String message) throws JsonProcessingException {
    return objectMapper.readTree(message).path("payload");
  }

  private void handleOperation(String operation, JsonNode payloadNode) throws JsonProcessingException {
    switch (operation) {
      case "c", "u" -> handleInsertOrUpdate(operation, payloadNode);
      case "d" -> handleDeleteOperation(payloadNode);
      default -> log.warn("Unknown operation type: {}", operation);
    }
  }

  private void handleInsertOrUpdate(String operation, JsonNode payloadNode) throws JsonProcessingException {
    JsonNode afterNode = payloadNode.path("after");
    String personUid = afterNode.get("person_uid").asText();
    String personParentUid = afterNode.get("person_parent_uid").asText();
    String afterJson = objectMapper.writeValueAsString(afterNode);
    if ("c".equals(operation)) {
      insertPatient(personUid,personParentUid, afterJson);
    }
    if ("u".equals(operation)) {
      updatePatient(personUid, afterJson);
    }
  }

  private void handleDeleteOperation(JsonNode payloadNode) {
    String personUid = payloadNode.path("before").get("person_uid").asText();
    deleteRecord(personUid);
  }

  private void insertPatient(String personUid,String personParentUid, String jsonContent) {
    try {
      MPIPatient newPatient=null;
      if(personUid.equals(personParentUid)) {//new person
        MPIPerson mpiPerson= mpiPersonRepository.save(new MPIPerson());
        newPatient = new MPIPatient(new MPIPatientDto(mpiPerson.getId(),personUid,personUid, jsonContent));
      }else{//current person
        MPIPatient mpiPatient =mpiPatientRepository.findByExternalPatientIdAndExternalPersonId(personParentUid,personParentUid).get();
        newPatient = new MPIPatient(new MPIPatientDto(mpiPatient.getId(),mpiPatient.getExternalPersonId(),personUid, jsonContent));
      }
      mpiPatientRepository.save(newPatient);
      String infoMessage = "Successfully inserted patient with ID: " + personUid;
      logService.logSuccess( infoMessage);
      log.info(infoMessage);
    } catch (Exception e) {
      String errorMessage = "Failed to insert patient with ID:  " + personUid + " - Error " + e.getMessage();
      logService.logError(errorMessage,e);
      log.error(errorMessage);
    }
  }


  private void updatePatient(String personUid, String jsonContent) {
    try {
      mpiPatientRepository.findByExternalPatientId(personUid).ifPresentOrElse(existingPatient -> {
        existingPatient.setData(jsonContent);
        mpiPatientRepository.save(existingPatient);
        String infoMessage = "Successfully updated record with ID: " + personUid;
        logService.logSuccess(infoMessage);
      }, () -> {
        String warnMessage = "Patient not found for ID: " + personUid;
        logService.logWarn(warnMessage);
      });
    } catch (Exception e) {
      String errorMessage = "Failed to update patient with ID: " + personUid + " - Error: " + e.getMessage();
      logService.logError(errorMessage,e);
    }
  }

  private void deleteRecord(String personUid) {
    try {
      mpiPatientRepository.findByExternalPatientId(personUid).ifPresentOrElse(existingPatient -> {
        mpiPatientRepository.deleteById(existingPatient.getId());
        String infoMessage = "Successfully deleted record with ID: " + personUid;
        logService.logSuccess(infoMessage);
      }, () -> {
        String warnMessage = "Patient not found for ID: " + personUid;
        logService.logWarn(warnMessage);
      });
    } catch (Exception e) {
      String errorMessage = "Failed to delete patient with ID: " + personUid + " - Error: " + e.getMessage();
      logService.logError( errorMessage,e);
    }
  }

}
