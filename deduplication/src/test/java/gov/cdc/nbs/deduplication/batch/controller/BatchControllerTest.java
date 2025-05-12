package gov.cdc.nbs.deduplication.batch.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;

import gov.cdc.nbs.deduplication.batch.step.UnprocessedPersonReader;

@ExtendWith(MockitoExtension.class)
class BatchControllerTest {

  @Mock
  private TaskExecutorJobLauncher jobLauncher;

  @Mock
  private UnprocessedPersonReader personReader;

  @Mock
  private Job deduplicationJob;

  @InjectMocks
  private BatchController batchController;

  @Test
  void testBatchJob() throws Exception {
    batchController.start();
    verify(personReader, times(1)).resetPagesRead();
    verify(jobLauncher, times(1)).run(eq(deduplicationJob), any(JobParameters.class));
  }

}
