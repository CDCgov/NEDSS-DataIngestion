package gov.cdc.dataingestion.registration.controller;

import gov.cdc.dataingestion.registration.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RegisterController {
    private final RegistrationService registrationService;

    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    @PostMapping("/registration")
    public HttpStatus createUser(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        log.info("Inside registration controller...");
        if(username.isEmpty() && password.isEmpty()) {
            log.error("Username and/or password must not be null or empty.");
            return HttpStatus.BAD_REQUEST;
        }
        if(username.isEmpty() || password.isEmpty()) {
            log.error("Username and/or password must not be null or empty.");
            return HttpStatus.BAD_REQUEST;
        }
        if(registrationService.createUser(username, password)) {
            return HttpStatus.CREATED;
        }
        log.error("Username already exists");
        return HttpStatus.NOT_ACCEPTABLE;
    }
}
