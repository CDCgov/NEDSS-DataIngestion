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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/deduplication/seed")
public class SeedController {

  private final JobLauncher launcher;
  private final Job seedJob;
  private final NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate;

  // Constructor Injection for NamedParameterJdbcTemplate
  public SeedController(
          final JobLauncher launcher,
          @Qualifier("seedJob") final Job seedJob,
          NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate) {
    this.launcher = launcher;
    this.seedJob = seedJob;
    this.deduplicationNamedJdbcTemplate = deduplicationNamedJdbcTemplate;
  }

  @PostMapping
  public void startSeed() throws JobExecutionAlreadyRunningException,
          JobRestartException,
          JobInstanceAlreadyCompleteException,
          JobParametersInvalidException {

    // Query seed_status table to get last_processed_id
    String sql = "SELECT last_processed_id FROM seed_status WHERE job_name = 'seed-job'";
    Long lastProcessedId = deduplicationNamedJdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);

    // If lastProcessedId is null (first time seeding), use a default value to start from the smallest ID
    if (lastProcessedId == null) {
      lastProcessedId = -1L; // or another value to indicate first-time seeding
    }

    // Add lastProcessedId to job parameters
    JobParameters parameters = new JobParametersBuilder()
            .addLong("startTime", System.currentTimeMillis())
            .addLong("lastProcessedId", lastProcessedId)
            .toJobParameters();

    // Run the seed job with the parameters
    launcher.run(seedJob, parameters);
  }
}
