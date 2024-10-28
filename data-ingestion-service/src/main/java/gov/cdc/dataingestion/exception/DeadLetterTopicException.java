package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class DeadLetterTopicException extends Exception{
    public DeadLetterTopicException(String message) {
        super(message);
    }
}
