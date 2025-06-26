package gov.cdc.nbs.deduplication.config.auth.oidc;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfigurer;

@Configuration
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "true")
class OidcAuthenticationConfiguration {

  @ConfigurationProperties(prefix = "nbs.security")
  record SecurityProperties(
      String tokenSecret,
      String tokenIssuer,
      long tokenExpirationMillis) {
    public int getTokenExpirationSeconds() {
      return Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(tokenExpirationMillis));
    }
  }

  @Bean
  AuthenticationConfigurer oidcAuthenticationConfigurer(
      final OidcAuthenticationConverter converter) {
    return new OidcAuthenticationConfigurer(converter);
  }

  @Bean
  Algorithm jwtAlgorithm(final SecurityProperties properties) {
    // Per OWASP, key should be at least 64 characters
    // https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html
    if (properties.tokenSecret() == null || properties.tokenSecret().length() < 64) {
      throw new IllegalArgumentException(
          "Invalid value specified for 'nbs.security.tokenSecret', Ensure the length of the secret is at least 64 characters");
    }
    return Algorithm.HMAC256(properties.tokenSecret());
  }

  @Bean
  JWTVerifier jwtVerifier(
      final Algorithm algorithm,
      final SecurityProperties properties) {
    return JWT.require(algorithm)
        .withIssuer(properties.tokenIssuer())
        .build();
  }

}
