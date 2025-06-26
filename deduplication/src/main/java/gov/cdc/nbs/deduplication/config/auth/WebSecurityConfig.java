package gov.cdc.nbs.deduplication.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      final HttpSecurity http,
      // will be either OidcAuthenticationConfigurer or NbsAuthenticationConfigurer
      final AuthenticationConfigurer authenticationConfigurer)
      throws Exception {
    return authenticationConfigurer.configure(withStandardSecurity(http))
        .build();
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
