package gov.cdc.dataingestion.validation.integration.validator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.constant.enums.EnumMessageType;
public class HL7v2Validator implements IHL7v2Validator {
    private final HapiContext context;
    public HL7v2Validator(HapiContext context) {
        this.context = context;
    }

    public String MessageStringValidation(String message) {
        String replaceSpecialCharacters;
        if (message.contains("\n")) {
            replaceSpecialCharacters = message.replaceAll("\n","\r");
        } else if (message.contains("\n\r")) {
            replaceSpecialCharacters = message.replaceAll("\n\r","\r");
        } else {
            if (message.contains("\\n")) {
                replaceSpecialCharacters = message.replaceAll("\\\\n","\r");
            } else if (message.contains("\\r")) {
                replaceSpecialCharacters = message.replaceAll("\\\\r","\r");
            }
            else {
                replaceSpecialCharacters = message;
            }
        }

        replaceSpecialCharacters = replaceSpecialCharacters.replaceAll("\\\\+", "\\\\");
        return replaceSpecialCharacters;
    }

    public ValidatedELRModel MessageValidation(String id, RawERLModel rawERLModel, String topicName) throws HL7Exception {
        String replaceSpecialCharacters = MessageStringValidation(rawERLModel.getPayload());

        replaceSpecialCharacters = replaceSpecialCharacters.replaceAll("\\\\+", "\\\\");

        ValidatedELRModel model = new ValidatedELRModel();
        // Set validation
        context.setValidationContext(ValidationContextFactory.defaultValidation());
        PipeParser parser = context.getPipeParser();
        // if invalid HL7, exception will be thrown
        Message parsedMessage = parser.parse(replaceSpecialCharacters);

        model.setRawId(id);
        model.setRawMessage(replaceSpecialCharacters);
        model.setMessageType(EnumMessageType.HL7.name());
        model.setMessageVersion(parsedMessage.getVersion());
        model.setCreatedBy(topicName);
        model.setUpdatedBy(topicName);
        return model;
    }
}