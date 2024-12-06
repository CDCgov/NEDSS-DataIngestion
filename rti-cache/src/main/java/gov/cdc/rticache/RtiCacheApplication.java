package gov.cdc.rticache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RtiCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(RtiCacheApplication.class, args);
    }

}
