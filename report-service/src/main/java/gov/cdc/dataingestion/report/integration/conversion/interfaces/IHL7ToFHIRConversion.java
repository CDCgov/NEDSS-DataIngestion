package gov.cdc.dataingestion.report.integration.conversion.interfaces;

import gov.cdc.dataingestion.report.repository.model.HL7toFhirModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

public interface IHL7ToFHIRConversion {
    HL7toFhirModel ConvertHL7v2ToFhir(ValidatedELRModel validatedModel, String topicName);
}
