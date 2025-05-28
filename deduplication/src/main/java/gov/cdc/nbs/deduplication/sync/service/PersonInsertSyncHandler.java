package gov.cdc.nbs.deduplication.sync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest;
import gov.cdc.nbs.deduplication.sync.model.MpiPatientResponse;
import gov.cdc.nbs.deduplication.sync.model.NbsMpiLinkDto;
import gov.cdc.nbs.deduplication.sync.model.PatientCreateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.LocalDateTime;


@Component
public class PersonInsertSyncHandler {

  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;
  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;

  public PersonInsertSyncHandler(
      ObjectMapper objectMapper,
      @Qualifier("recordLinkerRestClient") RestClient recordLinkageClient,
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      final PatientRecordService patientRecordService) {

    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
    this.deduplicationTemplate = deduplicationTemplate;
    this.patientRecordService = patientRecordService;
  }

  public void handleInsert(JsonNode payloadNode) throws JsonProcessingException {
    JsonNode afterNode = payloadNode.path("after");
    String personUid = afterNode.get("person_uid").asText();//NOSONAR
    boolean patientExists = doesPatientExistInMpi(personUid);
    if (!patientExists) {
      String personParentUid = afterNode.get("person_parent_uid").asText();
      MpiPerson mpiPerson = patientRecordService.fetchPersonRecord(personUid);

      boolean isNewPerson = personUid.equals(personParentUid);
      if (isNewPerson) {
        insertNewMpiPerson(mpiPerson);
      } else {
        handleInsertNewMpiPatient(mpiPerson);
      }
    }
  }

  public void insertNewMpiPerson(MpiPerson mpiPerson) throws JsonProcessingException {
    MpiResponse mpiResponse = createMpiPersonRecord(mpiPerson);
    linkNbsToMpi(new NbsMpiLinkDto(mpiResponse));
  }

  private void handleInsertNewMpiPatient(MpiPerson mpiPerson) throws JsonProcessingException {
    boolean parentExists = doesPatientExistInMpi(mpiPerson.parent_id());
    if (parentExists) {
      insertNewPatient(mpiPerson);
    } else {
      insertParentAndPatient(mpiPerson);
    }
  }

  private void insertNewPatient(MpiPerson patient) throws JsonProcessingException {
    insertNewMpiPatient(patient);
  }

  void insertParentAndPatient(MpiPerson patient) throws JsonProcessingException {
    MpiPerson parent = patientRecordService.fetchPersonRecord(patient.parent_id());
    insertNewPerson(parent);
    insertNewPatient(patient);
  }

  private void insertNewPerson(MpiPerson person) throws JsonProcessingException {
    insertNewMpiPerson(person);
  }

  private boolean doesPatientExistInMpi(String personId) {
    return Boolean.TRUE.equals(deduplicationTemplate.queryForObject(
        QueryConstants.MPI_PATIENT_EXISTS_CHECK,
        new MapSqlParameterSource("personId", personId),//NOSONAR
        Boolean.class
    ));
  }

  public void insertNewMpiPatient(MpiPerson mpiPerson) throws JsonProcessingException {
    MpiPatientResponse mpiPatientResponse = createMpiPatientRecord(mpiPerson);
    String personReferenceId = findPersonReferenceId(mpiPerson.parent_id());
    NbsMpiLinkDto nbsMpiLinkDto = new NbsMpiLinkDto(personReferenceId, mpiPerson.parent_id(), mpiPatientResponse);
    linkNbsToMpi(nbsMpiLinkDto);
  }

  private MpiResponse createMpiPersonRecord(MpiPerson mpiPerson) throws JsonProcessingException {
    SeedRequest request = new SeedRequest(mpiPerson);
    String requestJson = objectMapper.writeValueAsString(request);
    return recordLinkageClient.post()
        .uri("/seed")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(requestJson)
        .retrieve()
        .body(MpiResponse.class);
  }

  private MpiPatientResponse createMpiPatientRecord(MpiPerson mpiPerson) throws JsonProcessingException {
    String personReferenceId = findPersonReferenceId(mpiPerson.parent_id());
    PatientCreateRequest request = new PatientCreateRequest(personReferenceId, mpiPerson);
    String requestJson = objectMapper.writeValueAsString(request);
    return recordLinkageClient.post()
        .uri("/patient")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(requestJson)
        .retrieve()
        .body(MpiPatientResponse.class);
  }

  private void linkNbsToMpi(NbsMpiLinkDto nbsMpiLink) {
    deduplicationTemplate.update(QueryConstants.NBS_MPI_QUERY,
        createParameterSource(nbsMpiLink, getPersonNameAndAddTime(nbsMpiLink.externalPersonId()).addTime()));
  }


  private String findPersonReferenceId(String personId) {
    return deduplicationTemplate.queryForObject(QueryConstants.MPI_PERSON_ID_QUERY,
        new MapSqlParameterSource("personId", personId),
        String.class);
  }

  private SqlParameterSource createParameterSource(NbsMpiLinkDto nbsMpiLink, LocalDateTime personAddTime) {
    return new MapSqlParameterSource()
        .addValue("person_uid", nbsMpiLink.externalPatientId())
        .addValue("person_parent_uid", nbsMpiLink.externalPersonId())
        .addValue("mpi_patient", nbsMpiLink.patientReferenceId())
        .addValue("mpi_person", nbsMpiLink.personReferenceId())
        .addValue("status", "U")
        .addValue("person_add_time", personAddTime);
  }


  private PatientNameAndTime getPersonNameAndAddTime(String personId) {
    return patientRecordService.fetchPersonNameAndAddTime(personId);
  }

}
