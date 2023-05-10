package gov.cdc.dataingestion.hl7.helper.integration;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiFhirException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IFhirConverter;
import gov.cdc.dataingestion.hl7.helper.model.FhirConvertedMessage;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

public class FhirConverter implements IFhirConverter {
    private HL7ToFHIRConverter converter;
    public FhirConverter(HL7ToFHIRConverter converter) {
        this.converter = converter;
    }

    public FhirConvertedMessage HL7ToFHIRConversion(String validHL7Message) throws DiFhirException  {

        try {
            String output = this.converter.convert(validHL7Message);
            FhirConvertedMessage model = new FhirConvertedMessage();
            model.setHl7Message(validHL7Message);
            model.setFhirMessage(output);
            return model;
        } catch (Exception e) {
            throw new DiFhirException(e.getMessage());
        }

    }
}
