package gov.cdc.dataingestion.security.service;

import gov.cdc.dataingestion.security.model.Client;
import gov.cdc.dataingestion.security.model.SecurityUser;
import gov.cdc.dataingestion.security.repository.IClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ClientDetailsService implements UserDetailsService {

    private final IClientRepository iClientRepository;

    public ClientDetailsService(IClientRepository iClientRepository) {
        this.iClientRepository = iClientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Client> client = iClientRepository.findByUsername(username);
//        log.info("client object is...", client.get().getPassword());
//        log.info("client username is...", username);
//        client.orElseThrow(() -> new UsernameNotFoundException("Client Id now found: " + username));
//        return client.map(SecurityUser::new).get();

        return iClientRepository
                .findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Client Id not found: " + username));
    }
}
