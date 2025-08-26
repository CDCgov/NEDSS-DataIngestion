package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.config.ServicePropertiesProvider;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.ActRelationshipQuery.*;
import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Component
public class ActRelationshipJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;
    private final ServicePropertiesProvider servicePropertiesProvider;

    public ActRelationshipJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse, ServicePropertiesProvider servicePropertiesProvider) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
        this.servicePropertiesProvider = servicePropertiesProvider;
    }

    public void insertActRelationship(ActRelationship act) {
        jdbcTemplateOdse.update(INSERT_SQL_ACT_RELATIONSHIP, buildParams(act));
    }

    public void updateActRelationship(ActRelationship act) {
        jdbcTemplateOdse.update(UPDATE_SQL_ACT_RELATIONSHIP, buildParams(act));
    }

    public void mergeActRelationship(ActRelationship actRelationship) {
        jdbcTemplateOdse.update(MERGE_ACT_RELATIONSHIP, buildParams(actRelationship));
    }

    private MapSqlParameterSource buildParams(ActRelationship a) {
        if(a.getStatusTime()==null){
            a.setStatusTime(getCurrentTimeStamp(servicePropertiesProvider.getTz()));
        }
        return new MapSqlParameterSource()
                .addValue(SOURCE_ACT_UID_DB, a.getSourceActUid())
                .addValue("target_act_uid", a.getTargetActUid())
                .addValue("type_cd", a.getTypeCd())
                .addValue(ADD_REASON_CD_DB, a.getAddReasonCd())
                .addValue(ADD_TIME_DB, a.getAddTime())
                .addValue(ADD_USER_ID_DB, a.getAddUserId())
                .addValue("duration_amt", a.getDurationAmt())
                .addValue("duration_unit_cd", a.getDurationUnitCd())
                .addValue("from_time", a.getFromTime())
                .addValue(LAST_CHG_REASON_CD_DB, a.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, a.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, a.getLastChgUserId())
                .addValue(RECORD_STATUS_CD_DB, a.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, a.getRecordStatusTime())
                .addValue("sequence_nbr", a.getSequenceNbr())
                .addValue("source_class_cd", a.getSourceClassCd())
                .addValue(STATUS_CD_DB, a.getStatusCd())
                .addValue(STATUS_TIME_DB, a.getStatusTime())
                .addValue("target_class_cd", a.getTargetClassCd())
                .addValue("to_time", a.getToTime())
                .addValue("type_desc_txt", a.getTypeDescTxt())
                .addValue(USER_AFFILIATION_TXT_DB, a.getUserAffiliationTxt());
    }

    public void insertActRelationshipHistory(ActRelationshipHistory history) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SOURCE_ACT_UID_JAVA, history.getSourceActUid())
                .addValue("targetActUid", history.getTargetActUid())
                .addValue("typeCd", history.getTypeCd())
                .addValue("versionCrl", history.getVersionCrl())
                .addValue(ADD_REASON_CD_JAVA, history.getAddReasonCd())
                .addValue(ADD_TIME_JAVA, history.getAddTime())
                .addValue(ADD_USER_ID_JAVA, history.getAddUserId())
                .addValue("durationAmt", history.getDurationAmt())
                .addValue("durationUnitCd", history.getDurationUnitCd())
                .addValue("fromTime", history.getFromTime())
                .addValue(LAST_CHG_REASON_CD_JAVA, history.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_JAVA, history.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_JAVA, history.getLastChgUserId())
                .addValue(RECORD_STATUS_CD_JAVA, history.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_JAVA, history.getRecordStatusTime())
                .addValue("sequenceNbr", history.getSequenceNbr())
                .addValue(STATUS_CD_JAVA, history.getStatusCd())
                .addValue(STATUS_TIME_JAVA, history.getStatusTime())
                .addValue("sourceClassCd", history.getSourceClassCd())
                .addValue("targetClassCd", history.getTargetClassCd())
                .addValue("toTime", history.getToTime())
                .addValue("typeDescTxt", history.getTypeDescTxt())
                .addValue(USER_AFFILIATION_TXT_JAVA, history.getUserAffiliationTxt());

        jdbcTemplateOdse.update(CREATE_ACT_RELATIONSHIP_HISTORY, params);
    }

    public void deleteActRelationship(ActRelationship act) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SOURCE_ACT_UID_DB, act.getSourceActUid())
                .addValue("target_act_uid", act.getTargetActUid())
                .addValue("type_cd", act.getTypeCd());

        jdbcTemplateOdse.update(DELETE_SQL_ACT_RELATIONSHIP, params);
    }

    public List<ActRelationship> findBySourceActUid(Long sourceActUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(SOURCE_ACT_UID_JAVA, sourceActUid);
        return jdbcTemplateOdse.query(
                SELECT_BY_SOURCE,
                params,
                new BeanPropertyRowMapper<>(ActRelationship.class)
        );
    }

    public List<ActRelationship> findBySourceActUidAndTypeCode(Long sourceActUid, String typeCode) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SOURCE_ACT_UID_JAVA, sourceActUid)
                .addValue("typeCd", typeCode);
        return jdbcTemplateOdse.query(
                SELECT_BY_SOURCE_AND_TYPE_CODE,
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

}
