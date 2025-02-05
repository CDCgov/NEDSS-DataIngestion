package gov.cdc.nbs.deduplication.algorithm.exception;

public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

