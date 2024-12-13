package gov.cdc.nbs.deduplication.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

import gov.cdc.nbs.deduplication.matching.model.CreatePersonResponse;
import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.matching.model.LinkResponse;
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
    assertThat(response.linkResponse().prediction()).isEqualTo("no_match");
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
        "match",
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
    assertThat(response.linkResponse().prediction()).isEqualTo("match");
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
        "possible_match",
        null));

    mockClientPatientUpdateCall("patientReferenceId",
        new CreatePersonResponse(
            "newPatientReference",
            "newPersonReference"));

    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.POSSIBLE);
    assertThat(response.match()).isNull();
    assertThat(response.linkResponse().patient_reference_id()).isEqualTo("newPatientReference");
    assertThat(response.linkResponse().person_reference_id()).isEqualTo("newPersonReference");
    assertThat(response.linkResponse().prediction()).isEqualTo("possible_match");
    assertThat(response.linkResponse().results()).isNull();
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
    when(uriSpec.uri(String.format("/patient/%s/person", patientId))).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
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
    ArgumentCaptor<SqlParameterSource> captor = ArgumentCaptor.forClass(SqlParameterSource.class);

    when(template.update(Mockito.anyString(), captor.capture())).thenReturn(1);
    matchService.relateNbsIdToMpiId(new RelateRequest(
        1l,
        1l,
        MatchType.POSSIBLE,
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
    assertThat(captor.getValue().getValue("status")).isEqualTo("R");
  }

}
