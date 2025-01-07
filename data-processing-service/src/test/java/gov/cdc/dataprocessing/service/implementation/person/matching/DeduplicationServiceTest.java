package gov.cdc.dataprocessing.service.implementation.person.matching;

import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse.MatchType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeduplicationServiceTest {

  @Mock
  private RestClient restClient;

  @Mock
  private RequestBodyUriSpec uriSpec;

  @Mock
  private RequestBodySpec bodySpec;

  @Mock
  private ResponseSpec response;

  @InjectMocks
  private DeduplicationService service;

  @Test
  void shouldCallMatch() {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/match")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.any(PersonMatchRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);
    when(response.body(MatchResponse.class)).thenReturn(new MatchResponse(1L, MatchType.EXACT, null));

    MatchResponse matchResponse = service.match(new PersonMatchRequest(new PersonContainer()));
    assertThat(matchResponse.match()).isEqualTo(1L);
    assertThat(matchResponse.matchType()).isEqualTo(MatchType.EXACT);

  }

  @Test
  void shouldCallRelate() {
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/relate")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.any(RelateRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);

    service.relate(new RelateRequest(1L, 2L, MatchType.NONE, null));
    verify(restClient, times(1)).post();
  }
}
