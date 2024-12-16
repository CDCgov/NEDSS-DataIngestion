package gov.cdc.dataprocessing.service.implementation.person.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DeduplicationServiceTest {

  @Test
  void shouldCreateClient() {
    DeduplicationService service = new DeduplicationService(null);
    assertThat(service).isNotNull();
  }

  @Test
  void shouldCreateClientWithUrl() {
    DeduplicationService service = new DeduplicationService("someBaseUrl");
    assertThat(service).isNotNull();
  }
}
