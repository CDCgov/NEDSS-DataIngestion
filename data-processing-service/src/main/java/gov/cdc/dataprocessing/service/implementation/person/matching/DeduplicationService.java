package gov.cdc.dataprocessing.service.implementation.person.matching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DeduplicationService {

  private RestClient restClient;

  public DeduplicationService(
      @Value("${features.modernizedMatching.url}") String modernizedMatchingUrl) {
    this.restClient = RestClient.builder().baseUrl(modernizedMatchingUrl).build();
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
