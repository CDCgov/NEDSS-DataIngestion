package gov.cdc.nbs.deduplication.seed;

import gov.cdc.nbs.deduplication.seed.step.DeduplicationWriter;
import gov.cdc.nbs.deduplication.seed.step.PersonReader;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/deduplication/seed")
public class SeedController {

  private final JobLauncher launcher;
  private final Job seedJob;
  private final DeduplicationWriter deduplicationWriter;
  private final PersonReader personReader;

  public SeedController(
          final JobLauncher launcher,
          @Qualifier("seedJob") final Job seedJob,
          DeduplicationWriter deduplicationWriter,
          PersonReader personReader) {
    this.launcher = launcher;
    this.seedJob = seedJob;
    this.deduplicationWriter = deduplicationWriter;
    this.personReader = personReader;
  }

  @PostMapping
  public void startSeed() throws JobExecutionAlreadyRunningException,
          JobRestartException,
          JobInstanceAlreadyCompleteException,
          JobParametersInvalidException {

    Long lastProcessedId = deduplicationWriter.getLastProcessedId();

    if (lastProcessedId == null) {
      lastProcessedId = personReader.getSmallestPersonId();
    }

    lastProcessedId = Optional.ofNullable(lastProcessedId).orElse(0L);

    // Pass the lastProcessedId to the job parameters
    JobParameters parameters = new JobParametersBuilder()
            .addLong("startTime", System.currentTimeMillis())
            .addLong("lastProcessedId", lastProcessedId)
            .toJobParameters();

    launcher.run(seedJob, parameters);

    // After seeding, update the lastProcessedId to the largest person_id processed
    Long largestProcessedId = deduplicationWriter.getLastProcessedId();
    if (largestProcessedId != null) {
      deduplicationWriter.updateLastProcessedId(largestProcessedId);
    }
  }
}
