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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;

import java.util.Collections;

import static org.junit.Assert.assertThrows;
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
  @Qualifier("nbsNamedTemplate")
  private NamedParameterJdbcTemplate nbsTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private RestClient.RequestBodySpec requestBodySpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  @InjectMocks
  private PersonUpdateSyncHandler personUpdateSyncHandler;

  @Test
  void testHandleUpdate_personTable() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("person_uid", "1234");
    String topic = "test.NBS_ODSE.dbo.Person";// the "Person_name" and "Person_race" are the same case
    MpiPerson mpiPerson = createMockMpiPerson("1234", "1234");

    mockPatientRecordServiceFetchPersonRecord("1234", mpiPerson);
    mockPatchPatientApi();
    mockObjectMapperWriteValueAsString(patientUpdateRequestJson());

    // Act
    personUpdateSyncHandler.handleUpdate(payloadNode, topic);

    // Assert
    verifyRestClientPatchCall();
    verifyPatientRecordServiceCall("1234");
  }

  @Test
  void testHandleUpdate_entityIdTable() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("entity_uid", "5678");
    String topic = "test.NBS_ODSE.dbo.Entity_id";
    MpiPerson mpiPerson = createMockMpiPerson("5678", "5678");

    // Mocking
    mockPatientRecordServiceFetchPersonRecord("5678", mpiPerson);
    mockPatchPatientApi();
    mockObjectMapperWriteValueAsString(patientUpdateRequestJson());

    // Act
    personUpdateSyncHandler.handleUpdate(payloadNode, topic);

    // Assert
    verifyRestClientPatchCall();
    verifyPatientRecordServiceCall("5678");
  }

  @Test
  void testHandleUpdate_postalLocatorTable() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("postal_locator_uid", "10000");
    String topic = "test.NBS_ODSE.dbo.Postal_locator";
    MpiPerson mpiPerson = createMockMpiPerson("1234", "1234");

    // Mocking
    mockNbsTemplateQueryForObject(QueryConstants.FETCH_PERSON_UID_BY_POSTAL_LOCATOR, "10000", "1234");
    mockPatientRecordServiceFetchPersonRecord("1234", mpiPerson);
    mockPatchPatientApi();
    mockObjectMapperWriteValueAsString(patientUpdateRequestJson());

    // Act
    personUpdateSyncHandler.handleUpdate(payloadNode, topic);

    // Assert
    verifyRestClientPatchCall();
    verifyPatientRecordServiceCall("1234");
  }

  @Test
  void testHandleUpdate_teleLocatorTable() throws JsonProcessingException {
    // Arrange
    JsonNode payloadNode = createPayloadNode("tele_locator_uid", "20000");
    String topic = "test.NBS_ODSE.dbo.Tele_locator";
    MpiPerson mpiPerson = createMockMpiPerson("1234", "1234");

    // Mocking
    mockNbsTemplateQueryForObject(QueryConstants.FETCH_PERSON_UID_BY_TELE_LOCATOR, "20000", "1234");
    mockPatientRecordServiceFetchPersonRecord("1234", mpiPerson);
    mockPatchPatientApi();
    mockObjectMapperWriteValueAsString(patientUpdateRequestJson());

    // Act
    personUpdateSyncHandler.handleUpdate(payloadNode, topic);

    // Assert
    verifyRestClientPatchCall();
    verifyPatientRecordServiceCall("1234");
  }

  @Test
  void testHandleUpdate_UnknownTableName() {
    // Arrange
    JsonNode payloadNode = createPayloadNode("field_name", "field_value");
    String topic = "test.NBS_ODSE.dbo.OutOfScopeTable";

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> personUpdateSyncHandler.handleUpdate(payloadNode, topic));
  }

  // Mocking Methods

  private void mockObjectMapperWriteValueAsString(String json) throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn(json);
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

  private void mockNbsTemplateQueryForObject(String query, String id, String personUid) {
    doReturn(personUid).when(nbsTemplate).queryForObject(
        eq(query),
        argThat((SqlParameterSource parameterSource) -> {
          String actualId = (String) parameterSource.getValue("id");
          return id.equals(actualId);
        }),
        eq(String.class));
  }

  private void verifyRestClientPatchCall() {
    verify(recordLinkageClient).patch();
    verify(requestBodyUriSpec).uri(anyString());
    verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).accept(MediaType.APPLICATION_JSON);
    verify(requestBodySpec).body(any(String.class));
    verify(requestBodySpec).retrieve();
    verify(responseSpec).body(MpiResponse.class);
  }

  private void verifyPatientRecordServiceCall(String personUid) {
    verify(patientRecordService).fetchPersonRecord(personUid);
  }

  // Helper Methods

  private String patientUpdateRequestJson() {
    return """
        {
         "external_person_id": "12345",
         "record": {
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
         }
        """;
  }

  private JsonNode createPayloadNode(String fieldName, String fieldValue) {
    JsonNodeFactory factory = JsonNodeFactory.instance;
    return factory.objectNode()
        .set("after", factory.objectNode()
            .put(fieldName, fieldValue));
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
}
