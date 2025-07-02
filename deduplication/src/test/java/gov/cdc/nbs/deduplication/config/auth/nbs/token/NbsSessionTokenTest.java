package gov.cdc.nbs.deduplication.config.auth.nbs.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;

class NbsSessionTokenTest {

  @Test
  void null_token() {
    Optional<NbsSessionToken> token = NbsSessionToken.resolve(null);

    assertThat(token).isEmpty();
  }

  @Test
  void no_match_token() {
    Cookie[] cookies = new Cookie[] { new Cookie("not_session", null) };
    Optional<NbsSessionToken> token = NbsSessionToken.resolve(cookies);

    assertThat(token).isEmpty();
  }

  @Test
  void valid_token() {
    Cookie[] cookies = new Cookie[] { new Cookie("JSESSIONID", "mySessionToken") };

    Optional<NbsSessionToken> token = NbsSessionToken.resolve(cookies);
    assertThat(token).isPresent();
    assertThat(token.get().jSessionId()).isEqualTo("mySessionToken");
  }

  @Test
  void valid_dot_token() {
    Cookie[] cookies = new Cookie[] {
        new Cookie("JSESSIONID", "_5BLZmfqXxtJm7H9TCdYJv9WnPa7R_fYED67-Rc_.98237c833076") };

    Optional<NbsSessionToken> token = NbsSessionToken.resolve(cookies);
    assertThat(token).isPresent();
    assertThat(token.get().jSessionId()).isEqualTo("_5BLZmfqXxtJm7H9TCdYJv9WnPa7R_fYED67-Rc_");
  }

  @Test
  void invalid_null_token() {
    Cookie[] cookies = new Cookie[] {
        new Cookie("JSESSIONID", null) };

    Optional<NbsSessionToken> token = NbsSessionToken.resolve(cookies);
    assertThat(token).isEmpty();
  }
}
