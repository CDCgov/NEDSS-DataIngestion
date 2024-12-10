package gov.cdc.nbs.deduplication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import gov.cdc.nbs.deduplication.config.TestConfig;

@SpringBootTest(classes = TestConfig.class)
class DeduplicationApplicationTests {

  @Autowired
  private TestConfig config;

  @Test
  void contextLoads() {
    assertThat(config).isNotNull();
  }

}
