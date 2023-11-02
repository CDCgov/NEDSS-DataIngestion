package gov.cdc.dataingestion.validation.services;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.services.interfaces.IHl7Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Hl7Service implements IHl7Service {
    private final IHL7v2Validator hl7v2Validator;

    @Autowired
    public Hl7Service(IHL7v2Validator hl7v2Validator) {
        this.hl7v2Validator = hl7v2Validator;
    }

    public String hl7Validator(String message) throws DiHL7Exception {
        var msg = this.hl7v2Validator.MessageStringValidation(message);
        msg = this.hl7v2Validator.hl7MessageValidation(msg);
        return msg;
    }
}
