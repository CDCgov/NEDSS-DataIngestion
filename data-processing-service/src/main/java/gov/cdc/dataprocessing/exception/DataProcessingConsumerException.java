package gov.cdc.dataprocessing.exception;

import lombok.Getter;

@Getter
public class DataProcessingConsumerException extends Exception{
    private final Object result;
    public DataProcessingConsumerException(String message, Object result) {
        super(message);
        this.result = result;
    }
}
