package gov.cdc.dataingestion.security.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class TokenControllerTest {
    CustomMetricsBuilder customMetricsBuilder;
    RestTemplate restTemplate;
    String expectedToken = "testToken";
    RestTemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        customMetricsBuilder = mock(CustomMetricsBuilder.class);
        restTemplate = mock(RestTemplate.class);
        templateBuilder=mock(RestTemplateBuilder.class);
    }
    @Test
    void testTokenEndpoint() {
        String authTokenUri = "http://localhost:8080/realms/test/openid-connect/token";
        TokenController tokenController = new TokenController(restTemplate,customMetricsBuilder);
        tokenController.authTokenUri = authTokenUri;
        String clientId="test-keycloak-client";
        String clientSecret="testclientsecret";

        String expectedTokenString = "{\"access_token\":\"testToken\"}";

        ResponseEntity responseEntity = new ResponseEntity(expectedTokenString, HttpStatus.OK);
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<List<String>>>any()))
                .thenReturn(responseEntity);

        String generatedToken = tokenController.token(clientId,clientSecret).getBody().toString();

        assertEquals(expectedToken, generatedToken);
        verify(customMetricsBuilder, times(1)).incrementTokensRequested();
    }
    @Test
    void test_tokenendpoint_for_invalid_client_credentials() {
        String authTokenUri = "http://localhost:8080/realms/test/openid-connect/token";
        TokenController tokenController = new TokenController(restTemplate,customMetricsBuilder);
        tokenController.authTokenUri = authTokenUri;
        String clientId=null;
        String clientSecret=null;
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<List<String>>>any()))
                .thenThrow(HttpClientErrorException.class);

        String expectedResult="401 UNAUTHORIZED";
        String statusCode = tokenController.token(clientId,clientSecret).getStatusCode().toString();
        assertEquals(expectedResult, statusCode);
    }

    @Test
    void test_resttemplate_not_null(){
        RestTemplate restTemplateExpected=new RestTemplate();
        when(templateBuilder.build()).thenReturn(restTemplateExpected);
        RestTemplate restTemplateActual= TokenController.restTemplate(templateBuilder);
        assertNotNull(restTemplateActual);
    }
}