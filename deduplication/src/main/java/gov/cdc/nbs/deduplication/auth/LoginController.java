package gov.cdc.nbs.deduplication.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.config.auth.AuthenticationConfiguration.SecurityProperties;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsToken;
import gov.cdc.nbs.deduplication.config.auth.nbs.token.NbsTokenCreator;
import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
@ConditionalOnProperty(value = "nbs.security.login.enabled", havingValue = "true")
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

    var userDetails = userService.loadUserByUsername(request.userName());

    NbsToken token = this.tokenCreator.forUser(request.userName());

    token.apply(
        securityProperties.getTokenExpirationSeconds(),
        response);

    return new LoginResponse(
        userDetails.getUsername(),
        token.value());
  }

}
