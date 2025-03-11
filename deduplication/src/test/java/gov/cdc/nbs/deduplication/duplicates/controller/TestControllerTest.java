package gov.cdc.nbs.deduplication.duplicates.controller;

import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.duplicates.controller.TestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;


@ExtendWith(MockitoExtension.class)
class TestControllerTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job deduplicationJob;

  @InjectMocks
  private TestController testController;

  @Test
  void testBatchJob() throws Exception {
    testController.testBatchJob();
    verify(jobLauncher, times(1)).run(eq(deduplicationJob), any(JobParameters.class));
  }

}
