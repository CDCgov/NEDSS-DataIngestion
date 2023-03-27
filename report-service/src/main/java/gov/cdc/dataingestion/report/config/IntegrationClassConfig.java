package gov.cdc.dataingestion.report.config;


import gov.cdc.dataingestion.report.integration.conversion.HL7ToFHIRConversion;
import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationClassConfig {
    @Bean
    IHL7ToFHIRConversion hl7ToFHIRConversion() {
        return new HL7ToFHIRConversion(new HL7ToFHIRConverter());
    }
}
