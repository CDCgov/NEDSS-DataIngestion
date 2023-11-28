package gov.cdc.dataingestion.exception;

public class DeadLetterTopicException extends Exception{
    public DeadLetterTopicException(String message) {
        super(message);
    }
}
