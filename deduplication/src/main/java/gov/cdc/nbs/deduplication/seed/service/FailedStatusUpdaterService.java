package gov.cdc.nbs.deduplication.seed.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FailedStatusUpdaterService {

    private final NamedParameterJdbcTemplate template;

    public FailedStatusUpdaterService(@Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public void updateFailedStatus(List<Long> failedPersonIds) {
        if (failedPersonIds == null || failedPersonIds.isEmpty()) {
            return; // No failed IDs to update
        }

        String query = """
            UPDATE nbs_mpi_mapping
            SET status = 'F'
            WHERE person_uid IN (:failedPersonIds);
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("failedPersonIds", failedPersonIds);

        try {
            int updatedCount = template.update(query, params);
            System.out.println("Updated status to 'F' for " + updatedCount + " records.");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update failed statuses", ex);
        }
    }
}
