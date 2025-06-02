package gov.cdc.nbs.deduplication.sync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.sync.model.PatientUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PersonUpdateSyncHandler {


  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;
  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;
  private final PersonInsertSyncHandler personInsertSyncHandler;

  public PersonUpdateSyncHandler(
      ObjectMapper objectMapper,
      @Qualifier("recordLinkerRestClient") RestClient recordLinkageClient,
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      final PatientRecordService patientRecordService,
      final PersonInsertSyncHandler personInsertSyncHandler) {
    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
    this.deduplicationTemplate = deduplicationTemplate;
    this.patientRecordService = patientRecordService;
    this.personInsertSyncHandler = personInsertSyncHandler;
  }

  public void handleUpdate(JsonNode payloadNode) throws JsonProcessingException {
    JsonNode afterNode = payloadNode.path("after");
    String personUid = afterNode.get("person_uid").asText();
    MpiPerson mpiPerson = patientRecordService.fetchPersonRecord(personUid);
    handleUpdateExistingPatient(mpiPerson);
  }


  private void handleUpdateExistingPatient(MpiPerson mpiPerson) throws JsonProcessingException {
    boolean patientExists = doesPatientExistInMpi(mpiPerson.external_id());
    boolean isPerson = isPersonRecord(mpiPerson);

    if (patientExists) {
      updateExistingPatient(mpiPerson);
    } else {
      if (isPerson) {
        insertNewPerson(mpiPerson);
      } else {//patient
        boolean parentExists = doesPatientExistInMpi(mpiPerson.parent_id());
        if (parentExists) {
          insertNewPatient(mpiPerson);
        } else {
          insertParentAndPatient(mpiPerson);
        }
      }
    }
  }

  private boolean doesPatientExistInMpi(String personId) {
    return Boolean.TRUE.equals(deduplicationTemplate.queryForObject(
        QueryConstants.MPI_PATIENT_EXISTS_CHECK,
        new MapSqlParameterSource("personId", personId),//NOSONAR
        Boolean.class
    ));
  }

  private boolean isPersonRecord(MpiPerson mpiPerson) {
    return mpiPerson.external_id().equals(mpiPerson.parent_id());
  }

  private void insertNewPerson(MpiPerson person) throws JsonProcessingException {
    personInsertSyncHandler.insertNewMpiPerson(person);
  }

  private void insertNewPatient(MpiPerson patient) throws JsonProcessingException {
    personInsertSyncHandler.insertNewMpiPatient(patient);
  }

  private void insertParentAndPatient(MpiPerson patient) throws JsonProcessingException {
    personInsertSyncHandler.insertParentAndPatient(patient);
  }


  private void updateExistingPatient(MpiPerson mpiPerson) throws JsonProcessingException {
    String personReferenceId = getPersonReferenceIdByParentId(mpiPerson.parent_id());
    String patientReferenceId = getPatientReferenceIdByPersonId(mpiPerson.external_id());
    PatientUpdateRequest request = new PatientUpdateRequest(personReferenceId, mpiPerson);
    String requestJson = objectMapper.writeValueAsString(request);
    String uri = UriComponentsBuilder.fromUriString("/patient/{patient_reference_id}")
        .buildAndExpand(patientReferenceId)
        .toUriString();
    recordLinkageClient.patch()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(requestJson)
        .retrieve()
        .body(MpiResponse.class);
  }



  private String getPersonReferenceIdByParentId(String personId) {
    return deduplicationTemplate.queryForObject(QueryConstants.MPI_PERSON_ID_QUERY,
        new MapSqlParameterSource("personId", personId),
        String.class);
  }

  private String getPatientReferenceIdByPersonId(String personId) {
    return deduplicationTemplate.queryForObject(QueryConstants.MPI_PATIENT_ID_QUERY,
        new MapSqlParameterSource("personId", personId),
        String.class);
  }

}
