package gov.cdc.dataingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  Report service application.
 */
@SpringBootApplication
public class ServiceApplication {
    /**
     * Main method for spring boot application.
     * @param args
     */
    public static void main(final String[] args) {
        try {
            SpringApplication.run(ServiceApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
