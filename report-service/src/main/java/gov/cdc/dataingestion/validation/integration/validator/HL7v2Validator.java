package gov.cdc.dataingestion.validation.integration.validator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.constant.enums.EnumMessageType;

import static gov.cdc.dataingestion.constant.SupportedHl7Version.VERSION231;
import static gov.cdc.dataingestion.constant.SupportedHl7Version.VERSION251;

public class HL7v2Validator implements IHL7v2Validator {
    private final HL7Helper hl7Helper;
    public HL7v2Validator(HL7Helper hl7Helper) {
        this.hl7Helper = hl7Helper;
    }

    public String MessageStringValidation(String message) throws DiHL7Exception {
        return this.hl7Helper.hl7StringValidator(message);
    }

    public ValidatedELRModel MessageValidation(String id, RawERLModel rawERLModel, String topicName) throws DiHL7Exception {
        String replaceSpecialCharacters = MessageStringValidation(rawERLModel.getPayload());
        ValidatedELRModel model = new ValidatedELRModel();
        var parsedMessage = this.hl7Helper.hl7StringParser(replaceSpecialCharacters);

        if (parsedMessage.getOriginalVersion().equalsIgnoreCase(VERSION251) ||
            parsedMessage.getOriginalVersion().equalsIgnoreCase(VERSION231)) {
            model.setRawId(id);
            model.setRawMessage(replaceSpecialCharacters);
            model.setMessageType(EnumMessageType.HL7.name());
            model.setMessageVersion(parsedMessage.getOriginalVersion());
            model.setCreatedBy(topicName);
            model.setUpdatedBy(topicName);
        }
        else {
            throw new DiHL7Exception("Invalid HL7 version");
        }
        return model;
    }
}