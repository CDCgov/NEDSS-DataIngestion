package gov.cdc.dataingestion.share;
import gov.cdc.dataingestion.share.model.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class ControllerExceptionHandlerTest {
    private ControllerExceptionHandler handler;
    private Exception exception;

    @BeforeEach
    public void setUp() {
        handler = new ControllerExceptionHandler();
        exception = new Exception("Something went wrong");
    }

    @Test
    void handleException_ReturnsErrorResponseWithInternalServerError() {
        // Act
        ResponseEntity<ErrorResponse> responseEntity = handler.handleException(exception);

        // Assert
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatusCode());
        Assertions.assertEquals("An internal server error occurred.", errorResponse.getMessage());
        Assertions.assertEquals(exception.getMessage(), errorResponse.getDetails());
    }
}
