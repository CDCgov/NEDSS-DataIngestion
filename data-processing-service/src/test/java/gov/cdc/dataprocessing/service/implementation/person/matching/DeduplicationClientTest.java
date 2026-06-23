package gov.cdc.dataprocessing.service.implementation.person.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DeduplicationClientTest {

  @Test
  void setsBaseUrl() {
    DeduplicationClient client = new DeduplicationClient();

    DeduplicationService service = client.deduplicationService("someUrl");

    assertThat(service).isNotNull();
  }

  @Test
  void setsNullBaseUrl() {
    DeduplicationClient client = new DeduplicationClient();

    DeduplicationService service = client.deduplicationService(null);

    assertThat(service).isNotNull();
  }
}
