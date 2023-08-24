package gov.cdc.dataingestion.authintegration.controller;

import gov.cdc.dataingestion.authintegration.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/nbsauth/token")
    public String getAuthToken() {
        logger.info("Inside getAuthToken() controller...");
        return authService.getToken();
    }
}
