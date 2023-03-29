package gov.cdc.dataingestion.validation.integration.validator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.model.RawERLModel;
import gov.cdc.dataingestion.validation.model.ValidatedELRModel;
import gov.cdc.dataingestion.validation.model.enums.MessageType;

import java.sql.Timestamp;

public class HL7v2Validator implements IHL7v2Validator {
    private String validatedKeyPrefix = "VALIDATED_";
    private HapiContext context;
    public HL7v2Validator(HapiContext context) {
        this.context = context;
    }

    public ValidatedELRModel MessageValidation(String id,  RawERLModel rawERLModel, String topicName) throws HL7Exception {
        String replaceSpecialCharacters;
        if (rawERLModel.getPayload().contains("\n")) {
            replaceSpecialCharacters = rawERLModel.getPayload().replaceAll("\n","\r");
        } else {
            replaceSpecialCharacters = rawERLModel.getPayload();
        }
        ValidatedELRModel model = new ValidatedELRModel();
        // Set validation
        context.setValidationContext(ValidationContextFactory.defaultValidation());
        PipeParser parser = context.getPipeParser();
        // if invalid HL7, exception will be thrown
        Message parsedMessage = parser.parse(replaceSpecialCharacters);

        model.setId(validatedKeyPrefix + id);
        model.setRawId(id);
        model.setRawMessage(replaceSpecialCharacters);
        model.setMessageType(MessageType.HL7.name());
        model.setMessageVersion(parsedMessage.getVersion());
        model.setCreated_by(topicName);
        return model;
    }
}