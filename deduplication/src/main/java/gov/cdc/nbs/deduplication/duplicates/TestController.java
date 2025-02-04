package gov.cdc.nbs.deduplication.duplicates;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch-job/test")
public class TestController {

  private final JobLauncher jobLauncher;
  private final Job deduplicationJob;

  public TestController(final JobLauncher jobLauncher, final Job deduplicationJob) {
    this.jobLauncher = jobLauncher;
    this.deduplicationJob = deduplicationJob;
  }

  @GetMapping
  public void testBatchJob()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    JobParameters params = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(deduplicationJob, params);
  }


}
