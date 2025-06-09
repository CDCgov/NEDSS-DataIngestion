package gov.cdc.nbs.deduplication.sync.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;


import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.sync.model.MpiPatientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PersonInsertSyncHandlerTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RestClient recordLinkageClient;

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;


  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec2;

  @Mock
  private RestClient.RequestBodySpec requestBodySpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  @InjectMocks
  private PersonInsertSyncHandler personInsertSyncHandler;


  @Test
  void testHandleInsert_PersonExists_DoNothing() throws JsonProcessingException {
    String personUid = "1234";
    JsonNode payloadNode = createPayloadNode(personUid, personUid);

    exist(personUid);

    personInsertSyncHandler.handleInsert(payloadNode);

    verify(patientRecordService, never()).fetchPersonRecord(anyString());
    verify(recordLinkageClient, never()).post();
  }

  @Test
  void testHandleInsert_PatientExists_DoNothing() throws JsonProcessingException {
    String personUid = "5678";
    String parentId = "1234";
    JsonNode payloadNode = createPayloadNode(personUid, parentId);

    exist(personUid);

    personInsertSyncHandler.handleInsert(payloadNode);

    verify(patientRecordService, never()).fetchPersonRecord(anyString());
    verify(recordLinkageClient, never()).post();
  }

  @Test
  void testHandleInsert_PersonDoesNotExist_InsertNewPerson() throws JsonProcessingException {
    String personUid = "1234";
    JsonNode payloadNode = createPayloadNode(personUid, personUid);
    MpiPerson mpiPerson = createMockMpiPerson(personUid, personUid);
    MpiResponse mpiResponse = createMockMpiResponse();

    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPerson);
    mockSeedApi(mpiResponse);
    mockLinkNbsToMpi(1);
    mockObjectMapperWriteValueAsString(seedRequestJson());
    mockFetchPersonNameAndAddTime();
    notExist(personUid);

    personInsertSyncHandler.handleInsert(payloadNode);

    verifyRestClientCalls("/seed");
    verifyLinkNbsToMpi(times(1));
  }

  @Test
  void testHandleInsert_PatientDoesNotExist_ParentExists_InsertNewPatient() throws JsonProcessingException {
    String personUid = "5678";
    String parentId = "1234";

    JsonNode payloadNode = createPayloadNode(personUid, parentId);
    MpiPerson mpiPatientObj = createMockMpiPerson(personUid, parentId);
    MpiPatientResponse mpiPatientResponse = new MpiPatientResponse("patient-ref-id", personUid);

    notExist(personUid);
    exist(parentId);
    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPatientObj);
    mockPatientApi(mpiPatientResponse);
    mockFindPersonReferenceId(parentId, "person-ref-id");
    mockLinkNbsToMpi(1);
    mockObjectMapperWriteValueAsString(patientRequestJson());
    mockFetchPersonNameAndAddTime();

    personInsertSyncHandler.handleInsert(payloadNode);

    verifyRestClientCalls("/patient");
    verifyLinkNbsToMpi(times(1));
  }

  @Test
  void testHandleInsert_PatientDoesNotExist_ParentDoesNotExist_InsertParentAndPatient() throws JsonProcessingException {
    String personUid = "5678";
    String parentId = "1234";
    JsonNode payloadNode = createPayloadNode(personUid, parentId);
    MpiPerson mpiPatientObj = createMockMpiPerson(personUid, parentId);
    MpiPerson mpiParentObj = createMockMpiPerson(parentId, parentId); // New person
    MpiResponse parentMpiResponse = createMockMpiResponse();
    MpiPatientResponse patientMpiResponse = new MpiPatientResponse("patient-ref-id", personUid);

    notExist(personUid);
    notExist(parentId);
    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPatientObj);
    mockPatientRecordServiceFetchPersonRecord(parentId, mpiParentObj);
    //  parent creation (seed API) and patient creation (patient API)
    mockSeedApiAndPatientApi(parentMpiResponse, patientMpiResponse);
    mockLinkNbsToMpi(2);
    mockFindPersonReferenceId(parentId, "parent-ref-id");
    mockObjectMapperWriteValueAsString("{}");
    mockFetchPersonNameAndAddTime();

    personInsertSyncHandler.handleInsert(payloadNode);

    verify(requestBodyUriSpec).uri("/seed");
    verify(requestBodyUriSpec2).uri("/patient");
  }

  private void exist(String personId) {
    mockDoesPatientExistInMpi(personId, true);
  }

  private void notExist(String personId) {
    mockDoesPatientExistInMpi(personId, false);
  }

  // Mocking Methods
  private void mockSeedApiAndPatientApi(MpiResponse parentMpiResponse, MpiPatientResponse patientMpiResponse) {
    // Mock Person creation (seed API call)
    when(recordLinkageClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/seed")).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(MpiResponse.class)).thenReturn(parentMpiResponse);

    // Mock patient creation (patient API call)
    RestClient.RequestBodySpec requestBodySpec2 = mock(RestClient.RequestBodySpec.class);
    RestClient.ResponseSpec responseSpec2 = mock(RestClient.ResponseSpec.class);

    when(recordLinkageClient.post())
        .thenReturn(requestBodyUriSpec)    // First call for Person
        .thenReturn(requestBodyUriSpec2);  // Second call for patient

    when(requestBodyUriSpec2.uri("/patient")).thenReturn(requestBodySpec2);
    when(requestBodySpec2.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec2);
    when(requestBodySpec2.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec2);
    when(requestBodySpec2.body(any(String.class))).thenReturn(requestBodySpec2);
    when(requestBodySpec2.retrieve()).thenReturn(responseSpec2);
    when(responseSpec2.body(MpiPatientResponse.class)).thenReturn(patientMpiResponse);
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

  private void mockDoesPatientExistInMpi(String personId, boolean exists) {
    when(deduplicationTemplate.queryForObject(
        eq(QueryConstants.MPI_PATIENT_EXISTS_CHECK),
        argThat((MapSqlParameterSource source) -> {
          Object value = source.getValue("personId");
          return value != null && personId.equals(value.toString());
        }),
        eq(Boolean.class)))
        .thenReturn(exists);
  }

  private void mockPatientRecordServiceFetchPersonRecord(String personUid, MpiPerson mpiPerson) {
    when(patientRecordService.fetchPersonRecord(personUid)).thenReturn(mpiPerson);
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
        eq(String.class))).thenReturn(personReferenceId);
  }

  private void mockLinkNbsToMpi(int times) {
    when(deduplicationTemplate.update(eq(QueryConstants.NBS_MPI_QUERY), any(SqlParameterSource.class)))
        .thenReturn(times);
  }


  private void mockObjectMapperWriteValueAsString(String json) throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn(json);
  }

  private void mockFetchPersonNameAndAddTime() {
    when(patientRecordService.fetchPersonNameAndAddTime(anyString()))
        .thenReturn(new PatientNameAndTime("123", "John Doe", LocalDateTime.now()));
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

  private void verifyLinkNbsToMpi(VerificationMode mode) {
    ArgumentCaptor<SqlParameterSource> parameterSourceCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
    verify(deduplicationTemplate, mode).update(
        eq(QueryConstants.NBS_MPI_QUERY),
        parameterSourceCaptor.capture());
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
        Collections.singletonList("race"),
        Collections.emptyList() // identifiers
    );
  }

  private MpiResponse createMockMpiResponse() {
    List<MpiResponse.Patient> patients = List.of(
        new MpiResponse.Patient("patient_ref_1", "1234"));

    List<MpiResponse.Person> persons = List.of(
        new MpiResponse.Person("person_ref_1", "1234", patients));

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
