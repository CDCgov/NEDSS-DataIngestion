package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class DeadLetterTopicException extends Exception{
    public DeadLetterTopicException(String message) {
        super(message);
    }
}
