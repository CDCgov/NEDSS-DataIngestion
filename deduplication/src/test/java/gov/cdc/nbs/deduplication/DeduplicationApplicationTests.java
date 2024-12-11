package gov.cdc.nbs.deduplication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import gov.cdc.nbs.deduplication.config.DataSourceConfig;

@SpringBootTest
@ActiveProfiles("test")
class DeduplicationApplicationTests {

  @Autowired
  private DataSourceConfig config;

  @Test
  void contextLoads() {
    assertThat(config).isNotNull();
  }

}
