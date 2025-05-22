package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ActRelationshipQuery.*;

@Component
public class ActRelationshipJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ActRelationshipJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }


    public void insertActRelationship(ActRelationship act) {
        jdbcTemplateOdse.update(INSERT_SQL_ACT_RELATIONSHIP, buildParams(act)
        );
    }

    public void updateActRelationship(ActRelationship act) {
        jdbcTemplateOdse.update(UPDATE_SQL_ACT_RELATIONSHIP, buildParams(act)
        );
    }


    public void deleteActRelationship(ActRelationship act) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("source_act_uid", act.getSourceActUid())
                .addValue("target_act_uid", act.getTargetActUid())
                .addValue("type_cd", act.getTypeCd());

        jdbcTemplateOdse.update(DELETE_SQL_ACT_RELATIONSHIP, params);
    }

    private MapSqlParameterSource buildParams(ActRelationship a) {
        return new MapSqlParameterSource()
                .addValue("source_act_uid", a.getSourceActUid())
                .addValue("target_act_uid", a.getTargetActUid())
                .addValue("type_cd", a.getTypeCd())
                .addValue("add_reason_cd", a.getAddReasonCd())
                .addValue("add_time", a.getAddTime())
                .addValue("add_user_id", a.getAddUserId())
                .addValue("duration_amt", a.getDurationAmt())
                .addValue("duration_unit_cd", a.getDurationUnitCd())
                .addValue("from_time", a.getFromTime())
                .addValue("last_chg_reason_cd", a.getLastChgReasonCd())
                .addValue("last_chg_time", a.getLastChgTime())
                .addValue("last_chg_user_id", a.getLastChgUserId())
                .addValue("record_status_cd", a.getRecordStatusCd())
                .addValue("record_status_time", a.getRecordStatusTime())
                .addValue("sequence_nbr", a.getSequenceNbr())
                .addValue("source_class_cd", a.getSourceClassCd())
                .addValue("status_cd", a.getStatusCd())
                .addValue("status_time", a.getStatusTime())
                .addValue("target_class_cd", a.getTargetClassCd())
                .addValue("to_time", a.getToTime())
                .addValue("type_desc_txt", a.getTypeDescTxt())
                .addValue("user_affiliation_txt", a.getUserAffiliationTxt());
    }


    public List<ActRelationship> findBySourceActUid(Long sourceActUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("sourceActUid", sourceActUid);
        return jdbcTemplateOdse.query(
                SELECT_BY_SOURCE,
                params,
                new BeanPropertyRowMapper<>(ActRelationship.class)
        );
    }

    public List<ActRelationship> findByTargetActUid(Long targetActUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("targetActUid", targetActUid);
        return jdbcTemplateOdse.query(
                SELECT_BY_TARGET,
                params,
                new BeanPropertyRowMapper<>(ActRelationship.class)
        );
    }

    public void insertActRelationshipHistory(ActRelationshipHistory history) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("sourceActUid", history.getSourceActUid())
                .addValue("targetActUid", history.getTargetActUid())
                .addValue("typeCd", history.getTypeCd())
                .addValue("versionCrl", history.getVersionCrl())
                .addValue("addReasonCd", history.getAddReasonCd())
                .addValue("addTime", history.getAddTime())
                .addValue("addUserId", history.getAddUserId())
                .addValue("durationAmt", history.getDurationAmt())
                .addValue("durationUnitCd", history.getDurationUnitCd())
                .addValue("fromTime", history.getFromTime())
                .addValue("lastChgReasonCd", history.getLastChgReasonCd())
                .addValue("lastChgTime", history.getLastChgTime())
                .addValue("lastChgUserId", history.getLastChgUserId())
                .addValue("recordStatusCd", history.getRecordStatusCd())
                .addValue("recordStatusTime", history.getRecordStatusTime())
                .addValue("sequenceNbr", history.getSequenceNbr())
                .addValue("statusCd", history.getStatusCd())
                .addValue("statusTime", history.getStatusTime())
                .addValue("sourceClassCd", history.getSourceClassCd())
                .addValue("targetClassCd", history.getTargetClassCd())
                .addValue("toTime", history.getToTime())
                .addValue("typeDescTxt", history.getTypeDescTxt())
                .addValue("userAffiliationTxt", history.getUserAffiliationTxt());

        jdbcTemplateOdse.update(CREATE_ACT_RELATIONSHIP_HISTORY, params);
    }
}
