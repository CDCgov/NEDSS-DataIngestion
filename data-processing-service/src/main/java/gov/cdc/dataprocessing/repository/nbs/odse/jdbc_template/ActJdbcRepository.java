package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.ACT_UID_DB;
import static gov.cdc.dataprocessing.constant.query.ActQuery.*;

@Component
public class ActJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ActJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") final NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertAct(final Act act) {
        jdbcTemplateOdse.update(INSERT_SQL_ACT, toSqlParams(act));
    }

    public void updateAct(final Act act) {
        jdbcTemplateOdse.update(UPDATE_SQL_ACT, toSqlParams(act));
    }

    public void mergeAct(final Act act) {
        jdbcTemplateOdse.update(MERGE_SQL_ACT, toSqlParams(act));
    }

    private MapSqlParameterSource toSqlParams(final Act act) {
        return new MapSqlParameterSource()
                .addValue(ACT_UID_DB, act.getActUid())
                .addValue("class_cd", act.getClassCode())
                .addValue("mood_cd", act.getMoodCode());
    }
}
