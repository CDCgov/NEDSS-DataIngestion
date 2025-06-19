package gov.cdc.nbs.deduplication.sync.kafka;

import static org.assertj.core.api.Assertions.assertThat;

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
import gov.cdc.nbs.deduplication.patient.mpi.MpiPatientResolver;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.sync.service.PersonDeleteSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonInsertSyncHandler;
import gov.cdc.nbs.deduplication.sync.service.PersonUpdateSyncHandler;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@UseTestContainers
class SyncTest {

  private final PatientManager patientManager;
  private final KafkaConsumerService consumer;
  private final MpiPatientResolver mpiPatientResolver;

  @Autowired
  public SyncTest(
      final PatientManager patientManager,
      final PersonInsertSyncHandler insertSyncHandler,
      final PersonUpdateSyncHandler updateSyncHandler,
      final PersonDeleteSyncHandler deleteSyncHandler,
      final MpiPatientResolver mpiPatientResolver) {
    this.patientManager = patientManager;
    this.mpiPatientResolver = mpiPatientResolver;
    this.consumer = new KafkaConsumerService(insertSyncHandler, updateSyncHandler, deleteSyncHandler);
  }

  @Test
  void syncNewPatientTest() {
    // Create a new patient in NBS and add a name
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
            null,
            null));
    // verify patient does not exist in MPI
    MpiPerson mpiData = mpiPatientResolver.resolve(patientId);
    assertThat(mpiData).isNull();

    // generate a database create event
    String createEvent = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PAT",
              "person_uid": PERSON_ID,
              "person_parent_uid": PERSON_ID,
              "record_status_cd": "ACTIVE"
            }
          }
        }
        """
        .replaceAll("PERSON_ID", String.valueOf(patientId));

    // process event
    consumer.consumePersonMessage(createEvent);

    // verify MPI now contains patient data
    mpiData = mpiPatientResolver.resolve(patientId);
    assertThat(mpiData.external_id()).isEqualTo(String.valueOf(patientId));

    assertThat(mpiData.name()).hasSize(1);
    assertThat(mpiData.name().get(0).family()).isEqualTo("Smith");
    assertThat(mpiData.name().get(0).given()).containsExactly("John", "Bob");
  }

  @Test
  void syncUpdatePatientTest() {
    // Create a new patient in NBS and add a name
    long patientId = patientManager.createPatient();

    patientManager.addName(
        patientId,
        new PatientName(
            Timestamp.valueOf(LocalDateTime.now()),
            PatientName.Type.LEGAL,
            PatientName.Prefix.MR,
            "Tim",
            "B",
            "James",
            PatientName.Suffix.SENIOR,
            null));

    // generate a database create event
    String createEvent = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PAT",
              "person_uid": PERSON_ID,
              "person_parent_uid": PERSON_ID,
              "record_status_cd": "ACTIVE"
            }
          }
        }
        """
        .replaceAll("PERSON_ID", String.valueOf(patientId));

    // process event
    consumer.consumePersonMessage(createEvent);

    // verify MPI now contains patient data
    MpiPerson mpiData = mpiPatientResolver.resolve(patientId);
    assertThat(mpiData.external_id()).isEqualTo(String.valueOf(patientId));
    assertThat(mpiData.name()).hasSize(1);
    assertThat(mpiData.name().get(0).family()).isEqualTo("James");
    assertThat(mpiData.name().get(0).given()).containsExactly("Tim", "B");
    assertThat(mpiData.name().get(0).suffix()).containsExactly("Sr");

    // add an additional name for the patient
    patientManager.addName(
        patientId,
        new PatientName(
            Timestamp.valueOf(LocalDateTime.now()),
            null,
            null,
            "Timothy",
            null,
            "James",
            null,
            null));

    // generate a database update event
    String updateEvent = """
        {
          "payload": {
            "op": "u",
            "after": {
              "cd": "PAT",
              "person_uid": PERSON_ID,
              "person_parent_uid": PERSON_ID,
              "record_status_cd": "ACTIVE"
            }
          }
        }
        """
        .replaceAll("PERSON_ID", String.valueOf(patientId));

    // process update event
    consumer.consumePersonMessage(updateEvent);

    // verify MPI now contains new patient data
    mpiData = mpiPatientResolver.resolve(patientId);
    assertThat(mpiData.external_id()).isEqualTo(String.valueOf(patientId));
    assertThat(mpiData.name()).hasSize(2);
    assertThat(mpiData.name().get(0).family()).isEqualTo("James");
    assertThat(mpiData.name().get(0).given()).containsExactly("Tim", "B");

    assertThat(mpiData.name().get(1).family()).isEqualTo("James");
    assertThat(mpiData.name().get(1).given()).containsExactly("Timothy");
  }

  @Test
  void syncDeletePatientTest() {
    // Create a new patient in NBS and add a name
    long patientId = patientManager.createPatient();

    patientManager.addName(
        patientId,
        new PatientName(
            Timestamp.valueOf(LocalDateTime.now()),
            PatientName.Type.LEGAL,
            null,
            "Peter",
            "J",
            "Parks",
            null,
            null));

    // generate a database create event
    String createEvent = """
        {
          "payload": {
            "op": "c",
            "after": {
              "cd": "PAT",
              "person_uid": PERSON_ID,
              "person_parent_uid": PERSON_ID,
              "record_status_cd": "ACTIVE"
            }
          }
        }
        """
        .replaceAll("PERSON_ID", String.valueOf(patientId));

    // process event
    consumer.consumePersonMessage(createEvent);

    // verify MPI now contains patient data
    MpiPerson mpiData = mpiPatientResolver.resolve(patientId);
    assertThat(mpiData).isNotNull();

    // Delete the patient from NBS (set status INACTIVE)
    patientManager.markInactive(patientId);

    // generate a database update event
    String updateEvent = """
        {
          "payload": {
            "op": "u",
            "after": {
              "cd": "PAT",
              "person_uid": PERSON_ID,
              "person_parent_uid": PERSON_ID,
              "record_status_cd": "INACTIVE"
            }
          }
        }
        """
        .replaceAll("PERSON_ID", String.valueOf(patientId));

    // process event
    consumer.consumePersonMessage(updateEvent);

    // Verify patient is no longer in MPI
    mpiData = mpiPatientResolver.resolve(patientId);
    assertThat(mpiData).isNull();
  }

}
