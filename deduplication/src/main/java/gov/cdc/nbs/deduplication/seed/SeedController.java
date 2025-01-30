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
  private final NamedParameterJdbcTemplate nbsNamedJdbcTemplate;

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
  public void startSeed() throws JobExecutionAlreadyRunningException,
          JobRestartException,
          JobInstanceAlreadyCompleteException,
          JobParametersInvalidException {
    Long lastProcessedId = getLastProcessedId();
    if (lastProcessedId == null) {
      lastProcessedId = getSmallestPersonId();
      System.out.println("Using smallest person ID: " + lastProcessedId);
    }

    // Pass the lastProcessedId to the job parameters
    JobParameters parameters = new JobParametersBuilder()
            .addLong("startTime", System.currentTimeMillis())
            .addLong("lastProcessedId", lastProcessedId)
            .toJobParameters();
    System.out.println("Job Parameters: " + parameters.getParameters());

    launcher.run(seedJob, parameters);

    // After seeding, update the lastProcessedId to the largest person_id processed
    Long largestProcessedId = getLargestProcessedId();
    if (largestProcessedId != null) {
      updateLastProcessedId(largestProcessedId);
    }
  }

  // Method to get the last processed ID from the database
  private Long getLastProcessedId() {
    String sql = "SELECT last_processed_id FROM last_processed_id WHERE id = 1";
    try {
      return deduplicationNamedJdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
    } catch (Exception e) {
      return null; // No record found, means first run
    }
  }

  // Method to get the smallest person_id from the NBS database
  private Long getSmallestPersonId() {
    String smallestIdSql = "SELECT MIN(person_uid) FROM person";
    try {
      return nbsNamedJdbcTemplate.queryForObject(smallestIdSql, new HashMap<>(), Long.class);
    } catch (Exception e) {
      throw new IllegalStateException("Could not retrieve the smallest person ID from the nbs.person table.", e);
    }
  }

  // Method to get the largest processed person_id after the seeding job
  private Long getLargestProcessedId() {
    String largestIdSql = "SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId";
    HashMap<String, Object> params = new HashMap<>();
    params.put("lastProcessedId", getLastProcessedId());
    try {
      return nbsNamedJdbcTemplate.queryForObject(largestIdSql, params, Long.class);
    } catch (Exception e) {
      return null; // Handle the case where the largest ID could not be fetched
    }
  }

  // Method to update the last processed ID in the database
  private void updateLastProcessedId(Long largestProcessedId) {
    String updateSql = "UPDATE last_processed_id SET last_processed_id = :largestProcessedId WHERE id = 1";
    HashMap<String, Object> params = new HashMap<>();
    params.put("largestProcessedId", largestProcessedId);

    try {
      int rowsUpdated = deduplicationNamedJdbcTemplate.update(updateSql, params);
      if (rowsUpdated == 0) {
        throw new IllegalStateException("No rows were updated. Ensure the 'last_processed_id' table is initialized with an entry for id = 1.");
      }
    } catch (Exception e) {
      throw new IllegalStateException("Failed to update the last processed ID in the deduplication database.", e);
    }
  }
}
