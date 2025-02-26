package gov.cdc.nbs.deduplication.sync.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.sync.service.PersonInsertSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonUpdateSyncHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
      JsonNode payloadNode = objectMapper.readTree(message).path("payload");

      String operation = payloadNode.path("op").asText();
      handlePersonOperation(operation, payloadNode);

    } catch (Exception e) {
      log.error("Error while processing message from topic: {}: {}", "test.NBS_ODSE.dbo.Person", message, e);
    }
  }

  // Consumer method for person-related-data messages
  @KafkaListener(
      topics = {
          "${kafka.topics.person_name}",
          "${kafka.topics.person_race}",
          "${kafka.topics.entity_id}",
          "${kafka.topics.tele_locator}",
          "${kafka.topics.postal_locator}"
      },
      groupId = "mpiDataSyncerGroup"
  )
  public void consumePersonDataMessage(
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      String message) {
    try {
      JsonNode payloadNode = objectMapper.readTree(message).path("payload");

      String operation = payloadNode.path("op").asText();
      handlePersonDataOperation(operation, payloadNode, topic);
    } catch (Exception e) {
      log.error("Error while processing message from topic: {}: {}", topic, message, e);
    }
  }


  private void handlePersonOperation(String operation, JsonNode payloadNode) throws JsonProcessingException {
    if ("c".equals(operation)) {
      insertHandler.handleInsert(payloadNode); // c for adding a new row
    } else if ("u".equals(operation)) {
      updateHandler.handleUpdate(payloadNode, "Person"); // u for update
    }
  }

  // Handles operations for person-related-data (update existing patient)
  private void handlePersonDataOperation(String operation, JsonNode payloadNode, String topic)
      throws JsonProcessingException {
    if ("c".equals(operation) || "u".equals(operation)) {
      updateHandler.handleUpdate(payloadNode, topic);
    }
  }

}
