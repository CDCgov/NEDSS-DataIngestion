package gov.cdc.nbs.deduplication.seed.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class LastProcessedIdListener implements JobExecutionListener {

    private final NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate;
    private final NamedParameterJdbcTemplate nbsNamedJdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(LastProcessedIdListener.class);

    public LastProcessedIdListener(
            @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsNamedJdbcTemplate) {
        this.deduplicationNamedJdbcTemplate = jdbcTemplate;
        this.nbsNamedJdbcTemplate = nbsNamedJdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // This method is intentionally left empty.
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            Long lastProcessedId = jobExecution.getJobParameters().getLong("lastProcessedId");
            LOGGER.info("Received lastProcessedId: {}", lastProcessedId);

            if (lastProcessedId != null) {
                String updateSql = "UPDATE last_processed_id SET last_processed_id = :lastProcessedId WHERE id = 1";
                Map<String, Object> params = new HashMap<>();
                params.put("lastProcessedId", lastProcessedId);

                LOGGER.info("Executing SQL: {} with params: {}", updateSql, params);
                int rowsUpdated = deduplicationNamedJdbcTemplate.update(updateSql, params);
                LOGGER.info("Rows updated: {}", rowsUpdated);
            } else {
                LOGGER.warn("No lastProcessedId found in job parameters.");
            }
        } else {
            LOGGER.error("Job did not complete successfully. Status: {}", jobExecution.getStatus());
        }
    }

    // Method to get the last processed ID from the database
    public Long getLastProcessedId() {
        String sql = "SELECT last_processed_id FROM last_processed_id WHERE id = 1";
        try {
            return deduplicationNamedJdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
        } catch (Exception e) {
            return null; // No record found, means first run
        }
    }

    // Method to get the smallest person_id from the NBS database
    public Long getSmallestPersonId() {
        String smallestIdSql = "SELECT MIN(person_uid) FROM person";
        try {
            return nbsNamedJdbcTemplate.queryForObject(smallestIdSql, new HashMap<>(), Long.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not retrieve the smallest person ID from the nbs.person table.", e);
        }
    }

    // Method to get the largest processed person_id after the seeding job
    public Long getLargestProcessedId() {
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
    public void updateLastProcessedId(Long largestProcessedId) {
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
