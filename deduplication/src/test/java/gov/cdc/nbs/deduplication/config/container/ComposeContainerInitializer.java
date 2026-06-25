package gov.cdc.nbs.deduplication.config.container;

import java.io.File;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

class ComposeContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final Logger log = LoggerFactory.getLogger(ComposeContainerInitializer.class);
  private static final Slf4jLogConsumer consumer = new Slf4jLogConsumer(log);
  private static final File composeFile = new File("../docker-compose.yml");
  private static boolean started = false;

  @SuppressWarnings("resource")
  private static final ComposeContainer container =
      new ComposeContainer(composeFile)
          // Don't pull all the containers listed in the compose file
          .withPull(false)
          // List specific services
          .withServices("nbs-mssql", "di-record-linker")
          .waitingFor("nbs-mssql", Wait.forHealthcheck())
          .withLogConsumer("nbs-mssql", consumer)
          .withLogConsumer("di-record-linker", consumer)
          .withStartupTimeout(Duration.ofMinutes(10));

  @Override
  public void initialize(@NonNull final ConfigurableApplicationContext context) {
    if (started) {
      return;
    }

    container.start();
    started = true;
  }
}
