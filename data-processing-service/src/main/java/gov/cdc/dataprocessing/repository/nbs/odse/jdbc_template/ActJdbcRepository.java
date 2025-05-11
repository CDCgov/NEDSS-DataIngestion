package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.ActQuery.*;

@Component
public class ActJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ActJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertAct(Act act) {
        jdbcTemplateOdse.update(INSERT_SQL_ACT, new MapSqlParameterSource()
                .addValue("act_uid", act.getActUid())
                .addValue("class_cd", act.getClassCode())
                .addValue("mood_cd", act.getMoodCode())
        );
    }

    public void updateAct(Act act) {
        jdbcTemplateOdse.update(UPDATE_SQL_ACT, new MapSqlParameterSource()
                .addValue("act_uid", act.getActUid())
                .addValue("class_cd", act.getClassCode())
                .addValue("mood_cd", act.getMoodCode())
        );
    }

    public void mergeAct(Act act) {
        jdbcTemplateOdse.update(MERGE_SQL_ACT, new MapSqlParameterSource()
                .addValue("act_uid", act.getActUid())
                .addValue("class_cd", act.getClassCode())
                .addValue("mood_cd", act.getMoodCode())
        );
    }


}
