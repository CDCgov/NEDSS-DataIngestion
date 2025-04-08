package gov.cdc.nbs.deduplication.duplicates;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchJobScheduler {

  private final JobLauncher jobLauncher;
  private final Job deduplicationJob;

  @Value("${batch.job.schedule.cron:0 0 1 * * ?}") // Default to daily at 1 AM
  private String cronSchedule;

  public BatchJobScheduler(
      JobLauncher jobLauncher,
      @Qualifier("deduplicationJob") Job deduplicationJob) {
    this.jobLauncher = jobLauncher;
    this.deduplicationJob = deduplicationJob;
  }

  @Scheduled(cron = "#{@batchJobScheduler.cronSchedule}")
  public void runJob()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    JobParameters params = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(deduplicationJob, params);
  }

  public String getCronSchedule() {
    return cronSchedule;
  }
}
