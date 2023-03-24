package gov.cdc.dataingestion.consumer.validationservice.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;
import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
public class KafkaProducerService {
    private KafkaTemplate<String, String> kafkaTemplate;
    private CountDownLatch latch = new CountDownLatch(1);

    public KafkaProducerService( KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessageFromController(String msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        sendMessage(record);
    }

    public void sendMessageFromCSVController(List<List<String>> msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        Gson gson = new Gson();
        String json = gson.toJson(msg);

        var record = new ProducerRecord<>(topic, uniqueID, json);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        sendMessage(record);
    }

    public void sendMessageAfterValidatingMessage(MessageModel msg, String topic) {
        String uniqueID = "Valid_" + msg.getMessageType() + "_" + UUID.randomUUID();
        Gson gson = new Gson();
        String json = gson.toJson(msg);
        var record = new ProducerRecord<>(topic, uniqueID, json);
        record.headers().add(KafkaHeaderValue.MessageType, msg.getMessageType().toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageVersion, msg.getMessageVersion().getBytes());
        sendMessage(record);
        latch.countDown();
    }


    private void sendMessage(ProducerRecord<String, String> record) {
        kafkaTemplate.send(record);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
