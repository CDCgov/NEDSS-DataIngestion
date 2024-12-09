package gov.cdc.srtedataservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SrteDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrteDataServiceApplication.class, args);
    }

}
