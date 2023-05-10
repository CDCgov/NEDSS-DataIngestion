package gov.cdc.dataingestion.hl7.helper.integration.exception;

public class DiFhirException  extends Exception{
    public DiFhirException(String errorMessage) {
        super(errorMessage);
    }
}
