package gov.cdc.dataingestion.hl7.helper.integration.interfaces;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiFhirException;
import gov.cdc.dataingestion.hl7.helper.model.FhirConvertedMessage;

public interface IFhirConverter {
    public FhirConvertedMessage hL7ToFHIRConversion(String validHL7Message) throws DiFhirException;
}
