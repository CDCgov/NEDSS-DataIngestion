package gov.cdc.dataingestion.validation.integration.validator.interfaces;

import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

public interface IHL7DuplicateValidator {
     void validateHL7Document(ValidatedELRModel hl7ValidatedModel) throws DuplicateHL7FileFoundException;
}
