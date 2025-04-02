package gov.cdc.nbs.deduplication.health;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

  HealthController controller = new HealthController();

  @Test
  void should_return_health() {
    HealthResponse response = controller.health();

    assertThat(response.status()).isEqualTo("UP");
  }
}
