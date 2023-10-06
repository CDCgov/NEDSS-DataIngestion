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
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createUser(String username, String password) {
        Optional<RegisterClient> client = iClientRegisterRepository.findByUsername(username);
        if(client.isEmpty()) {
            clientObject.setId(UUID.randomUUID().toString());
            clientObject.setUsername(username);
            clientObject.setPassword(passwordEncoder.encode(password));
            if(username.contains("admin")) {
                clientObject.setRoles("ADMIN");
            }
            else {
                clientObject.setRoles("USER");
            }
            clientObject.setCreatedBy("diteamadmin");
            clientObject.setUpdatedBy("diteamadmin");
            iClientRegisterRepository.save(clientObject);
            return true;
        }
        return false;
    }
}
