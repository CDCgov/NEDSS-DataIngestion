package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KafkaNonTransactionalManagerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaNonTransactionalManagerConsumer.class);

    private final KafkaManagerProducer kafkaManagerProducer;

    private static final int BATCH_SIZE = 10;
    private final List<String> messageBatch = new ArrayList<>();

    public KafkaNonTransactionalManagerConsumer(KafkaManagerProducer kafkaManagerProducer) {
        this.kafkaManagerProducer = kafkaManagerProducer;
    }


    @KafkaListener(
            topics = "${kafka.topic.elr_micro}",
            containerFactory = "nonTransactionalKafkaListenerContainerFactory"
    )
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingException, DataProcessingConsumerException {
        logger.info("HIT");
        kafkaManagerProducer.sendUnprocessedData(message);

    }

//    public void handleMessage(String message,
//                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//                              @Header(KafkaCustomHeader.DATA_TYPE) String dataType)
//            throws DataProcessingException, DataProcessingConsumerException {
//            var profile = this.authUserService.getAuthUserInfo(nbsUser);
//            AuthUtil.setGlobalAuthUser(profile);
//            managerService.processDistribution(dataType,message);
//    }
}
