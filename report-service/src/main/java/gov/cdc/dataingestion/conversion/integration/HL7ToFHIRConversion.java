package gov.cdc.dataingestion.conversion.integration;

import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

public class HL7ToFHIRConversion implements IHL7ToFHIRConversion {
    private HL7ToFHIRConverter converter;
    public HL7ToFHIRConversion(HL7ToFHIRConverter converter) {
        this.converter = converter;
    }

    public HL7ToFHIRModel ConvertHL7v2ToFhir(ValidatedELRModel validatedELRModel, String topicName) throws UnsupportedOperationException {
        HL7ToFHIRModel model = new HL7ToFHIRModel();
        String output = this.converter.convert(validatedELRModel.getRawMessage());
        model.setRawId(validatedELRModel.getRawId());
        model.setFhirMessage(output);
        model.setCreatedBy(topicName);
        model.setUpdatedBy(topicName);
        return model;
    }
}
