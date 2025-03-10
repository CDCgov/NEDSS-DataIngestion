package gov.cdc.nbs.deduplication.sync.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.LinkResult;
import gov.cdc.nbs.deduplication.duplicates.model.MatchResponse;
import gov.cdc.nbs.deduplication.duplicates.service.DuplicateCheckService;
import gov.cdc.nbs.deduplication.duplicates.service.PatientRecordService;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.sync.model.MpiPatientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PersonInsertSyncHandlerTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RestClient recordLinkageClient;

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @Mock
  private DuplicateCheckService duplicateCheckService;

  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private RestClient.RequestBodySpec requestBodySpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  @InjectMocks
  private PersonInsertSyncHandler personInsertSyncHandler;

  @Test
  void testHandleInsert_NewPerson_possibleMatch() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("1234", "1234");
    MpiPerson mpiPerson = createMockMpiPerson("1234", "1234");
    MpiResponse mpiResponse = createMockMpiResponse();
    MatchResponse matchResponse = createMockMatchResponse(MatchResponse.Prediction.POSSIBLE_MATCH);

    mockPatientRecordServiceFetchPersonRecord("1234", mpiPerson);
    mockSeedApi(mpiResponse);
    mockLinkNbsToMpi();
    mockDuplicateCheckServiceFindDuplicateRecords(mpiPerson, matchResponse);
    mockInsertMatchCandidates();
    mockUpdateStatus();
    mockObjectMapperWriteValueAsString(seedRequestJson());

    // Act
    personInsertSyncHandler.handleInsert(payloadNode);

    // Assert
    verifyRestClientCalls("/seed");
    verifyLinkNbsToMpi();
    verifyInsertMatchCandidates();
    verifyUpdateStatus();
    verifyDuplicateCheckServiceCall(mpiPerson);
  }

  @Test
  void testHandleInsert_NewPerson_NoMatch() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("1234", "1234");
    MpiPerson mpiPerson = createMockMpiPerson("1234", "1234");
    MpiResponse mpiResponse = createMockMpiResponse();
    MatchResponse matchResponse = createMockMatchResponse(MatchResponse.Prediction.NO_MATCH);

    mockPatientRecordServiceFetchPersonRecord("1234", mpiPerson);
    mockSeedApi(mpiResponse);
    mockLinkNbsToMpi();
    mockDuplicateCheckServiceFindDuplicateRecords(mpiPerson, matchResponse);
    mockObjectMapperWriteValueAsString(seedRequestJson());

    // Act
    personInsertSyncHandler.handleInsert(payloadNode);

    // Assert
    verifyRestClientCalls("/seed");
    verifyLinkNbsToMpi();
    verifyDuplicateCheckServiceCall(mpiPerson);
  }

  @Test
  void testHandleInsert_NewPerson_MatchFound() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("1234", "1234");
    MpiPerson mpiPerson = createMockMpiPerson("1234", "1234");
    MpiResponse mpiResponse = createMockMpiResponse();
    MatchResponse matchResponse = createMockMatchResponse(MatchResponse.Prediction.MATCH);

    mockPatientRecordServiceFetchPersonRecord("1234", mpiPerson);
    mockSeedApi(mpiResponse);
    mockLinkNbsToMpi();
    mockDuplicateCheckServiceFindDuplicateRecords(mpiPerson, matchResponse);
    mockObjectMapperWriteValueAsString(seedRequestJson());

    // Act
    personInsertSyncHandler.handleInsert(payloadNode);

    // Assert
    verifyRestClientCalls("/seed");
    verifyLinkNbsToMpi();
    verifyDuplicateCheckServiceCall(mpiPerson);
  }

  @Test
  void testHandleInsert_ExistingPerson_InsertNewMpiPatient() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("5678", "1234");
    MpiPerson mpiPatientObj = createMockMpiPerson("5678", "1234");
    MpiPatientResponse mpiPatientResponse = new MpiPatientResponse("patient-ref-id", "5678");

    mockPatientRecordServiceFetchPersonRecord("5678", mpiPatientObj);
    mockPatientApi(mpiPatientResponse);
    mockFindPersonReferenceId("1234", "person-ref-id");
    mockLinkNbsToMpi();
    mockObjectMapperWriteValueAsString(patientRequestJson());

    // Act
    personInsertSyncHandler.handleInsert(payloadNode);

    // Assert
    verifyRestClientCalls("/patient");
    verifyLinkNbsToMpi();
  }


  // Mocking Methods

  private void mockPatientRecordServiceFetchPersonRecord(String personUid, MpiPerson mpiPerson) {
    when(patientRecordService.fetchPersonRecord(personUid)).thenReturn(mpiPerson);
  }

  private void mockSeedApi(MpiResponse mpiResponse) {
    when(recordLinkageClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/seed")).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(MpiResponse.class)).thenReturn(mpiResponse);
  }

  private void mockPatientApi(MpiPatientResponse mpiPatientResponse) {
    when(recordLinkageClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/patient")).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(MpiPatientResponse.class)).thenReturn(mpiPatientResponse);
  }

  private void mockFindPersonReferenceId(String personId, String personReferenceId) {
    when(deduplicationTemplate.queryForObject(
        eq(QueryConstants.MPI_PERSON_ID_QUERY),
        argThat((SqlParameterSource parameterSource) -> {
          String actualPersonId = (String) parameterSource.getValue("personId");
          return personId.equals(actualPersonId);
        }),
        eq(String.class))
    ).thenReturn(personReferenceId);
  }

  private void mockLinkNbsToMpi() {
    when(deduplicationTemplate.update(eq(QueryConstants.NBS_MPI_QUERY), any(SqlParameterSource.class)))
        .thenReturn(1);
  }

  private void mockDuplicateCheckServiceFindDuplicateRecords(MpiPerson mpiPerson, MatchResponse matchResponse) {
    when(duplicateCheckService.findDuplicateRecords(mpiPerson)).thenReturn(matchResponse);
  }

  private void mockInsertMatchCandidates() {
    when(deduplicationTemplate.batchUpdate(
        eq(QueryConstants.MATCH_CANDIDATES_QUERY),
        any(MapSqlParameterSource[].class))
    ).thenReturn(new int[] {1, 1});
  }

  private void mockUpdateStatus() {
    when(deduplicationTemplate.update(eq(QueryConstants.UPDATE_PROCESSED_PERSON), any(SqlParameterSource.class)))
        .thenReturn(1);
  }

  private void mockObjectMapperWriteValueAsString(String json) throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn(json);
  }

  // Verification Methods

  private void verifyRestClientCalls(String uri) {
    verify(recordLinkageClient).post();
    verify(requestBodyUriSpec).uri(uri);
    verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).accept(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).body(any(String.class));
    verify(requestBodySpec).retrieve();
    if (uri.equals("/seed")) {
      verify(responseSpec).body(MpiResponse.class);
    } else {// "/patient"
      verify(responseSpec).body(MpiPatientResponse.class);
    }
  }

  private void verifyLinkNbsToMpi() {
    ArgumentCaptor<SqlParameterSource> parameterSourceCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
    verify(deduplicationTemplate).update(
        eq(QueryConstants.NBS_MPI_QUERY),
        parameterSourceCaptor.capture()
    );
  }

  private void verifyInsertMatchCandidates() {
    verify(deduplicationTemplate).batchUpdate(
        eq(QueryConstants.MATCH_CANDIDATES_QUERY),
        any(MapSqlParameterSource[].class)
    );
  }

  private void verifyUpdateStatus() {
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_PROCESSED_PERSON),
        any(SqlParameterSource.class)
    );
  }

  private void verifyDuplicateCheckServiceCall(MpiPerson mpiPerson) {
    verify(duplicateCheckService).findDuplicateRecords(mpiPerson);
  }


  // Helper Methods

  private JsonNode createPayloadNode(String personUid, String personParentUid) {
    JsonNodeFactory factory = JsonNodeFactory.instance;
    return factory.objectNode()
        .set("after", factory.objectNode()
            .put("person_uid", personUid)
            .put("person_parent_uid", personParentUid));
  }

  private MpiPerson createMockMpiPerson(String personUid, String parentId) {
    return new MpiPerson(
        personUid,
        parentId,
        "2000-01-01",
        "M",
        Collections.emptyList(), // address
        Collections.emptyList(), // name
        Collections.emptyList(), // telecom
        "race",
        Collections.emptyList() // identifiers
    );
  }

  private MatchResponse createMockMatchResponse(MatchResponse.Prediction prediction) {
    List<LinkResult> linkResults = List.of(
        new LinkResult(UUID.randomUUID(), 3.5), // Simulate a possible match
        new LinkResult(UUID.randomUUID(), 7.2) // Simulate another possible match
    );
    if (prediction.equals(MatchResponse.Prediction.NO_MATCH)) {
      return new MatchResponse(
          prediction,
          null,
          null
      );
    } else {
      return new MatchResponse(
          prediction,
          UUID.randomUUID(),
          linkResults
      );
    }
  }

  private MpiResponse createMockMpiResponse() {
    List<MpiResponse.Patient> patients = List.of(
        new MpiResponse.Patient("patient_ref_1", "1234")
    );

    List<MpiResponse.Person> persons = List.of(
        new MpiResponse.Person("person_ref_1", "1234", patients)
    );

    return new MpiResponse(persons);
  }

  private String seedRequestJson() {
    return """
        {
          "external_id": "1234",
          "parent_id": "1234",
          "birth_date": "2000-01-01",
          "sex": "M",
          "address": [],
          "name": [],
          "telecom": [],
          "race": "mock_race",
          "identifiers": []
        }
        """;
  }

  private String patientRequestJson() {
    return """
        {
           "person_reference_id": "person_ref_id",
           "record": {
                   "external_id": "5678",
                   "parent_id": "1234",
                   "birth_date": "2000-01-01",
                   "sex": "M",
                   "address": [],
                   "name": [],
                   "telecom": [],
                   "race": "mock_race",
                   "identifiers": []
                 }
         }
        """;
  }

}
