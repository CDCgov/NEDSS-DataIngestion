package gov.cdc.dataingestion.authservice.integration.controller;

import gov.cdc.dataingestion.authservice.integration.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    AuthService authServiceMock = Mockito.mock(AuthService.class);

    String expectedToken = "sampleToken";

    @Test
    void testGetAuthToken() {
        when(authServiceMock.getToken()).thenReturn(expectedToken);

        AuthController authController = new AuthController(authServiceMock);

        String actualToken = authController.getAuthToken();
        Assertions.assertEquals(expectedToken, actualToken);
    }
}