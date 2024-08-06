package gov.cdc.dataprocessing.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KafkaNonTransactionalManagerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaNonTransactionalManagerConsumer.class);

    private final KafkaManagerProducer kafkaManagerProducer;

    private final List<String> messageBatch = new ArrayList<>();

    private static final int BATCH_SIZE = 10;

    public KafkaNonTransactionalManagerConsumer(KafkaManagerProducer kafkaManagerProducer) {
        this.kafkaManagerProducer = kafkaManagerProducer;
    }


    @KafkaListener(
            topics = "${kafka.topic.elr_micro}",
            containerFactory = "nonTransactionalKafkaListenerContainerFactory"
    )
    public void handleMessages(
            @Payload List<String> messages,
            @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics) {

        logger.info("Received batch of messages: {}", messages.size());

        synchronized (messageBatch) {
            messageBatch.addAll(messages);

            if (messageBatch.size() >= BATCH_SIZE) {
                processBatch();
            }
        }
    }

    @Scheduled(fixedRate = 3000)  // Run every 5 seconds
    public void checkBatch() {
        synchronized (messageBatch) {
            if (!messageBatch.isEmpty()) {
                processBatch();
            }
        }
    }

    private void processBatch() {
        logger.info("Processing batch of messages: {}", messageBatch.size());

        Gson gson = new Gson();
        String json = gson.toJson(messageBatch);
        kafkaManagerProducer.sendUnprocessedData(json);

        messageBatch.clear();
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
