package gov.cdc.dataingestion.security.controller;

import gov.cdc.dataingestion.security.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenControllerTest {

    TokenService tokenService;
    Authentication authentication;
    String expectedToken = "testToken";

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        authentication = new UsernamePasswordAuthenticationToken("username", "password");
    }

    @Test
    void testTokenEndpoint() {
        when(tokenService.generateToken(authentication)).thenReturn("testToken");

        TokenController tokenController = new TokenController(tokenService);

        String generatedToken = tokenController.token(authentication);

        verify(tokenService, times(1)).generateToken(authentication);
        assertEquals(expectedToken, generatedToken);
    }
}