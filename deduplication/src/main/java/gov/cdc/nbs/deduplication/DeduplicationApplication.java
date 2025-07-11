package gov.cdc.nbs.deduplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.RollbackOn;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement(rollbackOn = RollbackOn.ALL_EXCEPTIONS)
public class DeduplicationApplication {

  public static void main(String[] args) {
    SpringApplication.run(DeduplicationApplication.class, args);
  }

}
