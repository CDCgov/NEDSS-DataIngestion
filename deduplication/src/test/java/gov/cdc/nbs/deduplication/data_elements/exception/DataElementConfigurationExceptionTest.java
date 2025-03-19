package gov.cdc.nbs.deduplication.data_elements.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataElementConfigurationExceptionTest {

    @Test
    void testExceptionMessageAndCause() {
        // Given
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Root cause");

        // When
        DataElementConfigurationException exception = new DataElementConfigurationException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionWithoutCause() {
        // Given
        String message = "Test exception message";

        // When
        DataElementConfigurationException exception = new DataElementConfigurationException(message, null);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithNullMessageAndCause() {
        // Given
        String message = null;
        Throwable cause = new RuntimeException("Root cause");

        // When
        DataElementConfigurationException exception = new DataElementConfigurationException(message, cause);

        // Then
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionWithNullMessageAndNullCause() {
        // Given
        String message = null;
        Throwable cause = null;

        // When
        DataElementConfigurationException exception = new DataElementConfigurationException(message, cause);

        // Then
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
}
