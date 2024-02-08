package gov.cdc.dataprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 *  Report service application.
 */
@SpringBootApplication
@EnableCaching
public class ServiceApplication {
    /**
     * Main method for spring boot application.
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
