package gov.cdc.dataprocessing.service.implementation.person.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class DeduplicationClientTest {

  @Test
  void setsBaseUrl() {
    DeduplicationClient client = new DeduplicationClient();

    RestClient restClient = client.restClient("someUrl");

    assertThat(restClient).isNotNull();
  }

  @Test
  void setsNullBaseUrl() {
    DeduplicationClient client = new DeduplicationClient();

    RestClient restClient = client.restClient(null);

    assertThat(restClient).isNotNull();
  }
}
