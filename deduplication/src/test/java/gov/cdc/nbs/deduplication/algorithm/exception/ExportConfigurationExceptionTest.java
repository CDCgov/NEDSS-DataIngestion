package gov.cdc.nbs.deduplication.algorithm.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExportConfigurationExceptionTest {

    @Test
    void testConstructorWithMessageAndCause() {
        // Given a message and a cause
        String message = "Test error message";
        Throwable cause = new Exception("Test cause");

        ExportConfigurationException exception = new ExportConfigurationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Test error message";

        ExportConfigurationException exception = new ExportConfigurationException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
