package gov.cdc.nbs.mpidatasyncer.service.activemq;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ScheduledPersonSyncHandler {

  private final PersonSyncHandler readProducerService;

  @Value("${sync.fixedRate}")
  private long fixedRate;

  @Value("${sync.mode}")
  private boolean syncMode;

  @Value("${kafka.consumer.enabled}")
  private boolean isKafkaConsumerEnabled;

  @Scheduled(fixedRateString = "${sync.fixedRate}")
  public void sendBatchesToQueue() {
    if (syncMode && !isKafkaConsumerEnabled) {
      readProducerService.syncRecords();
    }
  }
}
