package gov.cdc.dataingestion.report.integration.conversion.interfaces;

public interface IHL7ToFHIRConversion {
    String ConvertHL7v2ToFhir(String hl7Message);
}
