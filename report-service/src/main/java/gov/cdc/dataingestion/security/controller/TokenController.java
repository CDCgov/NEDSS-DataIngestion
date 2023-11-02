package gov.cdc.dataingestion.security.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TokenController {

    private final TokenService tokenService;
    private final CustomMetricsBuilder customMetricsBuilder;

    public TokenController(TokenService tokenService, CustomMetricsBuilder customMetricsBuilder) {

        this.tokenService = tokenService;
        this.customMetricsBuilder = customMetricsBuilder;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        log.debug("Token requested for user: '{}'", authentication.getName());

        String token = tokenService.generateToken(authentication);
        customMetricsBuilder.incrementTokensRequested();
        log.debug("Token granted: {}", token);
        return token;
    }
}
