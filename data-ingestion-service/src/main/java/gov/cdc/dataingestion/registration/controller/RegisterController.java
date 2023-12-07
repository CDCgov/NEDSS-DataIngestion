package gov.cdc.dataingestion.registration.controller;

import gov.cdc.dataingestion.registration.dto.RegistrationRequestDTO;
import gov.cdc.dataingestion.registration.service.RegistrationService;
import gov.cdc.dataingestion.reportstatus.controller.ReportStatusController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {
    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final RegistrationService registrationService;
    private static final String USER_NAME_PWD_REQ_MSG ="Username and/or password are required.";
    private static final String USER_NAME_MIN_LENGTH ="The username must be atleast six characters in length.";
    private static final String PWD_MIN_LENGTH ="The password must be atleast eight characters in length.";
    private static final String USER_CREATED_MSG ="User Created Successfully.";
    private static final String USER_ALREADY_EXIST_MSG ="User already exists.Please choose another.";

    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/registration")
    public String createUser(@RequestBody RegistrationRequestDTO request) {

        String username = request.getUsername();
        String password = request.getPassword();

        if(username.isEmpty() || password.isEmpty()) {
            logger.error(USER_NAME_PWD_REQ_MSG);
            return USER_NAME_PWD_REQ_MSG;
        }
        if(username.trim().length()<6){
            logger.error(USER_NAME_MIN_LENGTH);
            return USER_NAME_MIN_LENGTH;
        }
        if(password.trim().length()<8){
            logger.error(PWD_MIN_LENGTH);
            return PWD_MIN_LENGTH;
        }
        if(registrationService.createUser(username, password)) {
            logger.debug(USER_CREATED_MSG);
            return USER_CREATED_MSG;
        }
        logger.error(USER_ALREADY_EXIST_MSG);
        return USER_ALREADY_EXIST_MSG;
    }
}