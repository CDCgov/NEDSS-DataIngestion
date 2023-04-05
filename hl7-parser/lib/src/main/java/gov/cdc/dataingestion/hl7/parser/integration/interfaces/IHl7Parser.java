package gov.cdc.dataingestion.hl7.parser.integration.interfaces;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.parser.model.Hl7ParsedMessage;

public interface IHl7Parser {
    String hl7MessageStringValidation(String message) throws HL7Exception;
    Hl7ParsedMessage hl7StringParser(String message) throws HL7Exception;
}
