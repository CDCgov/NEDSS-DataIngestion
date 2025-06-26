package gov.cdc.nbs.deduplication.config.auth.oidc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;

@Component
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "true")
class OidcAuthenticationConverter implements Converter<Jwt, PreAuthenticatedAuthenticationToken> {

  private final NbsUserDetailsService userDetailsService;

  OidcAuthenticationConverter(final NbsUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public PreAuthenticatedAuthenticationToken convert(final Jwt source) {
    String username = source.getClaimAsString("preferred_username");
    NbsUserDetails userDetails = userDetailsService.loadUserByUsername(username);

    return new PreAuthenticatedAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities());
  }
}