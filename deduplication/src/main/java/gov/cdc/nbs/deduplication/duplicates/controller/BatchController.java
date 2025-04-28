package gov.cdc.nbs.deduplication.duplicates.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.duplicates.step.UnprocessedPersonReader;

@RestController
@RequestMapping("/batch-job/start")
public class BatchController {

  private final TaskExecutorJobLauncher launcher;
  private final UnprocessedPersonReader personReader;
  private final Job deduplicationJob;

  public BatchController(
      @Qualifier("asyncJobLauncher") final TaskExecutorJobLauncher launcher,
      final UnprocessedPersonReader personReader,
      @Qualifier("deduplicationJob") final Job deduplicationJob) {
    this.launcher = launcher;
    this.personReader = personReader;
    this.deduplicationJob = deduplicationJob;
  }

  @GetMapping
  public void start()
      throws JobInstanceAlreadyCompleteException,
      JobExecutionAlreadyRunningException,
      JobParametersInvalidException,
      JobRestartException {
    JobParameters params = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    personReader.resetPagesRead();
    launcher.run(deduplicationJob, params);
  }

}
