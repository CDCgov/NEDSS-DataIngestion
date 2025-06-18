package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.NbsInterfaceQuery.SELECT_NBS_INTERFACE_BY_UID;

@Component
public class NbsInterfaceJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsInterfaceJdbcRepository(@Qualifier("msgouteNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public NbsInterfaceModel getNbsInterfaceByUid(Integer uid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nbsInterfaceUid", uid);

        return jdbcTemplateOdse.queryForObject(
                SELECT_NBS_INTERFACE_BY_UID,
                params,
                new BeanPropertyRowMapper<>(NbsInterfaceModel.class)
        );
    }

    public int updateRecordStatusToRtiProcess(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        final int CHUNK_SIZE = 500;
        int totalUpdated = 0;

        for (int i = 0; i < ids.size(); i += CHUNK_SIZE) {
            List<Integer> chunk = ids.subList(i, Math.min(i + CHUNK_SIZE, ids.size()));
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("status", "RTI_PENDING")
                    .addValue("ids", chunk);

            totalUpdated += jdbcTemplateOdse.update(
                    "UPDATE NBS_interface SET record_status_cd = :status WHERE nbs_interface_uid IN (:ids)",
                    params
            );
        }

        return totalUpdated;
    }



}
