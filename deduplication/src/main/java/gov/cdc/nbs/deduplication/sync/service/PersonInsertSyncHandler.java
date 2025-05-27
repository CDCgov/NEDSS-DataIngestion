package gov.cdc.nbs.deduplication.sync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.batch.model.LinkResult;
import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.model.MatchResponse;
import gov.cdc.nbs.deduplication.batch.service.DuplicateCheckService;
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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class PersonInsertSyncHandler {

  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;
  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final DuplicateCheckService duplicateCheckService;
  private final PatientRecordService patientRecordService;

  public PersonInsertSyncHandler(
      ObjectMapper objectMapper,
      @Qualifier("recordLinkerRestClient") RestClient recordLinkageClient,
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      final DuplicateCheckService duplicateCheckService,
      final PatientRecordService patientRecordService) {

    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
    this.deduplicationTemplate = deduplicationTemplate;
    this.duplicateCheckService = duplicateCheckService;
    this.patientRecordService = patientRecordService;
  }

  public void handleInsert(JsonNode payloadNode) throws JsonProcessingException {
    JsonNode afterNode = payloadNode.path("after");
    String personUid = afterNode.get("person_uid").asText();//NOSONAR
    String personParentUid = afterNode.get("person_parent_uid").asText();
    MpiPerson mpiPerson = patientRecordService.fetchPersonRecord(personUid);

    boolean isNewPerson = personUid.equals(personParentUid);
    if (isNewPerson) {
      insertNewMpiPerson(mpiPerson);
    } else {
      insertNewMpiPatient(mpiPerson);
    }
  }

  private void insertNewMpiPerson(MpiPerson mpiPerson) throws JsonProcessingException {
    MpiResponse mpiResponse = insertNewPersonIntoMpi(mpiPerson);
    linkNbsToMpi(new NbsMpiLinkDto(mpiResponse));
    MatchCandidate matchCandidate = checkForPossibleMatch(mpiPerson);
    if (matchCandidate.possibleMatchList() != null) {
      insertMatchCandidates(matchCandidate);
    }
    updateStatus(matchCandidate.personUid());
  }

  private void insertNewMpiPatient(MpiPerson mpiPerson) throws JsonProcessingException {
    MpiPatientResponse mpiPatientResponse = insertNewPatientIntoMpi(mpiPerson);
    String personReferenceId = findPersonReferenceId(mpiPerson.parent_id());
    NbsMpiLinkDto nbsMpiLinkDto = new NbsMpiLinkDto(personReferenceId, mpiPerson.parent_id(), mpiPatientResponse);
    linkNbsToMpi(nbsMpiLinkDto);
  }

  private MpiResponse insertNewPersonIntoMpi(MpiPerson mpiPerson) throws JsonProcessingException {
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

  private MpiPatientResponse insertNewPatientIntoMpi(MpiPerson mpiPerson) throws JsonProcessingException {
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

  private MatchCandidate checkForPossibleMatch(MpiPerson mpiPerson) {
    MatchResponse matchResponse = duplicateCheckService.findDuplicateRecords(mpiPerson);
    if (MatchResponse.Prediction.POSSIBLE_MATCH == matchResponse.prediction()) {
      List<String> possibleMatchList = matchResponse.results().stream()
          .map(LinkResult::personReferenceId)
          .map(UUID::toString)
          .toList();
      return new MatchCandidate(mpiPerson.external_id(), possibleMatchList);
    }
    return new MatchCandidate(mpiPerson.external_id(), null);
  }

  private void insertMatchCandidates(MatchCandidate candidate) {
    // Step 1: Insert into matches_requiring_review
    Long matchId = insertMatchGroup(candidate.personUid());

    // Step 2: Insert each potential match
    List<String> potentialNbsIds = getPersonIdsByMpiIds(candidate.possibleMatchList());
    for (String potentialNbsId : potentialNbsIds) {
      insertMatchCandidate(matchId, Long.valueOf(potentialNbsId));
    }
  }

  private Long insertMatchGroup(String personId) {
    PatientNameAndTime patientNameAndTime = getPersonNameAndAddTime(personId);
    MapSqlParameterSource groupParams = new MapSqlParameterSource()
        .addValue("personUid", personId)
        .addValue("personName", patientNameAndTime.name())
        .addValue("personAddTime", patientNameAndTime.addTime())
        .addValue("identifiedDate", getCurrentDate());

    KeyHolder keyHolder = new GeneratedKeyHolder();

    deduplicationTemplate.update(
        QueryConstants.INSERT_MATCH_GROUP,
        groupParams,
        keyHolder
    );

    Number matchGroupId = keyHolder.getKey();
    return matchGroupId != null ? matchGroupId.longValue() : null;
  }

  private void insertMatchCandidate(Long matchId, Long personUid) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("matchId", matchId)
        .addValue("personUid", personUid);

    deduplicationTemplate.update(QueryConstants.INSERT_MATCH_CANDIDATE, params);
  }

  private void updateStatus(String personId) {
    deduplicationTemplate.update(QueryConstants.UPDATE_PROCESSED_PERSON,
        new MapSqlParameterSource("personId", personId));
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

  private List<String> getPersonIdsByMpiIds(List<String> mpiIds) {
    return deduplicationTemplate.query(
        QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS,
        new MapSqlParameterSource("mpiIds", mpiIds),
        (rs, rowNum) -> rs.getString("person_uid"));
  }

  private PatientNameAndTime getPersonNameAndAddTime(String personId) {
    return patientRecordService.fetchPersonNameAndAddTime(personId);
  }

  private String getCurrentDate() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }



}
