package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.deadletter.repository.IDltRepository;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadLetterService {
    private static final String CREATED_BY = "DeadLetterService";
    private final IDltRepository dltRepository;
    private final KafkaProducerService kafkaProducerService;
}
