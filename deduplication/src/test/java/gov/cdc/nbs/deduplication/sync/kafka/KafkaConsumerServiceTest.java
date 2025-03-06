package gov.cdc.nbs.deduplication.sync.kafka;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.sync.service.PersonInsertSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonUpdateSyncHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.spi.ILoggingEvent;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

  @Mock
  private PersonInsertSyncHandler insertHandler;

  @Mock
  private PersonUpdateSyncHandler updateHandler;

  private KafkaConsumerService kafkaConsumerService;
  private ObjectMapper objectMapper;

  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    objectMapper = new ObjectMapper();
    kafkaConsumerService = new KafkaConsumerService(insertHandler, updateHandler);

    Logger logger = (Logger) LoggerFactory.getLogger(KafkaConsumerService.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);  // Attach ListAppender to capture logs
  }


  @Test
  void testConsumePersonMessage_CreateOperation() throws Exception {
    String message = "{\"payload\": {\"op\": \"c\"}}"; //  c for adding a new row
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");

    kafkaConsumerService.consumePersonMessage(message);

    verify(insertHandler, times(1)).handleInsert(payloadNode);
    verify(updateHandler, never()).handleUpdate(any(), any());
  }

  @Test
  void testConsumePersonMessage_UpdateOperation() throws Exception {
    String message = "{\"payload\": {\"op\": \"u\"}}"; // u for update
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");

    kafkaConsumerService.consumePersonMessage(message);

    verify(updateHandler, times(1)).handleUpdate(payloadNode, "Person");
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonMessage_otherOperation() throws Exception {
    String message = "{\"payload\": {\"op\": \"d\"}}";
    kafkaConsumerService.consumePersonMessage(message);

    verify(updateHandler, never()).handleUpdate(any(), any());
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonMessage_ExceptionHandling() throws Exception {
    String message = "{\"payload\": {\"op\": \"c\"}}";
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");
    doThrow(new RuntimeException("Test exception")).when(insertHandler).handleInsert(payloadNode);
    kafkaConsumerService.consumePersonMessage(message);

    // Verify that error logging is called when an exception occurs
    boolean logFound = listAppender
        .list
        .stream()
        .anyMatch(loggingEvent -> loggingEvent.getMessage().contains("Error while processing message from topic:"));

    assertTrue(logFound);
  }

  @Test
  void testConsumePersonDataMessage_CreateOperation() throws Exception {
    String message = "{\"payload\": {\"op\": \"c\"}}";
    String topic = "test.NBS_ODSE.dbo.Person_name";
    kafkaConsumerService.consumePersonDataMessage(topic, message);
    verify(updateHandler, times(1)).handleUpdate(any(), any());

  }

  @Test
  void testConsumePersonDataMessage_UpdateOperation() throws Exception {
    String message = "{\"payload\": {\"op\": \"u\"}}";
    String topic = "test.NBS_ODSE.dbo.Person_name";
    kafkaConsumerService.consumePersonDataMessage(topic, message);
    verify(updateHandler, times(1)).handleUpdate(any(), any());
  }

  @Test
  void testConsumePersonDataMessage_otherOperation() throws Exception {
    String message = "{\"payload\": {\"op\": \"d\"}}";
    String topic = "test.NBS_ODSE.dbo.Person_name";
    kafkaConsumerService.consumePersonDataMessage(topic, message);

    verify(updateHandler, never()).handleUpdate(any(), any());
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonDataMessage_ExceptionHandling() throws Exception {
    String message = "{\"payload\": {\"op\": \"u\"}}";
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");
    String topic = "test.NBS_ODSE.dbo.Person_name";

    doThrow(new RuntimeException("Test exception")).when(updateHandler).handleUpdate(payloadNode, topic);

    kafkaConsumerService.consumePersonDataMessage(topic, message);

    boolean logFound = listAppender
        .list
        .stream()
        .anyMatch(loggingEvent -> loggingEvent.getMessage().contains("Error while processing message from topic:"));

    assertTrue(logFound);
  }


}
