package gov.cdc.nbs.deduplication.seed.step;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

import java.util.List;

@Component
public class FailedRecordsReader implements ItemReader<DeduplicationEntry> {

    private static final String QUERY = """
        SELECT person_uid, person_parent_uid, mpi_patient, mpi_person, status
        FROM nbs_mpi_mapping
        WHERE status = 'F';
    """;

    private final JdbcTemplate jdbcTemplate;

    public FailedRecordsReader(@Qualifier("deduplicationTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DeduplicationEntry read() {
        List<DeduplicationEntry> failedEntries = jdbcTemplate.query(QUERY,
                (rs, rowNum) -> new DeduplicationEntry(
                        rs.getLong("person_uid"), // Convert to Long
                        rs.getLong("person_parent_uid"),
                        rs.getString("mpi_patient"),
                        rs.getString("mpi_person"),
                        rs.getString("status")
                ));
        return failedEntries.isEmpty() ? null : failedEntries.remove(0);
    }
}

