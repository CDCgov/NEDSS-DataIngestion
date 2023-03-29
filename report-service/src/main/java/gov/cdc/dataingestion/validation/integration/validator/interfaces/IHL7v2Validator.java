package gov.cdc.dataingestion.validation.integration.validator.interfaces;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.validation.model.RawERLModel;
import gov.cdc.dataingestion.validation.model.ValidatedELRModel;
import org.springframework.stereotype.Component;

@Component
public interface IHL7v2Validator {
    ValidatedELRModel MessageValidation(String message, RawERLModel rawERLModel) throws HL7Exception;
}