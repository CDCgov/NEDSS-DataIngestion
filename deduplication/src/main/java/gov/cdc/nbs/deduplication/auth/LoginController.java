package gov.cdc.nbs.deduplication.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.auth.model.LoginRequest;
import gov.cdc.nbs.deduplication.auth.model.LoginResponse;
import gov.cdc.nbs.deduplication.config.auth.nbs.NbsTokenConfiguration.SecurityProperties;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsToken;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenCreator;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
@Profile("dev")
@ConditionalOnProperty(value = "nbs.security.oidc.enabled", havingValue = "false", matchIfMissing = true)
public class LoginController {

  private final SecurityProperties securityProperties;
  private final NbsUserDetailsService userService;
  private final NbsTokenCreator tokenCreator;

  public LoginController(
      final SecurityProperties securityProperties,
      final NbsUserDetailsService userService,
      final NbsTokenCreator tokenCreator) {
    this.securityProperties = securityProperties;
    this.userService = userService;
    this.tokenCreator = tokenCreator;
  }

  @PostMapping
  LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {

    NbsUserDetails userDetails = userService.loadUserByUsername(request.username());

    NbsToken token = this.tokenCreator.forUser(request.username());

    token.apply(
        securityProperties.getTokenExpirationSeconds(),
        response);

    return new LoginResponse(
        userDetails.getUsername(),
        token.value());
  }

}
