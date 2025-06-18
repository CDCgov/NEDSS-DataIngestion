package gov.cdc.nbs.deduplication;

import gov.cdc.nbs.deduplication.config.DataSourceConfig;
import gov.cdc.nbs.deduplication.config.container.UseTestContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@UseTestContainers
class DeduplicationApplicationTests {

  @Autowired
  private DataSourceConfig config;

  @Test
  void contextLoads() {
    assertThat(config).isNotNull();
  }

}
