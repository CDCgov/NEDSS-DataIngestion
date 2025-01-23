package gov.cdc.dataingestion.validation.integration.validator.interfaces;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

public interface IHL7v2Validator {
    ValidatedELRModel messageValidation(String message, RawERLModel rawERLModel, String topicName, boolean validationActive, String customMapper) throws DiHL7Exception;
    String hl7MessageValidation(String message) throws DiHL7Exception;
    String messageStringFormat(String message) throws DiHL7Exception;
    String processFhsMessage (String message);
}