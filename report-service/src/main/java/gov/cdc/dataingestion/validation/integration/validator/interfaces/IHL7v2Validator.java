package gov.cdc.dataingestion.validation.integration.validator.interfaces;
import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.stereotype.Component;

public interface IHL7v2Validator {
    ValidatedELRModel MessageValidation(String message, RawERLModel rawERLModel, String topicName, boolean validationActive) throws DiHL7Exception;
    String messageValidation(String message) throws DiHL7Exception;
    String MessageStringValidation(String message) throws DiHL7Exception;
    String processFhsMessage (String message);
}