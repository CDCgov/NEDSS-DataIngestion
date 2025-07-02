package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.RtiDlt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RtiDltJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RtiDltJdbcRepository(@Qualifier("msgouteNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected RtiDlt mapRow(ResultSet rs, int rowNum) throws SQLException {
        RtiDlt rtiDlt = new RtiDlt();
        rtiDlt.setId(rs.getString("id"));
        rtiDlt.setNbsInterfaceId(rs.getLong("nbs_interface_id"));
        rtiDlt.setStatus(rs.getString("status"));
        rtiDlt.setStackTrace(rs.getString("stack_trace"));
        rtiDlt.setPayload(rs.getString("payload"));
        return rtiDlt;
    }

    public RtiDlt findById(String id) {
        try {
            String sql = "SELECT * FROM dbo.rti_dlt WHERE id = :id";
            return jdbcTemplate.queryForObject(sql, Map.of("id", id), this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RtiDlt> findByNbsInterfaceId(Long nbsInterfaceId) {
        try {
            String sql = "SELECT * FROM dbo.rti_dlt WHERE nbs_interface_id = :id ORDER BY created_on DESC";
            return jdbcTemplate.query(sql, Map.of("id", nbsInterfaceId), this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<RtiDlt> findByUnSuccessStatus() {
        try {
            String sql = "SELECT * FROM rti_dlt WHERE status != 'SUCCESS' ORDER BY created_on DESC";
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void upsert(RtiDlt rtiDlt) {
        String sql = """
            MERGE rti_dlt AS target
            USING (SELECT :id AS id) AS source
            ON (target.id = source.id)
            WHEN MATCHED THEN 
                UPDATE SET
                    nbs_interface_id = :nbsInterfaceId,
                    status = :status,
                    stack_trace = :stackTrace,
                    payload = :payload,
                    updated_on = :updatedOn
            WHEN NOT MATCHED THEN
                INSERT (id, nbs_interface_id, status, stack_trace, payload, created_on, updated_on)
                VALUES (:id, :nbsInterfaceId, :status, :stackTrace, :payload, :createdOn, :updatedOn);
        """;

        var params = new MapSqlParameterSource()
                .addValue("id", rtiDlt.getId())
                .addValue("nbsInterfaceId", rtiDlt.getNbsInterfaceId())
                .addValue("status", rtiDlt.getStatus())
                .addValue("stackTrace", rtiDlt.getStackTrace())
                .addValue("payload", rtiDlt.getPayload())
                .addValue("createdOn", rtiDlt.getCreatedOn())
                .addValue("updatedOn", rtiDlt.getUpdatedOn());

        jdbcTemplate.update(sql, params);
    }
}
