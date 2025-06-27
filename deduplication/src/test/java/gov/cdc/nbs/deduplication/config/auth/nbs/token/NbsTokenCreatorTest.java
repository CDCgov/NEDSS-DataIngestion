package gov.cdc.nbs.deduplication.config.auth.nbs.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfiguration.SecurityProperties;

class NbsTokenCreatorTest {

  @Test
  void should_create_token_when_provided_a_username() {

    Clock clock = Clock.fixed(Instant.parse("2020-03-03T10:15:30.00Z"), ZoneOffset.UTC);

    SecurityProperties properties = new SecurityProperties(
        "secret",
        "test-issuer",
        10000);

    Algorithm algorithm = mock(Algorithm.class);

    when(algorithm.sign(Mockito.any(), Mockito.any())).thenReturn("SomeBytes".getBytes());

    NbsTokenCreator creator = new NbsTokenCreator(
        clock,
        algorithm,
        properties);

    NbsToken actual = creator.forUser("user-value");

    DecodedJWT decoded = JWT.decode(actual.value());

    assertThat(decoded)
        .returns("test-issuer", DecodedJWT::getIssuer)
        .returns(Instant.parse("2020-03-03T10:15:30.00Z"), DecodedJWT::getIssuedAtAsInstant)
        .returns(Instant.parse("2020-03-03T10:15:40.00Z"), DecodedJWT::getExpiresAtAsInstant)
        .returns("user-value", DecodedJWT::getSubject);
  }
}
