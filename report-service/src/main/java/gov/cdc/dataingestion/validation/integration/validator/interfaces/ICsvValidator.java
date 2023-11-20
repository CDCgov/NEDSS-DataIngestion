package gov.cdc.dataingestion.validation.integration.validator.interfaces;

import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.stereotype.Component;
@Component
public interface ICsvValidator {
    ValidatedELRModel validateCSVAgainstCVSSchema(String message) throws Exception;
}