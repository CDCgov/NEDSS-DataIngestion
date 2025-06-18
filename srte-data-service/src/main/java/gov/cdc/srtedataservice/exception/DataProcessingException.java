package gov.cdc.srtedataservice.exception;


public class DataProcessingException extends Exception{

    public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String message, Exception result) {
        super(message, result);
    }
    public DataProcessingException(String message, Throwable result) {
        super(message, result);
    }
}
