package gov.cdc.nbs.deduplication.config.auth.nbs.token;

import java.util.Optional;

import jakarta.servlet.http.Cookie;

public record NbsSessionToken(String jSessionId) {

  private static final String J_SESSION_COOKIE_NAME = "JSESSIONID";

  public static Optional<NbsSessionToken> resolve(final Cookie[] cookies) {
    if (cookies == null) {
      return Optional.empty();
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(J_SESSION_COOKIE_NAME)) {
        String identifier = cookie.getValue();

        if (identifier != null && identifier.contains(".")) {
          identifier = identifier.substring(0, identifier.indexOf("."));
        }

        return Optional.of(new NbsSessionToken(identifier));
      }
    }
    return Optional.empty();
  }

}