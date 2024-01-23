package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.kafka.producer.KafkaObservationProducer;
import gov.cdc.dataprocessing.service.ObservationService;
import gov.cdc.dataprocessing.service.interfaces.IObservationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEdxLogConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEdxLogConsumer.class);

}
