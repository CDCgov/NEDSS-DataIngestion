package gov.cdc.dataingestion.validation.config;
import ca.uhn.hl7v2.DefaultHapiContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
import gov.cdc.dataingestion.validation.integration.validator.HL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.CsvValidator;

@Configuration
public class ClassConfig {

    @Bean
    IHL7v2Validator hl7v2Validator() {
        return new HL7v2Validator(new DefaultHapiContext());
    }

    @Bean
    ICsvValidator csvValidator() {
        return new CsvValidator();
    }



}