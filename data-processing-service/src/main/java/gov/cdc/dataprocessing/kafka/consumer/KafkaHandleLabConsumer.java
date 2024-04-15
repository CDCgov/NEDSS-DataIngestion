package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaHandleLabConsumer {
    @Value("${kafka.topic.elr_edx_log}")
    private String logTopic = "elr_edx_log";

    private final KafkaManagerProducer kafkaManagerProducer;
    private final IManagerService managerService;

    public KafkaHandleLabConsumer(KafkaManagerProducer kafkaManagerProducer,
                                  IManagerService managerService) {
        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerService = managerService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_lab}"
    )
    public void handleMessage(String message)
            throws DataProcessingConsumerException {
        try {
            managerService.initiatingLabProcessing(message);
        }
        catch (Exception e)
        {
            kafkaManagerProducer.sendData(logTopic, "result");
        }
    }
}
