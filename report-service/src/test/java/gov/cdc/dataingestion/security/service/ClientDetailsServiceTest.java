package gov.cdc.dataingestion.security.service;

import gov.cdc.dataingestion.security.model.Client;
import gov.cdc.dataingestion.security.repository.IClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientDetailsServiceTest {

    IClientRepository iClientRepositoryMock;
    Client client;
    ClientDetailsService clientDetailsService;

    @BeforeEach
    void setUp() {
        iClientRepositoryMock = mock(IClientRepository.class);
        client = new Client();
        clientDetailsService = new ClientDetailsService(iClientRepositoryMock);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(iClientRepositoryMock);
    }

    @Test
    void testLoadUserByUsernameExistingClient() {
        client.setUsername("existingUser");
        client.setPassword("password");
        when(iClientRepositoryMock.findByUsername("existingUser")).thenReturn(Optional.of(client));

        UserDetails userDetails = clientDetailsService.loadUserByUsername("existingUser");

        assertNotNull(userDetails);
        assertEquals("existingUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsernameNonExistingClient() {
        when(iClientRepositoryMock.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            clientDetailsService.loadUserByUsername("nonExistingUser");
        });
        verify(iClientRepositoryMock, times(1)).findByUsername("nonExistingUser");
    }
}