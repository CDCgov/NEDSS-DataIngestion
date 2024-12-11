package gov.cdc.nbs.deduplication.config;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RecordLinkageClientConfig {

  @Bean("recordLinkageRestClient")
  public RestClient recordLinkageRestClient(
      @Value("${recordLinkage.url}") final String url) {

    HttpContext context = HttpClientContext.create();
    context.setProtocolVersion(new ProtocolVersion("HTTP", 1, 1));

    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
        HttpClientBuilder.create().build());
    factory.setHttpContextFactory((a, b) -> context);
    return RestClient.builder().requestFactory(factory).baseUrl(url).build();

  }

}
