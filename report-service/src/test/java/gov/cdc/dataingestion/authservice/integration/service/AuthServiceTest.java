package gov.cdc.dataingestion.authservice.integration.service;

import gov.cdc.dataingestion.exception.DIAuthenticationException;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
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
    void setUp() throws DIAuthenticationException {
        MockitoAnnotations.openMocks(this);
        CloseableHttpClient httpsClientMock = null;
        authServiceMock = new AuthService(httpsClientMock);
        ReflectionTestUtils.setField(authServiceMock, "nbsUsername", nbsUsername);
        ReflectionTestUtils.setField(authServiceMock, "nbsPassword", nbsPassword);
        ReflectionTestUtils.setField(authServiceMock, "signOnUrl", signOnUrl);
        ReflectionTestUtils.setField(authServiceMock, "tokenUrl", tokenUrl);
        ReflectionTestUtils.setField(authServiceMock, "rolesUrl", rolesUrl);

        authServiceMock.generateAuthTokenDuringStartup();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGenerateAuthTokenDuringStartup_Success() throws IOException, DIAuthenticationException {
        when(httpClientMock.execute(any())).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));

        authServiceMock.generateAuthTokenDuringStartup();

        Assertions.assertNotNull(authServiceMock.getToken());
    }

    @Test
    void testRefreshAuthTokenScheduled() throws IOException, DIAuthenticationException {
        when(httpClientMock.execute(any())).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));

        authServiceMock.refreshAuthTokenScheduled();

        Assertions.assertNotNull(authServiceMock.getToken());
    }

    @Test
    void testGenerateAuthTokenDuringStartup_JSON() throws IOException, DIAuthenticationException {
        String jsonContent = "{\"token\":\"dummyToken\", \"refreshToken\":\"dummyRefreshToken\"}";

        when(httpClientMock.execute(any())).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(new StringEntity(jsonContent));

        authServiceMock.refreshAuthTokenScheduled();

        String token = authServiceMock.getToken();
        assertEquals("dummyToken", token);
    }
}