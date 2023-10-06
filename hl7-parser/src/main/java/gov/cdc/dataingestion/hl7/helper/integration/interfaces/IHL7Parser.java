package gov.cdc.dataingestion.hl7.helper.integration.interfaces;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;

public interface IHL7Parser {
    String hl7MessageStringValidation(String message) throws DiHL7Exception;
    HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception;

    HL7ParsedMessage convert231To251(String message, HL7ParsedMessage preParsedMessage) throws  DiHL7Exception;
    ca.uhn.hl7v2.model.v231.message.ORU_R01 hl7v231StringParser(String message) throws DiHL7Exception;

    String hl7ORUValidation(String message) throws DiHL7Exception;
}
