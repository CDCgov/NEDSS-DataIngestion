package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.exception.KafkaProducerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Value("${dlt.scheduler.enabled}")
    private boolean isSchedulerEnabled;

    private final ElrDeadLetterService elrDeadLetterService;

    public SchedulerConfig(ElrDeadLetterService elrDeadLetterService) {
        this.elrDeadLetterService = elrDeadLetterService;
    }

    @Scheduled(cron = "${dlt.scheduler.cron.expression}")
    public void scheduleTask() throws KafkaProducerException {
        if (isSchedulerEnabled) {
            elrDeadLetterService.processFailedMessagesFromKafka();
        }
    }
}
