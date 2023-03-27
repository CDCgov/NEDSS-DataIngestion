package gov.cdc.dataingestion.report.integration.conversion.interfaces;

import gov.cdc.dataingestion.report.model.HL7toFhirModel;

public interface IHL7ToFHIRConversion {
    HL7toFhirModel ConvertHL7v2ToFhir(String hl7Message);
}
