package gov.cdc.nbs.deduplication.config.auth.oidc;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfigurer;

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