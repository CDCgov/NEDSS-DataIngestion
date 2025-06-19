package gov.cdc.nbs.deduplication.sync.kafka;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.sync.service.PersonDeleteSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonInsertSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonUpdateSyncHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.spi.ILoggingEvent;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

  @Mock
  private PersonInsertSyncHandler insertHandler;

  @Mock
  private PersonUpdateSyncHandler updateHandler;

  @Mock
  private PersonDeleteSyncHandler deleteHandler;

  private KafkaConsumerService kafkaConsumerService;
  private ObjectMapper objectMapper;

  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    objectMapper = new ObjectMapper();
    kafkaConsumerService = new KafkaConsumerService(insertHandler, updateHandler, deleteHandler);

    Logger logger = (Logger) LoggerFactory.getLogger(KafkaConsumerService.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender); // Attach ListAppender to capture logs
  }

  @Test
  void testConsumePersonMessage_CreateOperation() throws Exception {
    // c for adding a new row and PAT for patient
    String message = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PAT",
              "record_status_cd": "ACTIVE"
            }
          }
        }
        """;
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");

    kafkaConsumerService.consumePersonMessage(message);

    verify(insertHandler, times(1)).handleInsert(payloadNode);
    verify(deleteHandler, never()).handleDelete(any());
    verify(updateHandler, never()).handleUpdate(any());
  }

  @Test
  void testConsumePersonMessage_UpdateOperation() throws Exception {
    // u for updating a row and PAT for patient
    String message = """
        {
          "payload": {
            "op": "u",
            "after": {
              "cd": "PAT",
              "record_status_cd": "ACTIVE"
            }
          }
        }
        """;
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");

    kafkaConsumerService.consumePersonMessage(message);

    verify(updateHandler, times(1)).handleUpdate(payloadNode);
    verify(deleteHandler, never()).handleDelete(any());
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonMessage_UpdateInactiveOperation() throws Exception {
    // u for updating a row and PAT for patient
    String message = """
        {
          "payload": {
            "op": "u",
            "after": {
              "cd": "PAT",
              "record_status_cd": "INACTIVE"
            }
          }
        }
        """;
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");

    kafkaConsumerService.consumePersonMessage(message);

    verify(deleteHandler, times(1)).handleDelete(payloadNode);
    verify(updateHandler, never()).handleUpdate(any());
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonMessage_otherOperation() throws Exception {
    // d for delete row and PAT for patient
    String message = """
        {
          "payload": {
            "op": "d",
            "after": {
              "cd": "PAT"
            }
          }
        }
        """;
    kafkaConsumerService.consumePersonMessage(message);

    verify(updateHandler, never()).handleUpdate(any());
    verify(deleteHandler, never()).handleDelete(any());
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonMessage_create_nonPatient() throws Exception {
    // c for create and PRV for provider
    String message = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PRV"
            }
          }
        }
        """;
    kafkaConsumerService.consumePersonMessage(message);

    verify(updateHandler, never()).handleUpdate(any());
    verify(deleteHandler, never()).handleDelete(any());
    verify(insertHandler, never()).handleInsert(any());
  }

  @Test
  void testConsumePersonMessage_ExceptionHandling() throws Exception {
    // c for adding a new row
    String message = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PAT"
            }
          }
        }
        """;
    JsonNode payloadNode = objectMapper.readTree(message).path("payload");
    doThrow(new RuntimeException("Test exception")).when(insertHandler).handleInsert(payloadNode);
    assertThrows(SyncProcessException.class, () -> kafkaConsumerService.consumePersonMessage(message));

    // Verify that error logging is called when an exception occurs
    boolean logFound = listAppender.list
        .stream()
        .anyMatch(loggingEvent -> loggingEvent.getMessage().contains("Error while processing message from topic:"));

    assertTrue(logFound);
  }

}
