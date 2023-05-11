package gov.cdc.dataingestion.registration.service;


import gov.cdc.dataingestion.registration.model.RegisterClient;
import gov.cdc.dataingestion.registration.repository.IClientRegisterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RegistrationService {
    private final IClientRegisterRepository iClientRegisterRepository;

    private final PasswordEncoder passwordEncoder;

    RegisterClient clientObject = new RegisterClient();

    public RegistrationService(PasswordEncoder passwordEncoder, IClientRegisterRepository iClientRegisterRepository) {
        this.iClientRegisterRepository = iClientRegisterRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    public boolean createUser(String username, String password) {
        log.info("inside register service...");
        Optional<RegisterClient> client = iClientRegisterRepository.findByUsername(username);
        if(client.isEmpty()) {
            clientObject.setId(UUID.randomUUID().toString());
            clientObject.setUsername(username);
            clientObject.setPassword(passwordEncoder.encode(password));
            clientObject.setRoles("ADMIN,USER");
            clientObject.setCreatedBy("rshanmugam");
            clientObject.setUpdatedBy("rshanmugam");
            iClientRegisterRepository.save(clientObject);
            return true;
        }
        return false;
    }
}
