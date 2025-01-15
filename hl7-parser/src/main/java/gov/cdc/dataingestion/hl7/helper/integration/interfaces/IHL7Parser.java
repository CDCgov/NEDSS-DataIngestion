package gov.cdc.dataingestion.hl7.helper.integration.interfaces;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;

public interface IHL7Parser {
    String hl7MessageStringFormat(String message) throws DiHL7Exception;
    HL7ParsedMessage<OruR1> hl7StringParser(String message) throws DiHL7Exception;

    HL7ParsedMessage<OruR1> convert231To251(String message, HL7ParsedMessage<OruR1> preParsedMessage) throws  DiHL7Exception;
    ca.uhn.hl7v2.model.v231.message.ORU_R01 hl7v231StringParser(String message) throws DiHL7Exception;

    String hl7ORUValidation(String message) throws DiHL7Exception;

    String processFhsMessage(String message);

    String hl7MessageCustomMapping(String message, String customMapper);
}
