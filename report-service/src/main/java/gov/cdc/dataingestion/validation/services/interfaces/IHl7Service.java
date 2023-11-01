package gov.cdc.dataingestion.validation.services.interfaces;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;

public interface IHl7Service {
    String hl7Validator(String message) throws DiHL7Exception;
}
