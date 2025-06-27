package gov.cdc.nbs.deduplication.config.auth;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

@Configuration
@EnableConfigurationProperties({
    AuthenticationConfiguration.SecurityProperties.class,
    AuthenticationConfiguration.PathSettings.class })
public class AuthenticationConfiguration {

  public interface AuthenticationConfigurer {
    @SuppressWarnings("java:S112")
    HttpSecurity configure(final HttpSecurity http) throws Exception;
  }

  @ConfigurationProperties(prefix = "nbs.security")
  public record SecurityProperties(
      String tokenSecret,
      String tokenIssuer,
      long tokenExpirationMillis) {
    public int getTokenExpirationSeconds() {
      return Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(tokenExpirationMillis));
    }
  }

  @ConfigurationProperties(prefix = "nbs.security.paths")
  record PathSettings(List<String> ignored) {
  }

  @Bean
  IgnoredPaths configuredIgnoredPaths(final PathSettings settings) {
    return new IgnoredPaths(settings.ignored());
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
