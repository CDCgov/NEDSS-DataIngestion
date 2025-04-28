package gov.cdc.nbs.deduplication.duplicates;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import gov.cdc.nbs.deduplication.duplicates.step.UnprocessedPersonReader;

@ExtendWith(MockitoExtension.class)
class BatchJobSchedulerTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job deduplicationJob;

  @Mock
  private UnprocessedPersonReader personReader;

  @InjectMocks
  private BatchJobScheduler batchJobScheduler;

  @Test
  void runJobTest() throws Exception {
    batchJobScheduler.runJob();

    ArgumentCaptor<JobParameters> captor = forClass(JobParameters.class);
    verify(jobLauncher, times(1)).run(eq(deduplicationJob), captor.capture());

    JobParameters capturedParams = captor.getValue();
    assertThat(capturedParams.getLong("time")).isNotNull();

    verify(personReader, times(1)).resetPagesRead();
  }
}
