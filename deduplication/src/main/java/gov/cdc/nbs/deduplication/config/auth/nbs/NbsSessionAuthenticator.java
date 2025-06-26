package gov.cdc.nbs.deduplication.config.auth.nbs;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsSessionToken;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "false", matchIfMissing = true)
public class NbsSessionAuthenticator {

  private static final String SELECT_USER_BY_SESSION = """
      SELECT TOP 1
        au.user_id
      FROM
        Security_log sl
        JOIN Auth_user au ON au.nedss_entry_id = sl.nedss_entry_id
        AND au.record_status_cd = 'ACTIVE'
      WHERE
        sl.session_id = :sessionId
        AND sl.event_time = (
          SELECT
            MAX(event_time)
          FROM
            Security_log sl2
          WHERE
            sl2.session_id = sl.session_id
        )
        AND sl.event_type_cd = 'LOGIN_SUCCESS';
      """;

  private final JdbcClient client;

  public NbsSessionAuthenticator(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  // checks for a valid JSESSIONID cookie
  // returns the user associated if one exists
  public Optional<String> authenticate(HttpServletRequest incoming) {
    return NbsSessionToken.resolve(incoming.getCookies())
        .flatMap(session -> findUserBySession(session.jSessionId()));
  }

  // Attempts to lookup a user Id from the provided session Id where the user is
  // currently logged in
  private Optional<String> findUserBySession(String sessionId) {
    return client.sql(SELECT_USER_BY_SESSION)
        .param("sessionId", sessionId)
        .query(String.class)
        .optional();
  }
}
