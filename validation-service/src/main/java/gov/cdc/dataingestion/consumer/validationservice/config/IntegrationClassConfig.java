package gov.cdc.dataingestion.consumer.validationservice.config;

import ca.uhn.hl7v2.DefaultHapiContext;
import gov.cdc.dataingestion.consumer.validationservice.integration.CsvValidator;
import gov.cdc.dataingestion.consumer.validationservice.integration.HL7v2Validator;
import gov.cdc.dataingestion.consumer.validationservice.integration.interfaces.ICsvValidator;
import gov.cdc.dataingestion.consumer.validationservice.integration.interfaces.IHL7v2Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationClassConfig {

    @Bean
    IHL7v2Validator hl7v2Validator() {
        return new HL7v2Validator(new DefaultHapiContext());
    }

    @Bean
    ICsvValidator csvValidator() {
        return new CsvValidator();
    }


}
