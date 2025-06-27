package gov.cdc.nbs.deduplication.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClockProvider {

  @Bean
  Clock clock() {
    return Clock.systemDefaultZone();
  }

}