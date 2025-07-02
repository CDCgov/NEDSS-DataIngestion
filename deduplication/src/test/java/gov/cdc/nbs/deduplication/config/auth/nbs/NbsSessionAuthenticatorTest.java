package gov.cdc.nbs.deduplication.config.auth.nbs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class NbsSessionAuthenticatorTest {

  @Mock
  private JdbcClient client;

  @InjectMocks
  private NbsSessionAuthenticator authenticator;

  @Test
  void should_authenticate() {
    // Given a valid request and response
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Cookie sessionCookie = new Cookie("JSESSIONID", "1234");
    when(request.getCookies()).thenReturn(new Cookie[] { sessionCookie });

    // mock DB query
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    MappedQuerySpec<String> querySpec = Mockito.mock(MappedQuerySpec.class);
    when(client.sql(NbsSessionAuthenticator.SELECT_USER_BY_SESSION)).thenReturn(statementSpec);
    when(statementSpec.param("sessionId", "1234")).thenReturn(statementSpec);
    when(statementSpec.query(String.class)).thenReturn(querySpec);
    when(querySpec.optional()).thenReturn(Optional.of("user"));

    Optional<String> user = authenticator.authenticate(request);
    assertThat(user).isPresent().hasValue("user");
  }

  @Test
  void should_not_authenticate() {
    // Given a valid request and response
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Cookie sessionCookie = new Cookie("JSESSIONID", "1234");
    when(request.getCookies()).thenReturn(new Cookie[] { sessionCookie });

    // mock DB query
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    MappedQuerySpec<String> querySpec = Mockito.mock(MappedQuerySpec.class);
    when(client.sql(NbsSessionAuthenticator.SELECT_USER_BY_SESSION)).thenReturn(statementSpec);
    when(statementSpec.param("sessionId", "1234")).thenReturn(statementSpec);
    when(statementSpec.query(String.class)).thenReturn(querySpec);
    when(querySpec.optional()).thenReturn(Optional.empty());

    Optional<String> user = authenticator.authenticate(request);
    assertThat(user).isEmpty();
  }
}
