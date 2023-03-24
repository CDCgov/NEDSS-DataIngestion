package gov.cdc.dataingestion.consumer.validationservice.integration.interfaces;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;
import org.springframework.stereotype.Component;

@Component
public interface IHL7v2Validator {
    MessageModel MessageValidation(String message) throws HL7Exception;
}
