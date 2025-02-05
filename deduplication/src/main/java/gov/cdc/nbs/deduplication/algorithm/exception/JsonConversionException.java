package gov.cdc.nbs.deduplication.algorithm.exception;

public class JsonConversionException extends RuntimeException {

    // Constructor with message and cause
    public JsonConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with only message
    public JsonConversionException(String message) {
        super(message);
    }
}
