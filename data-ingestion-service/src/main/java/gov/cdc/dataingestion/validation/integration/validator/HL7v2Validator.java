package gov.cdc.dataingestion.validation.integration.validator;

import gov.cdc.dataingestion.constant.enums.EnumMessageType;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class HL7v2Validator implements IHL7v2Validator {
    private final HL7Helper hl7Helper;
    public HL7v2Validator(HL7Helper hl7Helper) {
        this.hl7Helper = hl7Helper;
    }

    public String messageStringFormat(String message) throws DiHL7Exception {
        return this.hl7Helper.hl7StringFormat(message);
    }

    public String hl7MessageValidation(String message) throws DiHL7Exception {
        return this.hl7Helper.hl7Validation(message);
    }

    public String processFhsMessage (String message) {
        return this.hl7Helper.processFhsMessage(message);
    }

    public ValidatedELRModel messageValidation(String id, RawElrModel rawElrModel, String topicName, boolean validationActive) throws DiHL7Exception {
        String replaceSpecialCharacters = messageStringFormat(rawElrModel.getPayload());
        replaceSpecialCharacters = this.hl7Helper.processFhsMessage(replaceSpecialCharacters);
        replaceSpecialCharacters = this.hl7Helper.hl7Validation(replaceSpecialCharacters);
        ValidatedELRModel model = new ValidatedELRModel();
        try {
            var parsedMessage = this.hl7Helper.hl7StringParser(replaceSpecialCharacters);//check
            model.setRawId(id);
            model.setRawMessage(replaceSpecialCharacters);
            model.setMessageType(EnumMessageType.HL7.name());
            model.setMessageVersion(parsedMessage.getOriginalVersion());
            model.setCreatedBy(topicName);
            model.setUpdatedBy(topicName);
        } catch (Exception e) {
            throw new DiHL7Exception(e.getMessage());
        }
        return model;
    }
}