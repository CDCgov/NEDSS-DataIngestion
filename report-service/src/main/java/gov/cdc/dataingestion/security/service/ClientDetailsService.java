package gov.cdc.dataingestion.security.service;

import gov.cdc.dataingestion.security.model.SecurityUser;
import gov.cdc.dataingestion.security.repository.IClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientDetailsService implements UserDetailsService {

    private final IClientRepository iClientRepository;

    public ClientDetailsService(IClientRepository iClientRepository) {
        this.iClientRepository = iClientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return iClientRepository
                .findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Client Id not found: " + username));
    }
}
