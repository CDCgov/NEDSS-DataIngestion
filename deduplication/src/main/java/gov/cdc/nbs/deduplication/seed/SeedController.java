package gov.cdc.nbs.deduplication.seed;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final NamedParameterJdbcTemplate nbsNamedJdbcTemplate;

  // Constructor Injection for NamedParameterJdbcTemplate
  public SeedController(
          final JobLauncher launcher,
          @Qualifier("seedJob") final Job seedJob,
          @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate,
          @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsNamedJdbcTemplate) {
    this.launcher = launcher;
    this.seedJob = seedJob;
    this.deduplicationNamedJdbcTemplate = deduplicationNamedJdbcTemplate;
    this.nbsNamedJdbcTemplate = nbsNamedJdbcTemplate;
  }

  @PostMapping
  public void startSeed() throws Exception {
    // After the first run, this should retrieve the last processed ID from the previous clustering
    String sql = "SELECT last_processed_id FROM last_processed_id WHERE id = 1";
    Long lastProcessedId = null;

    try {
      lastProcessedId = deduplicationNamedJdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
      System.out.println("Last processed ID from DB: " + lastProcessedId);
    } catch (Exception e) {
      System.out.println("No record found in last_processed_id table.");
    }

    if (lastProcessedId == null) {
      String smallestIdSql = "SELECT MIN(person_uid) FROM person";
      try {
        lastProcessedId = nbsNamedJdbcTemplate.queryForObject(smallestIdSql, new HashMap<>(), Long.class);
        if (lastProcessedId == null) {
          throw new IllegalStateException("No records found in the NBS database.");
        }
      } catch (Exception e) {
        System.err.println("Error fetching smallest person ID: " + e.getMessage());
        throw new IllegalStateException("Could not retrieve the smallest person ID from the nbs.person table.", e);
      }
    }

    // Log job parameters before starting
    System.out.println("Starting job with lastProcessedId: " + lastProcessedId);

    JobParameters parameters = new JobParametersBuilder()
            .addLong("startTime", System.currentTimeMillis())
            .addLong("lastProcessedId", lastProcessedId)
            .toJobParameters();

    launcher.run(seedJob, parameters);
  }

}
