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
    private static final Logger LOGGER = LoggerFactory.getLogger(LastProcessedIdListener.class);

    public LastProcessedIdListener(@Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.deduplicationNamedJdbcTemplate = jdbcTemplate;
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

}
