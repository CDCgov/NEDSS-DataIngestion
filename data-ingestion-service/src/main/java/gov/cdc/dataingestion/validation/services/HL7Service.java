package gov.cdc.dataingestion.validation.services;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class HL7Service implements IHL7Service {
    private final IHL7v2Validator hl7v2Validator;

    @Autowired
    public HL7Service(IHL7v2Validator hl7v2Validator) {
        this.hl7v2Validator = hl7v2Validator;
    }

    public String hl7Validator(String message) throws DiHL7Exception {
        var validHL7Message = hl7v2Validator.messageStringFormat(message);
        validHL7Message = hl7v2Validator.hl7MessageValidation(validHL7Message);
        return validHL7Message;
    }
}
