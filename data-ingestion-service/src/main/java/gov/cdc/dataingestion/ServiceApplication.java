package gov.cdc.dataingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  Report service application.
 */
@SpringBootApplication
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class ServiceApplication {
    /**
     * Main method for spring boot application.
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
