package gov.cdc.dataprocessing.exception;

import lombok.Getter;

@Getter
public class DataProcessingDBException  extends Exception{
    private Object result;
    public DataProcessingDBException(String message, Object result) {
        super(message);
        this.result = result;
    }

    public DataProcessingDBException(String message) {
        super(message);
    }
}
