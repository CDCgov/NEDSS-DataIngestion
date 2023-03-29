package gov.cdc.dataingestion.report.integration.conversion;

import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.report.model.HL7toFhirModel;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

public class HL7ToFHIRConversion implements IHL7ToFHIRConversion {
    private HL7ToFHIRConverter converter;
    public HL7ToFHIRConversion(HL7ToFHIRConverter converter) {
        this.converter = converter;
    }

    public HL7toFhirModel ConvertHL7v2ToFhir(HL7toFhirModel hl7Message, String rawMessage) throws UnsupportedOperationException {
        HL7toFhirModel model = new HL7toFhirModel();
        String output = this.converter.convert(rawMessage);
        model.setRawHL7Message(hl7Message);
        model.setConvertedFhirMessage(output);
        return model;
    }
}
