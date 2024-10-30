package gov.cdc.dataingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  Report service application.
 */
@SpringBootApplication
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class ServiceApplication {
    /**
     * Main method for spring boot application.
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
