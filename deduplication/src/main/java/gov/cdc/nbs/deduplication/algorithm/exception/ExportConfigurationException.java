package gov.cdc.nbs.deduplication.algorithm.exception;

public class ExportConfigurationException extends RuntimeException {

    // Constructor to accept a message and cause
    public ExportConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor to accept just a message
    public ExportConfigurationException(String message) {
        super(message);
    }
}
