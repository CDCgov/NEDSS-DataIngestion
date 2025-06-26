package gov.cdc.nbs.deduplication.config.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.stereotype.Component;

@Component
// applies the configured security to the service
public class SecurityConfigurer {

  // will be either OidcAuthenticationConfigurer or NbsAuthenticationConfigurer
  private final AuthenticationConfigurer authenticationConfigurer;

  public SecurityConfigurer(
      final AuthenticationConfigurer authenticationConfigurer) {
    this.authenticationConfigurer = authenticationConfigurer;
  }

  public HttpSecurity configure(final HttpSecurity http) throws Exception {
    return authenticationConfigurer.configure(withStandardSecurity(http));
  }

  @SuppressWarnings("java:S4502")
  private HttpSecurity withStandardSecurity(final HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                new NBSSessionAuthenticationEntryPoint(),
                AnyRequestMatcher.INSTANCE));
  }
}
