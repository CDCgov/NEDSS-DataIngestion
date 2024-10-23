package gov.cdc.dataprocessing.exception;

import lombok.Getter;

@Getter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
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
