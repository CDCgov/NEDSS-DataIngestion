package gov.cdc.dataingestion.hl7.helper.integration.interfaces;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;

public interface IHL7Parser {
    String hl7MessageStringValidation(String message) throws DiHL7Exception;
    HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception;
}
