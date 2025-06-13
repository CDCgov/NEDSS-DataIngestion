package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.ActLocatorParticipationQuery.*;

@Component
public class ActLocatorParticipationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ActLocatorParticipationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertActLocatorParticipation(ActLocatorParticipation a) {
        jdbcTemplateOdse.update(INSERT_SQL_ACT_LOCATOR_PAT, buildParamsActLocatorPat(a));
    }

    public void updateActLocatorParticipation(ActLocatorParticipation a) {
        jdbcTemplateOdse.update(UPDATE_SQL_ACT_LOCATOR_PAT, buildParamsActLocatorPat(a));
    }

    public void mergeActLocatorParticipation(ActLocatorParticipation a) {
        jdbcTemplateOdse.update(MERGE_ACT_LOCATOR, buildParamsActLocatorPat(a));
    }

    private MapSqlParameterSource buildParamsActLocatorPat(ActLocatorParticipation a) {
        return new MapSqlParameterSource()
                .addValue(ENTITY_UID_DB, a.getEntityUid())
                .addValue(ACT_UID_DB, a.getActUid())
                .addValue("locator_uid", a.getLocatorUid())
                .addValue(ADD_REASON_CD_DB, a.getAddReasonCd())
                .addValue(ADD_TIME_DB, a.getAddTime())
                .addValue(ADD_USER_ID_DB, a.getAddUserId())
                .addValue("duration_amt", a.getDurationAmount())
                .addValue("duration_unit_cd", a.getDurationUnitCd())
                .addValue("from_time", a.getFromTime())
                .addValue(LAST_CHG_REASON_CD_DB, a.getLastChangeReasonCd())
                .addValue(LAST_CHG_TIME_DB, a.getLastChangeTime())
                .addValue(LAST_CHG_USER_ID_DB, a.getLastChangeUserId())
                .addValue(RECORD_STATUS_CD_DB, a.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, a.getRecordStatusTime())
                .addValue("to_time", a.getToTime())
                .addValue(STATUS_CD_DB, a.getStatusCd())
                .addValue(STATUS_TIME_DB, a.getStatusTime())
                .addValue("type_cd", a.getTypeCd())
                .addValue("type_desc_txt", a.getTypeDescTxt())
                .addValue(USER_AFFILIATION_TXT_DB, a.getUserAffiliationTxt());
    }

    public List<ActLocatorParticipation> findByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ACT_UID_DB, actUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(ActLocatorParticipation.class)
        );
    }

}
