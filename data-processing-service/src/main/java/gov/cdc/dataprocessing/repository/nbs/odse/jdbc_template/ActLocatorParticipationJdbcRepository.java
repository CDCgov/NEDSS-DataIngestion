package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ActLocatorParticipationQuery.*;

@Component
public class ActLocatorParticipationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ActLocatorParticipationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    private MapSqlParameterSource buildParamsActLocatorPat(ActLocatorParticipation a) {
        return new MapSqlParameterSource()
                .addValue("entity_uid", a.getEntityUid())
                .addValue("act_uid", a.getActUid())
                .addValue("locator_uid", a.getLocatorUid())
                .addValue("add_reason_cd", a.getAddReasonCd())
                .addValue("add_time", a.getAddTime())
                .addValue("add_user_id", a.getAddUserId())
                .addValue("duration_amt", a.getDurationAmount())
                .addValue("duration_unit_cd", a.getDurationUnitCd())
                .addValue("from_time", a.getFromTime())
                .addValue("last_chg_reason_cd", a.getLastChangeReasonCd())
                .addValue("last_chg_time", a.getLastChangeTime())
                .addValue("last_chg_user_id", a.getLastChangeUserId())
                .addValue("record_status_cd", a.getRecordStatusCd())
                .addValue("record_status_time", a.getRecordStatusTime())
                .addValue("to_time", a.getToTime())
                .addValue("status_cd", a.getStatusCd())
                .addValue("status_time", a.getStatusTime())
                .addValue("type_cd", a.getTypeCd())
                .addValue("type_desc_txt", a.getTypeDescTxt())
                .addValue("user_affiliation_txt", a.getUserAffiliationTxt());
    }

    public void insertActLocatorParticipation(ActLocatorParticipation a) {
        jdbcTemplateOdse.update(INSERT_SQL_ACT_LOCATOR_PAT, buildParamsActLocatorPat(a));
    }

    public void updateActLocatorParticipation(ActLocatorParticipation a) {
        jdbcTemplateOdse.update(UPDATE_SQL_ACT_LOCATOR_PAT, buildParamsActLocatorPat(a));
    }

    public List<ActLocatorParticipation> findByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("act_uid", actUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(ActLocatorParticipation.class)
        );
    }
}
