package gov.cdc.nbs.deduplication.config.auth.oidc;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;

@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "true")
class AuthorizedUserResolver {

  private static final String QUERY = """
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

  public AuthorizedUserResolver(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  // Attempts to lookup a user Id from the provided session Id where the user is
  // currently logged in
  public Optional<String> resolve(String sessionId) {
    return client.sql(QUERY)
        .param("sessionId", sessionId)
        .query(String.class)
        .optional();
  }

}
