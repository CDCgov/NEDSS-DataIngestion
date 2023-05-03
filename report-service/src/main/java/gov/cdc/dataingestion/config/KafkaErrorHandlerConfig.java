//package gov.cdc.dataingestion.config;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaOperations;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//import org.springframework.kafka.listener.ListenerExecutionFailedException;
//import org.springframework.util.StringUtils;
//
//import java.util.function.BiConsumer;
//
//@Configuration
//public class KafkaErrorHandlerConfig   {
//
//
//    @Bean
//    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, String> template) {
//        return new DeadLetterPublishingRecoverer(template, (record, ex) -> {
//            if (ex instanceof Exception) {
//                record.headers().add("x-exception-message", ex.getMessage().getBytes());
//
//            }
//            return null;
//        });
//    }
//}
