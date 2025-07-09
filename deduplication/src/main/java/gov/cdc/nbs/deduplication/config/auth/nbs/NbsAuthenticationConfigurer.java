package gov.cdc.nbs.deduplication.config.auth.nbs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfiguration.AuthenticationConfigurer;
import gov.cdc.nbs.deduplication.config.auth.IgnoredPaths;
import gov.cdc.nbs.deduplication.config.auth.nbs.NbsTokenConfiguration.SecurityProperties;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenCreator;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenValidator;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;

@Component
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "false", matchIfMissing = true)
class NbsAuthenticationConfigurer implements AuthenticationConfigurer {

  private final NbsAuthenticationFilter filter;

  NbsAuthenticationConfigurer(
      final NbsTokenValidator tokenValidator,
      final NbsUserDetailsService userService,
      final NbsTokenCreator tokenCreator,
      final SecurityProperties securityProperties,
      final NbsSessionAuthenticator sessionAuthenticator,
      final IgnoredPaths ignoredPaths) {
    this.filter = new NbsAuthenticationFilter(
        tokenValidator,
        userService,
        tokenCreator,
        securityProperties,
        sessionAuthenticator,
        ignoredPaths);
  }

  public HttpSecurity configure(final HttpSecurity http) {
    return http
        .addFilterBefore(
            filter,
            UsernamePasswordAuthenticationFilter.class);
  }
}