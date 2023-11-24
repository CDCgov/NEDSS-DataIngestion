package gov.cdc.dataingestion.conversion.integration.interfaces;

import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

public interface IHL7ToFHIRConversion {
    HL7ToFHIRModel convertHL7v2ToFhir(ValidatedELRModel validatedModel, String topicName);
}
