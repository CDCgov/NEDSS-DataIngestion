package gov.cdc.dataingestion.validation.integration.validator.interfaces;
import gov.cdc.dataingestion.hl7.helper.integration.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.stereotype.Component;

public interface IHL7v2Validator {
    ValidatedELRModel MessageValidation(String message, RawERLModel rawERLModel, String topicName) throws DiHL7Exception;
}