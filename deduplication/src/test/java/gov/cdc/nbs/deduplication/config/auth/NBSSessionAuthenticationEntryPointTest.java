package gov.cdc.nbs.deduplication.config.auth;

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class NBSSessionAuthenticationEntryPointTest {

  private NBSSessionAuthenticationEntryPoint entryPoint = new NBSSessionAuthenticationEntryPoint();

  @Test
  void should_return() throws IOException {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    entryPoint.commence(request, response, null);

    verify(response).sendError(401, "Access Denied");
  }
}
