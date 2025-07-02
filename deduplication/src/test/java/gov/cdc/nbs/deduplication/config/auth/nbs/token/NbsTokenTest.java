package gov.cdc.nbs.deduplication.config.auth.nbs.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import jakarta.servlet.http.Cookie;

class NbsTokenTest {

  @Test
  void should_resolve() {
    Cookie[] cookies = new Cookie[] { new Cookie("some_Cookie", "some value"), new Cookie("nbs_token", "tokenValue") };

    Optional<NbsToken> token = NbsToken.resolve(cookies);
    assertThat(token).isPresent();
    assertThat(token.get().value()).isEqualTo("tokenValue");
  }

  @Test
  void should_not_resolve_missing() {
    Cookie[] cookies = new Cookie[] { new Cookie("some_Cookie", "some value") };

    Optional<NbsToken> token = NbsToken.resolve(cookies);
    assertThat(token).isEmpty();
  }

  @Test
  void should_not_resolve_empty() {
    Cookie[] cookies = new Cookie[] {};

    Optional<NbsToken> token = NbsToken.resolve(cookies);
    assertThat(token).isEmpty();
  }

  @Test
  void should_not_resolve_null() {
    Cookie[] cookies = null;

    Optional<NbsToken> token = NbsToken.resolve(cookies);
    assertThat(token).isEmpty();
  }

}
