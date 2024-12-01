package gov.cdc.nbs.mpidatasyncer.config;

import gov.cdc.nbs.mpidatasyncer.service.logs.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncModeLogger {

  @Value("${sync.mode}")
  private boolean syncMode;

  @Value("${kafka.consumer.enabled}")
  private boolean kafkaEnabled;

  private final LogService logService;

  @EventListener(ApplicationReadyEvent.class)
  public void logPropertyValueOnStartup() {
    if (syncMode) {
      String message = (kafkaEnabled) ? "Application is in Sync mode Using Kafka."
          : "Application is in Sync mode Using ActiveMQ.";
      logService.logInfo(message);
    }
  }
}
