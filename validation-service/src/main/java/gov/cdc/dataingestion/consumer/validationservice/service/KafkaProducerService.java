package gov.cdc.dataingestion.consumer.validationservice.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;
import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
    }


    private void sendMessage(ProducerRecord<String, String> record) {
        kafkaTemplate.send(record);
    }
}
