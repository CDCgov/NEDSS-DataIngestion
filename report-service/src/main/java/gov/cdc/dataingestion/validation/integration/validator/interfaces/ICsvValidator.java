package gov.cdc.dataingestion.validation.integration.validator.interfaces;

import gov.cdc.dataingestion.validation.model.ValidatedELRModel;
import org.springframework.stereotype.Component;
import java.io.Reader;
import java.util.List;
@Component
public interface ICsvValidator {
    ValidatedELRModel ValidateCSVAgainstCVSSchema(String message) throws Exception;
    List<List<String>> ReadLineByLine(Reader reader) throws Exception;
}