package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.exception.KafkaProducerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableScheduling
class SchedulerConfigTest {
    @Test
    void testScheduleTask_SchedulerEnabled() throws KafkaProducerException {
        ElrDeadLetterService mockService = mock(ElrDeadLetterService.class);
        SchedulerConfig schedulerConfig = new SchedulerConfig(mockService);
        ReflectionTestUtils.setField(schedulerConfig, "isSchedulerEnabled", true);

        schedulerConfig.scheduleTask();

        verify(mockService, times(1)).processFailedMessagesFromKafka();
    }

    @Test
    void testScheduleTask_SchedulerDisabled() throws KafkaProducerException {
        ElrDeadLetterService mockService = mock(ElrDeadLetterService.class);
        SchedulerConfig schedulerConfig = new SchedulerConfig(mockService);
        ReflectionTestUtils.setField(schedulerConfig, "isSchedulerEnabled", false);

        schedulerConfig.scheduleTask();

        verify(mockService, never()).processFailedMessagesFromKafka();
    }

    @Test
    void testScheduleTask_ServiceThrowsException() throws KafkaProducerException {
        ElrDeadLetterService mockService = mock(ElrDeadLetterService.class);
        doThrow(new KafkaProducerException("Test exception"))
                .when(mockService).processFailedMessagesFromKafka();
        SchedulerConfig schedulerConfig = new SchedulerConfig(mockService);
        ReflectionTestUtils.setField(schedulerConfig, "isSchedulerEnabled", true);

        try {
            schedulerConfig.scheduleTask();
        } catch (KafkaProducerException e) {
            verify(mockService, times(1)).processFailedMessagesFromKafka();
            return;
        }
        throw new AssertionError("Expected KafkaProducerException was not thrown.");
    }
}