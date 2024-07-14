package gov.cdc.dataprocessing.exception;

import lombok.Getter;

@Getter
public class DataProcessingConsumerException extends Exception {
    public DataProcessingConsumerException(String message) {
        super(message);
    }
}