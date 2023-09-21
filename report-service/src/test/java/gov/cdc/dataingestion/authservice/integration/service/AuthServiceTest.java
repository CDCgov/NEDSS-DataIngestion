package gov.cdc.dataingestion.authservice.integration.service;

import gov.cdc.dataingestion.exception.DIAuthenticationException;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authServiceMock;

    @Mock
    private CloseableHttpClient httpClientMock;

    @Mock
    private CloseableHttpResponse httpResponseMock;

    @Value("${auth.username}")
    private String nbsUsername = "testUser";

    @Value("${auth.password}")
    private String nbsPassword = "testPassword";

    @Value("${auth.sign-on-url}")
    private String signOnUrl = "https://nbsauthenticator.datateam-cdc-nbs.eqsandbox.com/nbsauth/signon";

    @Value("${auth.token-url}")
    private String tokenUrl = "https://nbsauthenticator.datateam-cdc-nbs.eqsandbox.com/nbsauth/token";

    @Value("${auth.roles-url}")
    private String rolesUrl = "https://nbsauthenticator.datateam-cdc-nbs.eqsandbox.com/nbsauth/roles";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authServiceMock = new AuthService();
        authServiceMock.httpsClient = httpClientMock;
        ReflectionTestUtils.setField(authServiceMock, "nbsUsername", nbsUsername);
        ReflectionTestUtils.setField(authServiceMock, "nbsPassword", nbsPassword);
        ReflectionTestUtils.setField(authServiceMock, "signOnUrl", signOnUrl);
        ReflectionTestUtils.setField(authServiceMock, "tokenUrl", tokenUrl);
        ReflectionTestUtils.setField(authServiceMock, "rolesUrl", rolesUrl);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGenerateAuthTokenDuringStartup() throws IOException, DIAuthenticationException {
        when(httpClientMock.execute(any())).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));

        authServiceMock.generateAuthTokenDuringStartup();

        assertNotNull(authServiceMock.getToken());
        assertNotNull(authServiceMock.token);
        assertNotNull(authServiceMock.refreshToken);
    }

    @Test
    void testRefreshAuthTokenScheduled() throws IOException, DIAuthenticationException {
        when(httpClientMock.execute(any())).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));

        authServiceMock.generateAuthTokenDuringStartup();
        authServiceMock.refreshAuthTokenScheduled();

        assertNotNull(authServiceMock.getToken());
        assertNotNull(authServiceMock.token);
        assertNotNull(authServiceMock.refreshToken);
    }

    @Test
    void testGetTokenFromApiResponse() throws IOException, DIAuthenticationException {
        String jsonTokenContent = "{\"token\":\"dummyToken\", \"refreshToken\":\"dummyRefreshToken\"}";

        when(httpResponseMock.getEntity()).thenReturn(new StringEntity(jsonTokenContent));

        authServiceMock.getTokenFromApiResponse(httpResponseMock);

        assertEquals("dummyToken", authServiceMock.token);
        assertEquals("dummyRefreshToken", authServiceMock.refreshToken);
    }

    @Test
    void testGetAuthRolesFromApiResponse() throws IOException, DIAuthenticationException {
        String jsonRolesContent = "{\"roles\":\"test-roles, test_access, test-auth-role, \"}";

        when(httpResponseMock.getEntity()).thenReturn(new StringEntity(jsonRolesContent));

        authServiceMock.getAuthRolesFromApiResponse(httpResponseMock);

        assertEquals(false, authServiceMock.isUserAllowedToLoadElrData);
        assertEquals(false, authServiceMock.isUserAllowedToLoadEcrData);
    }

    @Test
    void testGetAuthRolesFromApiResponseNull() throws IOException, DIAuthenticationException {
        authServiceMock.getAuthRolesFromApiResponse(null);

        assertEquals(false, authServiceMock.isUserAllowedToLoadElrData);
        assertEquals(false, authServiceMock.isUserAllowedToLoadEcrData);
    }

    @Test
    void testGetSignOnUrl() {
        String actualSignOnUrl = authServiceMock.getSignOnUrl();
        String expectedSignOnUrl = "https://nbsauthenticator.datateam-cdc-nbs.eqsandbox.com/nbsauth/signon?user=dGVzdFVzZXI=&password=dGVzdFBhc3N3b3Jk";

        assertEquals(expectedSignOnUrl, actualSignOnUrl);
    }
}
