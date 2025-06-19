package gov.cdc.dataingestion.share;
import com.google.gson.Gson;
import gov.cdc.dataingestion.share.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class CustomAuthenticationEntryPointTest {
    private CustomAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        entryPoint = new CustomAuthenticationEntryPoint();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        authException = new CustomAuthenticationException("Authentication failed");
    }

    @Test
    void commence_RespondsWithUnauthorizedError() throws IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails(authException.getMessage());
        String expectedJson = gson.toJson(expectedErrorResponse);

        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_Full_Auth_Req() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException("Full authentication is required to access this resource");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        Mockito.when(request.getHeader("clientid")).thenReturn("testclientId");
        Mockito.when(request.getHeader("clientsecret")).thenReturn("testclientsecret");
        Mockito.when(request.getHeader("authorization")).thenReturn("Bearer token123");
        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails("Invalid client or Invalid client credentials");
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_authorization_with_non_bearertoken() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException("Full authentication is required to access this resource");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        Mockito.when(request.getHeader("clientid")).thenReturn("testclientId");
        Mockito.when(request.getHeader("clientsecret")).thenReturn("testclientsecret");
        //If authorization is not Bearer token.
        Mockito.when(request.getHeader("authorization")).thenReturn("test");
        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails("Full authentication is required to access this resource");
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_authorization_null() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException("Full authentication is required to access this resource");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        Mockito.when(request.getHeader("clientid")).thenReturn("testclientId");
        Mockito.when(request.getHeader("clientsecret")).thenReturn("testclientsecret");
        //If authorization in header is null

        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails("Full authentication is required to access this resource");
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_clientId_null() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException("Full authentication is required to access this resource");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        Mockito.when(request.getHeader("clientsecret")).thenReturn("testclientsecret");
        Mockito.when(request.getHeader("authorization")).thenReturn("Bearer token123");
        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails("Full authentication is required to access this resource");
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_clientSecret_null() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException("Full authentication is required to access this resource");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        Mockito.when(request.getHeader("clientid")).thenReturn("testclientId");
        Mockito.when(request.getHeader("authorization")).thenReturn("Bearer token123");
        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails("Full authentication is required to access this resource");
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_Non_Full_Auth_Req() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException("test error msg");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails("test error msg");
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    @Test
    void commence_For_NullErrorMsg() throws IOException {
        // Arrange
        authException = new CustomAuthenticationException(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        Mockito.verify(response).setContentType("application/json");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Gson gson = new Gson();
        ErrorResponse expectedErrorResponse = new ErrorResponse();
        expectedErrorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        expectedErrorResponse.setMessage("Unauthorized");
        expectedErrorResponse.setDetails(null);
        String expectedJson = gson.toJson(expectedErrorResponse);
        System.out.println("stringWriter.toString():"+stringWriter.toString());
        Assertions.assertEquals(expectedJson, stringWriter.toString());
    }
    private static class CustomAuthenticationException extends AuthenticationException {
        public CustomAuthenticationException(String message) {
            super(message);
        }
    }
}
