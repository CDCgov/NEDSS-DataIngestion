package gov.cdc.nbs.deduplication.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

  HealthController controller = new HealthController();

  @Test
  void should_return_health() {
    HealthResponse response = controller.health();

    assertThat(response.status()).isEqualTo("UP");
  }
}
