package gov.cdc.nbs.deduplication.sync.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.sync.service.PersonDeleteSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonInsertSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonUpdateSyncHandler;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(value = "deduplication.sync.enabled", havingValue = "true")
public class KafkaConsumerService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final PersonInsertSyncHandler insertHandler;
  private final PersonUpdateSyncHandler updateHandler;
  private final PersonDeleteSyncHandler deleteHandler;

  KafkaConsumerService(
      final PersonInsertSyncHandler insertHandler,
      final PersonUpdateSyncHandler updateHandler,
      final PersonDeleteSyncHandler deleteHandler) {
    this.insertHandler = insertHandler;
    this.updateHandler = updateHandler;
    this.deleteHandler = deleteHandler;
  }

  // Consumer method for person-related messages
  @KafkaListener(topics = "${kafka.topics.person}", groupId = "mpiDataSyncerGroup")
  public void consumePersonMessage(String message) {
    try {
      // read message
      JsonNode payloadNode = objectMapper.readTree(message).path("payload");

      // check if person record is PAT. if not, do nothing
      JsonNode afterNode = payloadNode.path("after");
      String cd = afterNode.get("cd").asText();

      if ("PAT".equals(cd)) {
        String operation = payloadNode.path("op").asText();
        handlePersonOperation(operation, payloadNode);
      }

    } catch (Exception e) {
      log.error("Error while processing message from topic: {}: {}", "test.NBS_ODSE.dbo.Person", message, e);
      throw new SyncProcessException(e.getMessage());
    }
  }

  private void handlePersonOperation(String operation, JsonNode payloadNode) throws JsonProcessingException {
    if ("c".equals(operation)) {
      insertHandler.handleInsert(payloadNode); // c for adding a new row
    } else if ("u".equals(operation)) { // u for update
      // Deleting a patient in NBS sets record_status_cd to "INACTIVE"
      String newStatus = payloadNode.path("after").path("record_status_cd").asText();
      if ("ACTIVE".equalsIgnoreCase(newStatus)) {
        updateHandler.handleUpdate(payloadNode);
      } else {
        deleteHandler.handleDelete(payloadNode);
      }
    }
  }

}
