package gov.cdc.dataingestion.config;

import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.validation.integration.validator.HL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClassConfig {

    @Bean
    IHL7v2Validator hl7v2Validator() {
        return new HL7v2Validator(new HL7Helper());
    }


}