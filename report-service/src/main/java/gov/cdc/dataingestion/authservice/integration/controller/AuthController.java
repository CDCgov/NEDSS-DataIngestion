package gov.cdc.dataingestion.authservice.integration.controller;

import gov.cdc.dataingestion.authservice.integration.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/token")
    public String getAuthToken() {
        return authService.getToken();
    }
}
