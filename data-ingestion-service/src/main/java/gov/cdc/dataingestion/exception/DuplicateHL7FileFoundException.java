package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class DuplicateHL7FileFoundException extends Exception {

    public DuplicateHL7FileFoundException(String message) {
        super(message);
    }
}
