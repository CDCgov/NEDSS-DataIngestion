package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.service.interfaces.core.IManagerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEdxLogConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEdxLogConsumer.class);
    private final IManagerService managerService;

    public KafkaEdxLogConsumer(IManagerService managerService) {
        this.managerService = managerService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_edx_log}"
    )
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws EdxLogException, DataProcessingConsumerException {
        managerService.processingEdxLog("data");
    }
}
