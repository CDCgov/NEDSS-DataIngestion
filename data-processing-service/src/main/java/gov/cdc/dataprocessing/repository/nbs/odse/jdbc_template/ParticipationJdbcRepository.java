package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.ParticipationQuery.*;

@Component
public class ParticipationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ParticipationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createParticipation(Participation participation) {
        jdbcTemplateOdse.update(INSERT_PARTICIPATION, buildParams(participation));
    }

    public void updateParticipation(Participation participation) {
        jdbcTemplateOdse.update(UPDATE_PARTICIPATION, buildParams(participation));
    }

    private MapSqlParameterSource buildParams(Participation p) {
        return new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, p.getSubjectEntityUid())
                .addValue(ACT_UID_JAVA, p.getActUid())
                .addValue("typeCode", p.getTypeCode())
                .addValue("actClassCode", p.getActClassCode())
                .addValue("addReasonCode", p.getAddReasonCode())
                .addValue(ADD_TIME_JAVA, p.getAddTime())
                .addValue(ADD_USER_ID_JAVA, p.getAddUserId())
                .addValue("awarenessCode", p.getAwarenessCode())
                .addValue("awarenessDescription", p.getAwarenessDescription())
                .addValue("code", p.getCode())
                .addValue("durationAmount", p.getDurationAmount())
                .addValue("durationUnitCode", p.getDurationUnitCode())
                .addValue("fromTime", p.getFromTime())
                .addValue("lastChangeReasonCode", p.getLastChangeReasonCode())
                .addValue("lastChangeTime", p.getLastChangeTime())
                .addValue("lastChangeUserId", p.getLastChangeUserId())
                .addValue("recordStatusCode", p.getRecordStatusCode())
                .addValue(RECORD_STATUS_TIME_JAVA, p.getRecordStatusTime())
                .addValue("roleSeq", p.getRoleSeq())
                .addValue("statusCode", p.getStatusCode())
                .addValue(STATUS_TIME_JAVA, p.getStatusTime())
                .addValue("subjectClassCode", p.getSubjectClassCode())
                .addValue("toTime", p.getToTime())
                .addValue("typeDescription", p.getTypeDescription())
                .addValue("userAffiliationText", p.getUserAffiliationText());
    }

    public void deleteParticipation(Long subjectEntityUid, Long actUid, String typeCode) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, subjectEntityUid)
                .addValue(ACT_UID_JAVA, actUid)
                .addValue("typeCode", typeCode);

        jdbcTemplateOdse.update(DELETE_PARTICIPATION, params);
    }

    public void mergeParticipationHist(ParticipationHist hist) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, hist.getSubjectEntityUid())
                .addValue(ACT_UID_JAVA, hist.getActUid())
                .addValue("typeCd", hist.getTypeCd())
                .addValue("versionCtrlNbr", hist.getVersionCtrlNbr())
                .addValue("actClassCd", hist.getActClassCd())
                .addValue(ADD_REASON_CD_JAVA, hist.getAddReasonCd())
                .addValue(ADD_TIME_JAVA, hist.getAddTime())
                .addValue(ADD_USER_ID_JAVA, hist.getAddUserId())
                .addValue("awarenessCd", hist.getAwarenessCd())
                .addValue("awarenessDescTxt", hist.getAwarenessDescTxt())
                .addValue("cd", hist.getCd())
                .addValue("durationAmt", hist.getDurationAmt())
                .addValue("durationUnitCd", hist.getDurationUnitCd())
                .addValue("fromTime", hist.getFromTime())
                .addValue(LAST_CHG_REASON_CD_JAVA, hist.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_JAVA, hist.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_JAVA, hist.getLastChgUserId())
                .addValue(RECORD_STATUS_CD_JAVA, hist.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_JAVA, hist.getRecordStatusTime())
                .addValue("roleSeq", hist.getRoleSeq())
                .addValue(STATUS_CD_JAVA, hist.getStatusCd())
                .addValue(STATUS_TIME_JAVA, hist.getStatusTime())
                .addValue("subjectClassCd", hist.getSubjectClassCd())
                .addValue("toTime", hist.getToTime())
                .addValue("typeDescTxt", hist.getTypeDescTxt())
                .addValue(USER_AFFILIATION_TXT_JAVA, hist.getUserAffiliationTxt());

        jdbcTemplateOdse.update(MERGE_PARTICIPATION_HIST, params);
    }

    public List<Participation> findByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ACT_UID_DB, actUid);

        return jdbcTemplateOdse.query(
                SELECT_PARTICIPATION_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(Participation.class)
        );
    }

    public List<Participation> findBySubjectUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, subjectEntityUid);

        return jdbcTemplateOdse.query(
                SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_LIST,
                params,
                new BeanPropertyRowMapper<>(Participation.class)
        );
    }

    public List<Participation> selectParticipationBySubjectEntityUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, subjectEntityUid);

        return jdbcTemplateOdse.query(SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID, params, new BeanPropertyRowMapper<>(Participation.class));
    }

    public List<Participation> selectParticipationBySubjectAndActUid(Long subjectEntityUid, Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, subjectEntityUid)
                .addValue(ACT_UID_JAVA, actUid);

        return jdbcTemplateOdse.query(SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_AND_ACT_UID, params, new BeanPropertyRowMapper<>(Participation.class));
    }



}
