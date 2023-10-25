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
    private static final String userNamePwdReqMsg="Username and/or password are required.";
    private static final String userNameMinLength="The username must be atleast six characters in length.";
    private static final String pwdMinLength="The password must be atleast eight characters in length.";
    private static final String userCreatedMsg="User Created Successfully.";
    private static final String userAlreadyExistMsg="User already exists.Please choose another.";
    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/registration")
    public String createUser(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        log.info("Inside registration controller...");
        if(username.isEmpty() || password.isEmpty()) {
            log.error(userNamePwdReqMsg);
            return userNamePwdReqMsg;
        }
        if(username.trim().length()<6){
            log.error(userNameMinLength);
            return userNameMinLength;
        }
        if(password.trim().length()<8){
            log.error(pwdMinLength);
            return pwdMinLength;
        }
        if(registrationService.createUser(username, password)==true) {
            log.debug(userCreatedMsg);
            return userCreatedMsg;
        }
        log.error(userAlreadyExistMsg);
        return userAlreadyExistMsg;
    }
}