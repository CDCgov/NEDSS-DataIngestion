package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class DiAsyncException extends RuntimeException  {
    public DiAsyncException(String message) {
        super(message);
    }
}
