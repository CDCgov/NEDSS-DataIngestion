package gov.cdc.nbs.deduplication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RecordLinkageClientConfig {

  @Bean("recordLinkageRestClient")
  public RestClient recordLinkageRestClient(@Value("${recordLinkage.url}") final String url) {
    return RestClient.builder().baseUrl(url).build();
  }

}
