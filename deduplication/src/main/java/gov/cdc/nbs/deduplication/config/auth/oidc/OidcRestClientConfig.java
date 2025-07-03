package gov.cdc.nbs.deduplication.config.auth.oidc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("dev")
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "true")
class OidcRestClientConfig {

  @Bean("oidcRestClient")
  public RestClient oidcRestClient(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") final String uri) {
    return RestClient.builder().baseUrl(uri + "/protocol/openid-connect/token").build();
  }
}
