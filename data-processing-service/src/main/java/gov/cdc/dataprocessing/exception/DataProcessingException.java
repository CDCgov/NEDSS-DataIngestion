package gov.cdc.dataprocessing.exception;

public class DataProcessingException extends Exception {

    public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String message, Exception result) {
        super(message, result);
    }
}
