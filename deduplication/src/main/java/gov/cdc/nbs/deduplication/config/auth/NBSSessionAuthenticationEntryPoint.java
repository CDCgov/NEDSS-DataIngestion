package gov.cdc.nbs.deduplication.config.auth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class NBSSessionAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException authException)
      throws IOException {

    response.sendError(401, "Access Denied");
  }
}