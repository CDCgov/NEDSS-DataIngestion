package gov.cdc.nbs.mpidatasyncer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "gov.cdc.nbs.mpidatasyncer.entity.logs")
public class MpiDataSyncerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MpiDataSyncerApplication.class, args);
	}

}
