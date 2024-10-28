package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class FhirConversionException extends Exception {

    public FhirConversionException(String message) {
        super(message);
    }
}
