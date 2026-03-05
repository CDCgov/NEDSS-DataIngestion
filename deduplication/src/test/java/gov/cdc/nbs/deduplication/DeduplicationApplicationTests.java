package gov.cdc.nbs.deduplication;

import static org.assertj.core.api.Assertions.assertThat;

import gov.cdc.nbs.deduplication.config.DataSourceConfig;
import gov.cdc.nbs.deduplication.config.container.UseTestContainers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@UseTestContainers
@Disabled
class DeduplicationApplicationTests {

  @Autowired private DataSourceConfig config;

  @Test
  @Disabled
  void contextLoads() {
    assertThat(config).isNotNull();
  }
}
