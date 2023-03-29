package gov.cdc.dataingestion.validation;

import gov.cdc.dataingestion.validation.config.ClassConfig;
import gov.cdc.dataingestion.validation.config.DataSourceConfig;
import gov.cdc.dataingestion.validation.config.KafkaConfig;
import gov.cdc.dataingestion.validation.repository.RawELRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ValidationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(
                        ValidationServiceApplication.class,

                args);
    }

}