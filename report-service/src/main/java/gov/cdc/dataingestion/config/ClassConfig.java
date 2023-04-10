package gov.cdc.dataingestion.config;
import gov.cdc.dataingestion.conversion.integration.HL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.hl7.helper.HL7ParserLibrary;
import gov.cdc.dataingestion.validation.integration.validator.CsvValidator;
import gov.cdc.dataingestion.validation.integration.validator.HL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClassConfig {

    @Bean
    IHL7v2Validator hl7v2Validator() {
        return new HL7v2Validator(new HL7ParserLibrary());
    }

    @Bean
    ICsvValidator csvValidator() {
        return new CsvValidator();
    }

    @Bean
    IHL7ToFHIRConversion hl7ToFHIRConversion() {
        return new HL7ToFHIRConversion(new HL7ToFHIRConverter());
    }


}