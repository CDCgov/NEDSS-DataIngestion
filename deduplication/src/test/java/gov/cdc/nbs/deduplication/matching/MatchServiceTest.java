package gov.cdc.nbs.deduplication.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.RelateRequest;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;

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

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private MatchService matchService;

  @BeforeEach
  void setup() {
    matchService = new MatchService(restClient, template, nbsTemplate);
  }

  @Test
  void testNoMatchFound() {
    PersonMatchRequest matchRequest = new PersonMatchRequest(
        null,
        null,
        null,
        null,
        null,
        null);

    mockClientLinkCall(new LinkResponse(
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

    mockClientLinkCall(new LinkResponse(
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

    mockClientLinkCall(new LinkResponse(
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

    mockClientLinkCall(null);

    MatchException exception = assertThrows(MatchException.class, () -> matchService.match(matchRequest));
    assertThat(exception.getMessage()).isEqualTo("Link response from Record Linkage is null");
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

    mockClientLinkCall(new LinkResponse(
        "patientReferenceId",
        "personReferenceId",
        "possible",
        null));

    mockClientPatientUpdateCall("patientReferenceId", null);

    MatchException exception = assertThrows(MatchException.class, () -> matchService.match(matchRequest));
    assertThat(exception.getMessage())
        .isEqualTo("Record Linkage failed to create new entry for patient: patientReferenceId");

  }

  private void mockClientLinkCall(LinkResponse response) {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/link")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.any(LinkRequest.class))).thenReturn(bodySpec);
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

    when(template.update(Mockito.anyString(), captor.capture())).thenReturn(1);
    matchService.relateNbsIdToMpiId(new RelateRequest(
        1l,
        1l,
        MatchType.EXACT,
        new LinkResponse(
            "patientRef",
            "personRef",
            "match",
            null)));

    assertThat(captor.getAllValues()).hasSize(1);
    assertThat(captor.getValue().getValue("person_uid")).isEqualTo(1l);
    assertThat(captor.getValue().getValue("person_parent_uid")).isEqualTo(1l);
    assertThat(captor.getValue().getValue("mpi_patient")).isEqualTo("patientRef");
    assertThat(captor.getValue().getValue("mpi_person")).isEqualTo("personRef");
    assertThat(captor.getValue().getValue("status")).isEqualTo("P");
  }

  @Test
  void testRelateNbsToMpiPossible() {
    LocalDateTime now = LocalDateTime.now();
    // Link mpi values
    ArgumentCaptor<SqlParameterSource> linkCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
    when(template.update(eq(MatchService.LINK_NBS_MPI_QUERY), linkCaptor.capture())).thenReturn(1);

    // fetch patient name and add time
    when(nbsTemplate.query(
        eq(MatchService.FIND_NBS_ADD_TIME_AND_NAME_QUERY),
        Mockito.any(MapSqlParameterSource.class),
        ArgumentMatchers.<RowMapper<PatientNameAndTime>>any()))
        .thenReturn(List.of(new PatientNameAndTime("patient name", now)));

    // insert into match_candidate table
    ArgumentCaptor<SqlParameterSource> captor = ArgumentCaptor.forClass(SqlParameterSource.class);
    when(template.update(eq(MatchService.INSERT_POSSIBLE_MATCH), captor.capture())).thenReturn(1);

    matchService.relateNbsIdToMpiId(new RelateRequest(
        1l,
        1l,
        MatchType.POSSIBLE,
        new LinkResponse(
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
                "certain")))));

    List<SqlParameterSource> linkParams = linkCaptor.getAllValues();
    assertThat(linkParams).hasSize(1);
    // Link MPI query
    assertThat(linkParams.get(0).getValue("person_uid")).isEqualTo(1l);
    assertThat(linkParams.get(0).getValue("person_parent_uid")).isEqualTo(1l);
    assertThat(linkParams.get(0).getValue("mpi_patient")).isEqualTo("patientRef");
    assertThat(linkParams.get(0).getValue("mpi_person")).isEqualTo("personRef");
    assertThat(linkParams.get(0).getValue("status")).isEqualTo("R");

    // Persist possible matches
    List<SqlParameterSource> matchCandidateParams = captor.getAllValues();
    assertThat(matchCandidateParams).hasSize(1);
    assertThat(matchCandidateParams.get(0).getValue("person_uid")).isEqualTo(1l);
    assertThat(matchCandidateParams.get(0).getValue("person_name")).isEqualTo("patient name");
    assertThat(matchCandidateParams.get(0).getValue("person_add_time")).isEqualTo(now);
    assertThat(matchCandidateParams.get(0).getValue("mpi_person_id")).isEqualTo("abcd");
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
        linkResponse);
    assertThrows(MatchException.class, () -> matchService.relateNbsIdToMpiId(request));
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
        linkResponse);
    assertThrows(MatchException.class, () -> matchService.relateNbsIdToMpiId(request));
  }

}
