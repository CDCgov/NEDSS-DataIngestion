package gov.cdc.nbs.deduplication.config.auth.nbs.token;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public record NbsToken(String value) {

  public static final String NBS_TOKEN_NAME = "nbs_token";

  public void apply(
      final int expirationInSeconds,
      final HttpServletResponse response) {
    Cookie cookie = asCookie();
    cookie.setMaxAge(expirationInSeconds);
    response.addCookie(cookie);
  }

  @SuppressWarnings({ "squid:S3330" })
  private Cookie asCookie() {
    Cookie cookie = new Cookie(NBS_TOKEN_NAME, value());
    cookie.setPath("/");
    cookie.setSecure(true);
    return cookie;
  }

  public static Optional<NbsToken> resolve(Cookie[] cookies) {
    if (cookies == null) {
      return Optional.empty();
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(NBS_TOKEN_NAME)) {
        return Optional.of(new NbsToken(cookie.getValue()));
      }
    }
    return Optional.empty();
  }

}