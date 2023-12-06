package gov.cdc.dataingestion.security.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TokenControllerTest {
    CustomMetricsBuilder customMetricsBuilder;
    RestTemplate restTemplate;
    String expectedToken = "testToken";
    @BeforeEach
    void setUp() {
        customMetricsBuilder = mock(CustomMetricsBuilder.class);
        restTemplate = mock(RestTemplate.class);
    }
    @Test
    void testTokenEndpoint() {
        String authTokenUri = "http://localhost:8080/realms/test/openid-connect/token";
        TokenController tokenController = new TokenController(restTemplate, customMetricsBuilder);
        tokenController.authTokenUri = authTokenUri;

        String expectedTokenString = "{\"access_token\":\"testToken\"}";

        ResponseEntity responseEntity = new ResponseEntity(expectedTokenString, HttpStatus.OK);
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<List<String>>>any()))
                .thenReturn(responseEntity);

        String generatedToken = tokenController.token("test-keycloak-client", "testclientsecret");

        assertEquals(expectedToken, generatedToken);
        verify(customMetricsBuilder, times(1)).incrementTokensRequested();
    }
}