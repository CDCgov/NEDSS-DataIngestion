package gov.cdc.dataingestion.validation.integration.validator;

import gov.cdc.dataingestion.hl7.helper.integration.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.validation.model.enums.MessageType;
import gov.cdc.dataingestion.hl7.helper.HL7ParserLibrary;
public class HL7v2Validator implements IHL7v2Validator {
    private HL7ParserLibrary hl7Parser;
    public HL7v2Validator(HL7ParserLibrary hl7Parser) {
        this.hl7Parser = hl7Parser;
    }

    public ValidatedELRModel MessageValidation(String id, RawERLModel rawERLModel, String topicName) throws DiHL7Exception {
        String validMessageString = this.hl7Parser.hl7StringValidator(rawERLModel.getPayload());
        ValidatedELRModel model = new ValidatedELRModel();
        HL7ParsedMessage parsedMessage = this.hl7Parser.hl7StringParser(validMessageString);
        model.setRawId(id);
        model.setRawMessage(validMessageString);
        model.setMessageType(MessageType.HL7.name());
        model.setMessageVersion(parsedMessage.getVersion());
        model.setCreatedBy(topicName);
        model.setUpdatedBy(topicName);
        return model;
    }
}