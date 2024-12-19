package gov.cdc.dataprocessing.service.implementation.person.matching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DeduplicationClient {

  @Bean("deduplicationRestClient")
  public RestClient restClient(@Value("${features.modernizedMatching.url}") String modernizedMatchingUrl) {
    return RestClient.builder().baseUrl(modernizedMatchingUrl).build();
  }
}
