package gov.cdc.nbs.deduplication.config.auth.nbs.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import gov.cdc.nbs.deduplication.config.auth.nbs.NbsTokenConfiguration.SecurityProperties;

@Component
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "false", matchIfMissing = true)
public class NbsTokenCreator {
  private final Clock clock;
  private final Algorithm algorithm;
  private final SecurityProperties properties;

  public NbsTokenCreator(
      final Clock clock,
      final Algorithm algorithm,
      final SecurityProperties properties) {
    this.clock = clock;
    this.algorithm = algorithm;
    this.properties = properties;
  }

  public NbsToken forUser(final String username) {
    Instant now = Instant.now(clock);
    Instant expiry = now.plus(Duration.ofMillis(properties.tokenExpirationMillis()));
    String token = JWT
        .create()
        .withIssuer(properties.tokenIssuer())
        .withIssuedAt(now)
        .withExpiresAt(expiry)
        .withSubject(username)
        .sign(algorithm);
    return new NbsToken(token);
  }

}
