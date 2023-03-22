package gov.cdc.dataingestion.report.integration.conversion;

import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

public class HL7ToFHIRConversion implements IHL7ToFHIRConversion {
    private HL7ToFHIRConverter converter;
    public HL7ToFHIRConversion(HL7ToFHIRConverter converter) {
        this.converter = converter;
    }

    public String ConvertHL7v2ToFhir(String hl7Message) {
        // converted string is in JSON format
        String output = this.converter.convert(hl7Message);
        return output;
    }
}
