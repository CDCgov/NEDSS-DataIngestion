package gov.cdc.nbs.deduplication.config.auth.oidc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfigurer;

@Component
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "true")
class OidcAuthenticationConfigurer implements AuthenticationConfigurer {

  private final OidcAuthenticationConverter converter;

  OidcAuthenticationConfigurer(final OidcAuthenticationConverter converter) {
    this.converter = converter;
  }

  @Override
  public HttpSecurity configure(final HttpSecurity http) throws Exception {
    return http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
  }
}