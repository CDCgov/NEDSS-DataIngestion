package gov.cdc.nbs.deduplication.config.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface AuthenticationConfigurer {

  @SuppressWarnings("java:S112")
  HttpSecurity configure(final HttpSecurity http) throws Exception;

}