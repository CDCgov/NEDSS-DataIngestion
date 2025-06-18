package gov.cdc.nbs.deduplication.sync.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

  KafkaConsumerService(final PersonInsertSyncHandler insertHandler, final PersonUpdateSyncHandler updateHandler) {
    this.insertHandler = insertHandler;
    this.updateHandler = updateHandler;
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
    }
  }

  private void handlePersonOperation(String operation, JsonNode payloadNode) throws JsonProcessingException {
    if ("c".equals(operation)) {
      insertHandler.handleInsert(payloadNode); // c for adding a new row
    } else if ("u".equals(operation)) {
      updateHandler.handleUpdate(payloadNode); // u for update
    }
  }

}
