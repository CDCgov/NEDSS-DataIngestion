
package gov.cdc.nbs.deduplication.seed.logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoggingService {

  private static final String QUERY = """
      INSERT INTO job_logs
        (step_name, message, exception_type, exception_message,failed_ids)
      VALUES
        (:step_name, :message, :exception_type, :exception_message, :failed_ids);
      """;

  private final NamedParameterJdbcTemplate template;

  public LoggingService(@Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
    this.template = template;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void logError(String stepName, String message, String failedIds, Throwable throwable) {
    template.update(QUERY, createParameterSource(stepName, message, failedIds, throwable));
  }

  SqlParameterSource createParameterSource(String stepName, String message, String failedIds, Throwable throwable) {
    return new MapSqlParameterSource()
        .addValue("step_name", stepName)
        .addValue("message", message)
        .addValue("exception_type", throwable.getClass().getName())
        .addValue("failed_ids", failedIds)
        .addValue("exception_message", throwable.getMessage()
        );
  }

}
