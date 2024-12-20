package gov.cdc.dataprocessing.service.implementation.person.matching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(name = "features.modernizedMatching.enabled", havingValue = "true")
public class DeduplicationClient {

  @Bean
  public DeduplicationService deduplicationService(
      @Value("${features.modernizedMatching.url}") String modernizedMatchingUrl) {
    return new DeduplicationService(RestClient.builder().baseUrl(modernizedMatchingUrl).build());
  }

}
