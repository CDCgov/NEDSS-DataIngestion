package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class ConversionPrepareException extends Exception{
    public ConversionPrepareException(String message) {
        super(message);
    }
}
