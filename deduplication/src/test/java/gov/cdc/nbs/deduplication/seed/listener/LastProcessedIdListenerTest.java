package gov.cdc.nbs.deduplication.seed.listener;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.batch.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

class LastProcessedIdListenerTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    private JobExecution jobExecution;

    @Mock
    private JobParameters jobParameters;

    @InjectMocks
    private LastProcessedIdListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAfterJob_Success() {
        Long lastProcessedId = 123L;

        // Mock jobExecution to return a completed status
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // Mock jobParameters to return lastProcessedId
        when(jobExecution.getJobParameters()).thenReturn(jobParameters);
        when(jobParameters.getLong("lastProcessedId")).thenReturn(lastProcessedId);

        listener.afterJob(jobExecution);

        // Capture the parameters passed to the update method
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jdbcTemplate, times(1)).update(eq("UPDATE last_processed_id SET last_processed_id = :lastProcessedId WHERE id = 1"), paramsCaptor.capture());

        Map<String, Object> capturedParams = paramsCaptor.getValue();
        assertThat(capturedParams)
                .isNotNull()
                .containsEntry("lastProcessedId", lastProcessedId);
    }

    @Test
    void testAfterJob_NoLastProcessedId() {
        // Mock jobExecution to return a completed status
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // Mock jobParameters to return null for lastProcessedId
        when(jobExecution.getJobParameters()).thenReturn(jobParameters);
        when(jobParameters.getLong("lastProcessedId")).thenReturn(null);

        listener.afterJob(jobExecution);

        // Verify that update method is not called since lastProcessedId is null
        verify(jdbcTemplate, never()).update(anyString(), any(Map.class));
    }

    @Test
    void testAfterJob_FailedJob() {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);

        listener.afterJob(jobExecution);

        // Verify that update method is not called for a failed job
        verify(jdbcTemplate, never()).update(anyString(), any(Map.class));
    }
}
