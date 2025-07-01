package gov.cdc.nbs.deduplication.config.auth.nbs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.cdc.nbs.deduplication.config.auth.nbs.NbsTokenConfiguration.SecurityProperties;
import gov.cdc.nbs.deduplication.config.auth.IgnoredPaths;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsToken;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenCreator;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenValidator;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenValidator.TokenStatus;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenValidator.TokenValidation;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class NbsAuthenticationFilterTest {

  @Mock
  private NbsTokenValidator tokenValidator;

  @Mock
  private NbsUserDetailsService userService;

  @Mock
  private NbsTokenCreator tokenCreator;

  @Mock
  private SecurityProperties securityProperties;

  @Mock
  private NbsSessionAuthenticator sessionAuthenticator;

  @Mock
  private IgnoredPaths ignoredPaths;

  @InjectMocks
  private NbsAuthenticationFilter filter;

  @Test
  void should_authenticate_with_token() throws ServletException, IOException {
    // Mock
    FilterChain chain = Mockito.mock(FilterChain.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    when(tokenCreator.forUser("user")).thenReturn(new NbsToken("tokenValue"));
    when(securityProperties.getTokenExpirationSeconds()).thenReturn(100);

    when(tokenValidator.validate(request)).thenReturn(new TokenValidation(TokenStatus.VALID, "user"));

    // Act
    filter.doFilterInternal(request, response, chain);

    // Assert
    verify(userService).authenticateByUsername("user");
  }

  @Test
  void should_authorize_session_expired_token() throws ServletException, IOException {
    // Mock
    FilterChain chain = Mockito.mock(FilterChain.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    when(tokenValidator.validate(request)).thenReturn(new TokenValidation(TokenStatus.EXPIRED, "user"));
    when(tokenCreator.forUser("sessionUser")).thenReturn(new NbsToken("tokenValue"));
    when(securityProperties.getTokenExpirationSeconds()).thenReturn(100);

    when(sessionAuthenticator.authenticate(request)).thenReturn(Optional.of("sessionUser"));

    // Act
    filter.doFilterInternal(request, response, chain);

    // Assert
    verify(userService).authenticateByUsername("sessionUser");
  }

  @Test
  void should_authorize_session_unset_token() throws ServletException, IOException {
    // Mock
    FilterChain chain = Mockito.mock(FilterChain.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    when(tokenValidator.validate(request)).thenReturn(new TokenValidation(TokenStatus.UNSET, "user"));
    when(tokenCreator.forUser("sessionUser")).thenReturn(new NbsToken("tokenValue"));
    when(securityProperties.getTokenExpirationSeconds()).thenReturn(100);

    when(sessionAuthenticator.authenticate(request)).thenReturn(Optional.of("sessionUser"));

    // Act
    filter.doFilterInternal(request, response, chain);

    // Assert
    verify(userService).authenticateByUsername("sessionUser");
  }

  @Test
  void should_not_authorize_session_unset_token() throws ServletException, IOException {
    // Mock
    FilterChain chain = Mockito.mock(FilterChain.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    when(tokenValidator.validate(request)).thenReturn(new TokenValidation(TokenStatus.UNSET, "user"));
    when(sessionAuthenticator.authenticate(request)).thenReturn(Optional.empty());

    // Act
    filter.doFilterInternal(request, response, chain);

    // Assert
    verifyNoInteractions(tokenCreator);
    verifyNoInteractions(securityProperties);
  }

  @Test
  void should_not_authorize_session_invalid_token() throws ServletException, IOException {
    // Mock
    FilterChain chain = Mockito.mock(FilterChain.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    when(tokenValidator.validate(request)).thenReturn(new TokenValidation(TokenStatus.INVALID, "user"));

    // Act
    filter.doFilterInternal(request, response, chain);

    // Assert
    verifyNoInteractions(tokenCreator);
    verifyNoInteractions(securityProperties);
    verifyNoInteractions(sessionAuthenticator);
  }

  @Test
  void should_ignore() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    when(ignoredPaths.ignored(request)).thenReturn(false);
    assertThat(filter.shouldNotFilter(request)).isFalse();
  }

}
