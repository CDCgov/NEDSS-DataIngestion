package gov.cdc.dataprocessing.exception;

import lombok.Getter;

@Getter

public class DataProcessingConsumerException extends Exception{
    private Object result;
    public DataProcessingConsumerException(String message, Object result) {
        super(message);
        this.result = result;
    }

    public DataProcessingConsumerException(String message) {
        super(message);
    }
}
