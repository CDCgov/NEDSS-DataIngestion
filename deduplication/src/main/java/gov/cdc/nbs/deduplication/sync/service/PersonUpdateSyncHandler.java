package gov.cdc.nbs.deduplication.sync.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.service.PatientRecordService;
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

  private final NamedParameterJdbcTemplate nbsTemplate;
  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;
  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;

  public PersonUpdateSyncHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate,
      ObjectMapper objectMapper,
      @Qualifier("recordLinkageRestClient") RestClient recordLinkageClient,
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      final PatientRecordService patientRecordService
  ) {
    this.nbsTemplate = nbsTemplate;
    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
    this.deduplicationTemplate = deduplicationTemplate;
    this.patientRecordService = patientRecordService;
  }

  public void handleUpdate(JsonNode payloadNode, String topic) throws JsonProcessingException {
    JsonNode afterNode = payloadNode.path("after");
    String personUid = extractPersonUidFromTopic(afterNode, topic);
    MpiPerson mpiPerson = patientRecordService.fetchPersonRecord(personUid);
    updateExistingPatient(mpiPerson);
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

  private String extractPersonUidFromTopic(JsonNode afterNode, String topic) {
    String[] topicParts = topic.split("\\.");
    String tableName = topicParts[topicParts.length - 1];
    return switch (tableName) {
      case "Person", "Person_name", "Person_race" -> afterNode.get("person_uid").asText();
      case "Entity_id" -> afterNode.get("entity_uid").asText();
      case "Postal_locator" -> {
        String postalLocatorUid = afterNode.get("postal_locator_uid").asText();
        yield findPersonUidFromQuery(QueryConstants.FETCH_PERSON_UID_BY_POSTAL_LOCATOR, postalLocatorUid);
      }
      case "Tele_locator" -> {
        String teleLocatorUid = afterNode.get("tele_locator_uid").asText();
        yield findPersonUidFromQuery(QueryConstants.FETCH_PERSON_UID_BY_TELE_LOCATOR, teleLocatorUid);
      }
      default -> throw new IllegalArgumentException("Unknown table name: " + tableName);
    };
  }

  private String findPersonUidFromQuery(String query, String id) {
    return nbsTemplate.queryForObject(query, new MapSqlParameterSource("id", id), String.class);
  }

}
