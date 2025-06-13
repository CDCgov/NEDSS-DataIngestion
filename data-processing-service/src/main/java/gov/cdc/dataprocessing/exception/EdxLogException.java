package gov.cdc.dataprocessing.exception;


public class EdxLogException extends Exception{
    private final Object result;
    public EdxLogException(String message, Object result) {
        super(message);
        this.result = result;
    }
}
