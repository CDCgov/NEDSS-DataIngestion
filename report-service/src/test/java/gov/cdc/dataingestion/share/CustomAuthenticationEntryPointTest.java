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

class CustomAuthenticationEntryPointTest {
    private CustomAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    public void setUp() {
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

    private static class CustomAuthenticationException extends AuthenticationException {
        public CustomAuthenticationException(String message) {
            super(message);
        }
    }
}
