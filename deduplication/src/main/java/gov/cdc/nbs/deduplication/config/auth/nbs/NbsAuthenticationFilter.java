package gov.cdc.nbs.deduplication.config.auth.nbs;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfiguration.SecurityProperties;
import gov.cdc.nbs.deduplication.config.auth.IgnoredPaths;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsToken;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenCreator;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenValidator;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenValidator.TokenValidation;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A {@code OncePerRequestFilter} that ensures that incoming requests have a
 * valid 'Authentication' header, nbs_token,
 * or JSESSIONID. An unauthorized user will be redirected to `/nbs/timeout`/
 */
public class NbsAuthenticationFilter extends OncePerRequestFilter {

  private final NbsTokenValidator tokenValidator;
  private final NbsUserDetailsService userService;
  private final NbsTokenCreator tokenCreator;
  private final SecurityProperties securityProperties;
  private final NbsSessionAuthenticator sessionAuthenticator;
  private final IgnoredPaths ignoredPaths;

  public NbsAuthenticationFilter(
      final NbsTokenValidator tokenValidator,
      final NbsUserDetailsService userService,
      final NbsTokenCreator tokenCreator,
      final SecurityProperties securityProperties,
      final NbsSessionAuthenticator sessionAuthenticator,
      final IgnoredPaths ignoredPaths) {
    this.tokenValidator = tokenValidator;
    this.userService = userService;
    this.tokenCreator = tokenCreator;
    this.securityProperties = securityProperties;
    this.sessionAuthenticator = sessionAuthenticator;
    this.ignoredPaths = ignoredPaths;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest incoming,
      final HttpServletResponse outgoing,
      final FilterChain chain)
      throws ServletException, IOException {
    // Check for an existing NBS token
    TokenValidation tokenValidation = tokenValidator.validate(incoming);

    switch (tokenValidation.status()) {
      case VALID:
        // Set the Spring auth context for the user
        issueAuthentication(tokenValidation.user(), outgoing);
        break;
      case EXPIRED, UNSET:
        // attempt authentication using the JSESSIONID
        attemptSessionAuthentication(incoming, outgoing);
        break;
      case INVALID:
        SecurityContextHolder.getContext().setAuthentication(null);
    }
    chain.doFilter(incoming, outgoing);
  }

  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    return ignoredPaths.ignored(request);
  }

  // Checks if the JSESSIONID is valid, if so, apply authentication
  void attemptSessionAuthentication(final HttpServletRequest incoming, final HttpServletResponse response) {
    Optional<String> user = sessionAuthenticator.authenticate(incoming);
    if (user.isPresent()) {
      issueAuthentication(user.get(), response);
    } else {
      SecurityContextHolder.getContext().setAuthentication(null);
    }
  }

  // Sets the security context authentication. Adds the NbsToken to the response.
  // Adds the NbsUserCookie to the response
  private void issueAuthentication(
      final String user,
      final HttpServletResponse response) {
    Authentication auth = this.userService.authenticateByUsername(user);

    SecurityContextHolder.getContext().setAuthentication(auth);

    addNbsToken(user, response);
    addNbsUserCookie(user, response);
  }

  private void addNbsToken(final String user, final HttpServletResponse response) {
    NbsToken token = tokenCreator.forUser(user);
    token.apply(securityProperties.getTokenExpirationSeconds(), response);
  }

  @SuppressWarnings({ "squid:S2092", "squid:S3330" })
  private void addNbsUserCookie(final String user, final HttpServletResponse response) {
    Cookie cookie = new Cookie("nbs_user", user);
    cookie.setPath("/");
    cookie.setMaxAge(securityProperties.getTokenExpirationSeconds());
    cookie.setSecure(true);
    cookie.setHttpOnly(false);

    response.addCookie(cookie);
  }

}