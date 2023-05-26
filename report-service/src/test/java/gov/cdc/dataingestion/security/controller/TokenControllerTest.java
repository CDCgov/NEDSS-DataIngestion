package gov.cdc.dataingestion.security.controller;

import gov.cdc.dataingestion.security.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenControllerTest {

    private TokenController tokenController;

    private  TokenService tokenService;

    @BeforeEach
    public void setUp() {
        tokenService = mock(TokenService.class);
        tokenController = new TokenController(tokenService);
    }

    @Test
    public void TokenTest()
    {
        var authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("tokenTest");
        when(tokenService.generateToken(authentication)).thenReturn("testGeneratedToken");

        var token  =  tokenController.token(authentication);
        Assertions.assertEquals("testGeneratedToken", token);
    }

}
