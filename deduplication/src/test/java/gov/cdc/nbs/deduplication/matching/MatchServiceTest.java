package gov.cdc.nbs.deduplication.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.matching.model.LinkResponse;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;

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

    mockClientMatchCall(new LinkResponse(
        "patientReferenceId",
        "personReferenceId",
        "no_match",
        null));

    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.NONE);
    assertThat(response.match()).isNull();
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

    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.POSSIBLE);
    assertThat(response.match()).isNull();
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
    MatchResponse response = matchService.match(matchRequest);

    assertThat(response.matchType()).isEqualTo(MatchType.NONE);
    assertThat(response.match()).isNull();
  }

  private void mockClientMatchCall(LinkResponse response) {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/match")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.any(LinkRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(LinkResponse.class)).thenReturn(response);
  }

}
