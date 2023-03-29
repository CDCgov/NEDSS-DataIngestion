package gov.cdc.dataingestion.report.integration.conversion;

import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.report.repository.model.HL7toFhirModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

import java.sql.Timestamp;

public class HL7ToFHIRConversion implements IHL7ToFHIRConversion {
    private String IdPrefix = "FHIR_";
    private HL7ToFHIRConverter converter;
    public HL7ToFHIRConversion(HL7ToFHIRConverter converter) {
        this.converter = converter;
    }

    public HL7toFhirModel ConvertHL7v2ToFhir(ValidatedELRModel validatedELRModel, String topicName) throws UnsupportedOperationException {
        HL7toFhirModel model = new HL7toFhirModel();
        String output = this.converter.convert(validatedELRModel.getRawMessage());
        model.setId(IdPrefix + validatedELRModel.getRawId());
        model.setRawId(validatedELRModel.getRawId());
        model.setFhirMessage(output);
        model.setCreatedBy(topicName);
        return model;
    }
}
