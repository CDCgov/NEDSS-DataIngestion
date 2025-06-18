package gov.cdc.nbs.deduplication.sync.kafka;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import gov.cdc.nbs.deduplication.config.container.UseTestContainers;
import gov.cdc.nbs.deduplication.patient.PatientManager;
import gov.cdc.nbs.deduplication.patient.PatientName;
import gov.cdc.nbs.deduplication.sync.service.PersonInsertSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonUpdateSyncHandler;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@UseTestContainers
class SyncTest {

  private final PatientManager patientManager;
  private final KafkaConsumerService consumer;

  @Autowired
  public SyncTest(
      final PatientManager patientManager,
      final PersonInsertSyncHandler insertSyncHandler,
      final PersonUpdateSyncHandler updateSyncHandler) {
    this.patientManager = patientManager;
    this.consumer = new KafkaConsumerService(insertSyncHandler, updateSyncHandler);
  }

  @Test
  void syncNewPatientTest() {
    // Create a new patient and add a name
    long patientId = patientManager.createPatient();

    patientManager.addName(
        patientId,
        new PatientName(
            Timestamp.valueOf(LocalDateTime.now()),
            PatientName.Type.LEGAL,
            null,
            "John",
            "Bob",
            "Smith",
            PatientName.Suffix.JUNIOR,
            null));
    // generate a database change event and process
    String createEvent = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PAT",
              "person_uid": PERSON_ID,
              "person_parent_uid": PERSON_ID
            }
          }
        }
        """;
    consumer.consumePersonMessage(createEvent.replaceAll("PERSON_ID", String.valueOf(patientId)));

    System.out.println("WOW we made it this far");
    // verify MPI now contains patient data

  }

}
