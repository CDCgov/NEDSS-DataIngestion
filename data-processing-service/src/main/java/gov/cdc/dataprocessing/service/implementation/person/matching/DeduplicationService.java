package gov.cdc.dataprocessing.service.implementation.person.matching;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class DeduplicationService {

  private RestClient restClient;

  public DeduplicationService(RestClient restClient) {
    this.restClient = restClient;
  }

  public MatchResponse match(PersonMatchRequest request) {
    return restClient.post()
        .uri("/match")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(MatchResponse.class);
  }

  public void relate(RelateRequest relateRequest) {
    restClient.post()
        .uri("/relate")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(relateRequest)
        .retrieve()
        .body(Void.class);
  }
}
