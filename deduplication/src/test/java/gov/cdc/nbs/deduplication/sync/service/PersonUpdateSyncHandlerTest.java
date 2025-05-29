package gov.cdc.nbs.deduplication.sync.service;

import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestClient;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


@ExtendWith(MockitoExtension.class)
class PersonUpdateSyncHandlerTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RestClient recordLinkageClient;

  @Mock
  @Qualifier("deduplicationNamedTemplate")
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private RestClient.RequestBodySpec requestBodySpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  @Mock
  private PersonInsertSyncHandler personInsertSyncHandler;

  @InjectMocks
  private PersonUpdateSyncHandler personUpdateSyncHandler;


  @Test
  void testHandleUpdate_patientExists_isPatient_updateExisting() throws JsonProcessingException {
    String personUid = "101";
    String parentUid = "100";
    MpiPerson mpiPerson = createMockMpiPerson(personUid, parentUid);

    setupCommonMocksForPatientExists(personUid, mpiPerson);

    runHandleUpdate(personUid);

    verifyRestClientPatchCall();
    verify(personInsertSyncHandler, never()).insertNewMpiPerson(any());
    verify(personInsertSyncHandler, never()).insertNewMpiPatient(any());
    verify(personInsertSyncHandler, never()).insertParentAndPatient(any());
  }

  @Test
  void testHandleUpdate_patientExists_isPerson_updateExisting() throws JsonProcessingException {
    String personUid = "100";
    String parentUid = "100";
    MpiPerson mpiPerson = createMockMpiPerson(personUid, parentUid);

    setupCommonMocksForPatientExists(personUid, mpiPerson);

    runHandleUpdate(personUid);

    verifyRestClientPatchCall();
    verify(personInsertSyncHandler, never()).insertNewMpiPerson(any());
    verify(personInsertSyncHandler, never()).insertNewMpiPatient(any());
    verify(personInsertSyncHandler, never()).insertParentAndPatient(any());
  }


  @Test
  void testHandleUpdate_patientDoesNotExists_isPerson_insertNewPerson() throws JsonProcessingException {
    String personUid = "100";

    MpiPerson mpiPerson = createMockMpiPerson(personUid, personUid);//isPerson
    notExist(personUid);// Patient does NOT exist
    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPerson);

    runHandleUpdate(personUid);
    verifyInsertNewPersonWasCalled(mpiPerson);
  }

  @Test
  void testHandleUpdate_patientDoesNotExists_isPatient_parentExists_insertNewPatient() throws JsonProcessingException {
    String personUid = "101";
    String parentUid = "100";

    MpiPerson mpiPerson = createMockMpiPerson(personUid, parentUid);
    notExist(personUid);// Patient does NOT exist
    exist(parentUid); // parent exists
    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPerson);

    runHandleUpdate(personUid);
    verifyInsertNewPatientWasCalled(mpiPerson);
  }

  @Test
  void testHandleUpdate_patientDoesNotExists_isPatient_parentDoesNotExist_insertParentAndPatient()
      throws JsonProcessingException {
    String personUid = "101";
    String parentUid = "100";
    MpiPerson mpiPerson = createMockMpiPerson(personUid, parentUid);

    mockDoesPatientExistInMpi(personUid, false); // Patient does NOT exist
    mockDoesPatientExistInMpi(parentUid, false); // parent does NOT exist
    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPerson);

    runHandleUpdate(personUid);
    verifyInsertParentAndPatientWasCalled(mpiPerson);
  }


  private void runHandleUpdate(String personUid) throws JsonProcessingException {
    JsonNode payloadNode = createPayloadNode(personUid);
    personUpdateSyncHandler.handleUpdate(payloadNode);
  }


  // Mocking Methods
  private void setupCommonMocksForPatientExists(String personUid, MpiPerson mpiPerson) throws JsonProcessingException {
    mockDoesPatientExistInMpi(personUid, true); // Patient exists
    mockPatientRecordServiceFetchPersonRecord(personUid, mpiPerson);
    mockPatchPatientApi();
    mockObjectMapperWriteValueAsString(patientUpdateRequestJson());
  }

  private void exist(String personId) {
    mockDoesPatientExistInMpi(personId, true);
  }

  private void notExist(String personId) {
    mockDoesPatientExistInMpi(personId, false);
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

  private void mockPatchPatientApi() {
    when(recordLinkageClient.patch()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(MpiResponse.class)).thenReturn(null);
  }

  private void mockObjectMapperWriteValueAsString(String json) throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn(json);
  }

  // Verification Methods

  private void verifyRestClientPatchCall() {
    verify(recordLinkageClient).patch();
    verify(requestBodyUriSpec).uri(anyString());
    verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).accept(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).body(any(String.class));
    verify(requestBodySpec).retrieve();
    verify(responseSpec).body(MpiResponse.class);
  }

  private void verifyInsertNewPersonWasCalled(MpiPerson mpiPerson) throws JsonProcessingException {
    verify(personInsertSyncHandler).insertNewMpiPerson(mpiPerson);
    verifyNoMoreInteractions(personInsertSyncHandler);
  }

  private void verifyInsertNewPatientWasCalled(MpiPerson mpiPerson) throws JsonProcessingException {
    verify(personInsertSyncHandler).insertNewMpiPatient(mpiPerson);
    verifyNoMoreInteractions(personInsertSyncHandler);
  }

  private void verifyInsertParentAndPatientWasCalled(MpiPerson mpiPerson) throws JsonProcessingException {
    verify(personInsertSyncHandler).insertParentAndPatient(mpiPerson);
    verifyNoMoreInteractions(personInsertSyncHandler);
  }

  // Helper Methods
  private String patientUpdateRequestJson() {
    return """
        {
         "external_person_id": "12345",
         "record": {
                 "external_id": "1234",
                 "parentUid": "1234",
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

  private JsonNode createPayloadNode(String personUid) {
    JsonNodeFactory factory = JsonNodeFactory.instance;
    return factory.objectNode()
        .set("after", factory.objectNode()
            .put("person_uid", personUid));
  }

  private MpiPerson createMockMpiPerson(String externalId, String parentId) {
    return new MpiPerson(
        externalId,
        parentId,
        "2000-01-01",
        "M",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.singletonList("race"),
        Collections.emptyList()
    );
  }


}
