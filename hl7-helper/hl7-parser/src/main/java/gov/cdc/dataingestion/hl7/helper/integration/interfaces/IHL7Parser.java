package gov.cdc.dataingestion.hl7.helper.integration.interfaces;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;

public interface IHL7Parser {
    String hl7MessageStringValidation(String message) throws DiHL7Exception;
    HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception;
    String convertHL7ToXml(String hl7Message) throws DiHL7Exception;
}
