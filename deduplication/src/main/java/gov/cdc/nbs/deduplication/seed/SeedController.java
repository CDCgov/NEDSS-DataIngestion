package gov.cdc.nbs.deduplication.seed;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deduplication/seed")
public class SeedController {

  private final JobLauncher launcher;
  private final Job seedJob;

  public SeedController(
      final JobLauncher launcher,
      @Qualifier("seedJob") final Job seedJob) {
    this.launcher = launcher;
    this.seedJob = seedJob;
  }

  @PostMapping
  public void startSeed() throws JobExecutionAlreadyRunningException,
      JobRestartException,
      JobInstanceAlreadyCompleteException,
      JobParametersInvalidException {
    JobParameters parameters = new JobParametersBuilder()
        .addLong("startTime", System.currentTimeMillis())
        .toJobParameters();
    launcher.run(seedJob, parameters);
  }

}
