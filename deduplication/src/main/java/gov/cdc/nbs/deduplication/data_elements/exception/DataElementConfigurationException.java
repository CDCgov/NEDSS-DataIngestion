package gov.cdc.nbs.deduplication.data_elements.exception;

public class DataElementConfigurationException extends RuntimeException {

    public DataElementConfigurationException(String message) {
        super(message);
    }

    public DataElementConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
