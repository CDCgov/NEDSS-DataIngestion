package gov.cdc.nbs.deduplication.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.List;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import gov.cdc.nbs.deduplication.matching.exception.MatchException;
import gov.cdc.nbs.deduplication.matching.model.CreatePersonRequest;
import gov.cdc.nbs.deduplication.matching.model.CreatePersonResponse;
import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.matching.model.LinkResponse;
import gov.cdc.nbs.deduplication.matching.model.LinkResponse.Results;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.RelateRequest;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

  @Mock
  private RestClient restClient;

  @Mock
  private RequestBodyUriSpec uriSpec;

  @Mock
  private RequestBodySpec bodySpec;

  @Mock
  private ResponseSpec responseSpec;

  @Mock
  private NamedParameterJdbcTemplate template;

  @InjectMocks
  private MatchService matchService;

  @Test
  void testNoMatchFound() {
    PersonMatchRequest matchRequest = new PersonMatchRequest(
        null,
        null,
        null,
        null,
        null,
        null);

    mockClientMatchCall(new LinkResponse(
        "patientReferenceId",
        "personReferenceId",
        "no_match",
        null));

    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.NONE);
    assertThat(response.match()).isNull();
    assertThat(response.linkResponse().patient_reference_id()).isEqualTo("patientReferenceId");
    assertThat(response.linkResponse().person_reference_id()).isEqualTo("personReferenceId");
    assertThat(response.linkResponse().match_grade()).isEqualTo("no_match");
    assertThat(response.linkResponse().results()).isNull();
  }

  @Test
  void testMatchFound() {
    PersonMatchRequest matchRequest = new PersonMatchRequest(
        null,
        null,
        null,
        null,
        null,
        null);

    mockClientMatchCall(new LinkResponse(
        "patientReferenceId",
        "personReferenceId",
        "certain",
        null));

    ArgumentCaptor<SqlParameterSource> captor = ArgumentCaptor.forClass(SqlParameterSource.class);
    when(template.queryForObject(
        Mockito.anyString(),
        captor.capture(),
        Mockito.eq(Long.class)))
        .thenReturn(99L);

    MatchResponse response = matchService.match(matchRequest);

    assertThat(captor.getValue().getValue("mpi_person")).isEqualTo("personReferenceId");

    assertThat(response.matchType()).isEqualTo(MatchType.EXACT);
    assertThat(response.match()).isEqualTo(99L);
    assertThat(response.linkResponse().patient_reference_id()).isEqualTo("patientReferenceId");
    assertThat(response.linkResponse().person_reference_id()).isEqualTo("personReferenceId");
    assertThat(response.linkResponse().match_grade()).isEqualTo("certain");
    assertThat(response.linkResponse().results()).isNull();
  }

  @Test
  void testPossibleMatchFound() {
    PersonMatchRequest matchRequest = new PersonMatchRequest(
        null,
        null,
        null,
        null,
        null,
        null);

    mockClientMatchCall(new LinkResponse(
        "patientReferenceId",
        "personReferenceId",
        "possible",
        null));

    mockClientPatientUpdateCall("patientReferenceId",
        new CreatePersonResponse(
            "newPersonReference",
            "externalPersonId"));

    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.POSSIBLE);
    assertThat(response.match()).isNull();
    assertThat(response.linkResponse().patient_reference_id()).isEqualTo("patientReferenceId");
    assertThat(response.linkResponse().person_reference_id()).isEqualTo("newPersonReference");
    assertThat(response.linkResponse().match_grade()).isEqualTo("possible");
    assertThat(response.linkResponse().results()).isNull();
  }

  @Test
  void testNullResponse() {
    PersonMatchRequest matchRequest = new PersonMatchRequest(
        null,
        null,
        null,
        null,
        null,
        null);

    mockClientMatchCall(null);

    MatchException exception = assertThrows(MatchException.class, () -> matchService.match(matchRequest));
    assertThat(exception.getMessage()).isEqualTo("Match response from Record Linkage is null");
  }

  @Test
  void testPossibleNullResponse() {
    PersonMatchRequest matchRequest = new PersonMatchRequest(
        null,
        null,
        null,
        null,
        null,
        null);

    mockClientMatchCall(new LinkResponse(
        "patientReferenceId",
        "personReferenceId",
        "possible",
        null));

    mockClientPatientUpdateCall("patientReferenceId", null);

    MatchException exception = assertThrows(MatchException.class, () -> matchService.match(matchRequest));
    assertThat(exception.getMessage())
        .isEqualTo("Record Linkage failed to create new entry for patient: patientReferenceId");

  }

  @Test
  void testMatchEndpointIsCalled() {
    PersonMatchRequest.PersonDto person = new PersonMatchRequest.PersonDto(
            Timestamp.valueOf("1980-01-01 00:00:00"), // birthTime
            "F", // sex
            null // additional gender
    );

    List<PersonMatchRequest.PersonNameDto> names = List.of(
            new PersonMatchRequest.PersonNameDto(
                    "JOHN",    // fist name
                    "MIDDLE",   // middle name
                    "DOE",
                    null
            )
    );

    List<PersonMatchRequest.PersonRaceDto> races = List.of(
            new PersonMatchRequest.PersonRaceDto(
                    "1002-5" // raceCode
            )
    );

    List<PersonMatchRequest.PostalLocatorDto> postalLocators = List.of(
            new PersonMatchRequest.PostalLocatorDto(
                    "123 Main St", // streetAddress
                    "Suit 11",     // street address 2
                    "Onionville",          // city
                    "Onion",       // state
                    "00000",           // zip
                    null          // cty code
            )
    );

    List<PersonMatchRequest.TeleLocatorDto> teleLocators = List.of(
            new PersonMatchRequest.TeleLocatorDto(
                    "5551234567"// phoneNumber
            )
    );

    List<PersonMatchRequest.EntityIdDto> identifications = List.of(
            new PersonMatchRequest.EntityIdDto(
                    "123-45-6789", // idNumber
                    "SSN",         // idType
                    null           // assigningAuthority
            )
    );

    PersonMatchRequest matchRequest = new PersonMatchRequest(
            person,
            names,
            races,
            postalLocators,
            teleLocators,
            identifications
    );

    LinkResponse linkResponse = new LinkResponse(
            "pat-001",
            "per-001",
            "certain",
            null);

    // Set up the /match call
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/match")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(any(LinkRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(LinkResponse.class)).thenReturn(linkResponse);

    // Mock the database response
    when(template.queryForObject(
            Mockito.anyString(),
            any(SqlParameterSource.class),
            Mockito.eq(Long.class)))
            .thenReturn(123L);

    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.EXACT);
    assertThat(response.match()).isEqualTo(123L);
    assertThat(response.linkResponse().person_reference_id()).isEqualTo("per-001");
  }


  private void mockClientMatchCall(LinkResponse response) {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/match")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(any(LinkRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(LinkResponse.class)).thenReturn(response);
  }

  private void mockClientLinkCall(LinkResponse response) {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/link")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(any(LinkRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(LinkResponse.class)).thenReturn(response);
  }

  private void mockClientPatientUpdateCall(String patientId, CreatePersonResponse response) {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/person")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(new CreatePersonRequest(List.of(patientId)))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(CreatePersonResponse.class)).thenReturn(response);
  }

  @Test
  void testRelateNbsToMpiExact() {
    ArgumentCaptor<SqlParameterSource> captor = ArgumentCaptor.forClass(SqlParameterSource.class);

    MpiPerson mockPerson = new MpiPerson(
            "personRef",
            "1",
            "1980-01-01",
            "M",
            List.of(new MpiPerson.Address(
                    List.of("123 Main St"), "Atlanta", "GA", "30303", "Fulton")),
            List.of(new MpiPerson.Name(
                    List.of("John", "A."), "Doe", List.of("Jr."))),
            List.of(new MpiPerson.Telecom("555-123-4567")),
            List.of("White", "Not Hispanic or Latino"),
            List.of(new MpiPerson.Identifier("SS", "123-45-6789", "SSA"))
    );

    // Spy the MatchService to mock sendLinkRequest
    MatchService matchServiceSpy = Mockito.spy(matchService);

    LinkResponse mockLinkResponse = new LinkResponse(
            "patientRef",
            "personRef",
            "match",
            null
    );

    doReturn(mockLinkResponse)
            .when(matchServiceSpy)
            .sendLinkRequest(any(LinkRequest.class));

    when(template.update(Mockito.anyString(), captor.capture())).thenReturn(1);

    matchServiceSpy.relateNbsIdToMpiId(new RelateRequest(
            1L,
            1L,
            MatchType.EXACT,
            mockLinkResponse,
            mockPerson
    ));

    assertThat(captor.getAllValues()).hasSize(1);
    assertThat(captor.getValue().getValue("person_uid")).isEqualTo(1L);
    assertThat(captor.getValue().getValue("person_parent_uid")).isEqualTo(1L);
    assertThat(captor.getValue().getValue("mpi_patient")).isEqualTo("patientRef");
    assertThat(captor.getValue().getValue("mpi_person")).isEqualTo("personRef");
    assertThat(captor.getValue().getValue("status")).isEqualTo("P");
  }


  @Test
  void testRelateNbsToMpiPossible() {
    ArgumentCaptor<SqlParameterSource> captor = ArgumentCaptor.forClass(SqlParameterSource.class);

    LinkResponse mockLinkResponse = new LinkResponse(
            "patientRef",
            "personRef",
            "certain",
            List.of(new Results(
                    "abcd",
                    0.5,
                    "pass label",
                    0.5,
                    0.3,
                    0.7,
                    "certain"))
    );

    // Use a spy to mock just sendLinkRequest
    MatchService matchServiceSpy = Mockito.spy(matchService);

    doReturn(mockLinkResponse)
            .when(matchServiceSpy)
            .sendLinkRequest(any(LinkRequest.class));

    when(template.update(Mockito.anyString(), captor.capture())).thenReturn(1);

    matchServiceSpy.relateNbsIdToMpiId(new RelateRequest(
            1L,
            1L,
            MatchType.POSSIBLE,
            mockLinkResponse,
            null
    ));

    List<SqlParameterSource> sqlParams = captor.getAllValues();
    assertThat(sqlParams).hasSize(2);

    // Verify Link MPI insert
    assertThat(sqlParams.get(0).getValue("person_uid")).isEqualTo(1L);
    assertThat(sqlParams.get(0).getValue("person_parent_uid")).isEqualTo(1L);
    assertThat(sqlParams.get(0).getValue("mpi_patient")).isEqualTo("patientRef");
    assertThat(sqlParams.get(0).getValue("mpi_person")).isEqualTo("personRef");
    assertThat(sqlParams.get(0).getValue("status")).isEqualTo("R");

    // Verify Possible Match insert
    assertThat(sqlParams.get(1).getValue("person_uid")).isEqualTo(1L);
    assertThat(sqlParams.get(1).getValue("mpi_person_id")).isEqualTo("abcd");
  }


  @Test
  void testRelateNbsToMpiPossibleEmptyList() {
    LinkResponse linkResponse = new LinkResponse(
        "patientRef",
        "personRef",
        "match",
        List.of());

    RelateRequest request = new RelateRequest(
        1l,
        1l,
        MatchType.POSSIBLE,
        linkResponse,
    null);
    assertThrows(NullPointerException.class, () -> matchService.relateNbsIdToMpiId(request));
  }

  @Test
  void testRelateNbsToMpiPossibleNullList() {
    LinkResponse linkResponse = new LinkResponse(
        "patientRef",
        "personRef",
        "match",
        null);

    RelateRequest request = new RelateRequest(
        1l,
        1l,
        MatchType.POSSIBLE,
        linkResponse, null);
    assertThrows(NullPointerException.class, () -> matchService.relateNbsIdToMpiId(request));
  }

}
