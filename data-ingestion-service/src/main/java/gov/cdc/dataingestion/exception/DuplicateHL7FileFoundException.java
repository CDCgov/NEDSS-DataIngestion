package gov.cdc.dataingestion.exception;

public class DuplicateHL7FileFoundException extends Exception {

    public DuplicateHL7FileFoundException(String message) {
        super(message);
    }
}
