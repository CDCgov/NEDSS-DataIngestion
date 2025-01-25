package gov.cdc.nbs.deduplication.seed.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LastProcessedIdListener implements JobExecutionListener {

    private final NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate;

    public LastProcessedIdListener(@Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.deduplicationNamedJdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            Long lastProcessedId = jobExecution.getJobParameters().getLong("lastProcessedId");
            System.out.println("After Job - Last processed ID: " + lastProcessedId);  // Log the value

            if (lastProcessedId != null) {
                String updateSql = "UPDATE last_processed_id SET last_processed_id = :lastProcessedId where id =1";
                Map<String, Object> params = new HashMap<>();
                params.put("lastProcessedId", lastProcessedId);

                int rowsUpdated = deduplicationNamedJdbcTemplate.update(updateSql, params);
                System.out.println("Rows updated: " + rowsUpdated);  // Log number of rows affected
                System.out.println("Successfully updated last_processed_id to: " + lastProcessedId);
            } else {
                System.err.println("Job completed, but no lastProcessedId found in job parameters.");
            }
        } else {
            System.err.println("Job did not complete successfully. Status: " + jobExecution.getStatus());
        }
    }
}
