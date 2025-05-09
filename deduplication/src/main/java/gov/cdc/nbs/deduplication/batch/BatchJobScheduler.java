package gov.cdc.nbs.deduplication.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.step.UnprocessedPersonReader;

@Component
@ConditionalOnProperty(value = "deduplication.batch.schedule.enabled", havingValue = "true")
public class BatchJobScheduler {

  private final TaskExecutorJobLauncher launcher;
  private final UnprocessedPersonReader personReader;
  private final Job deduplicationJob;

  @Value("${deduplication.batch.schedule.cron:0 0 1 * * ?}") // Default to daily at 1 AM
  private String cronSchedule;

  public BatchJobScheduler(
      @Qualifier("asyncJobLauncher") final TaskExecutorJobLauncher launcher,
      final UnprocessedPersonReader personReader,
      @Qualifier("deduplicationJob") Job deduplicationJob) {
    this.launcher = launcher;
    this.personReader = personReader;
    this.deduplicationJob = deduplicationJob;
  }

  @Scheduled(cron = "#{@batchJobScheduler.cronSchedule}")
  public void runJob()
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

  public String getCronSchedule() {
    return cronSchedule;
  }
}
