package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

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
                .addValue("subjectEntityUid", p.getSubjectEntityUid())
                .addValue("actUid", p.getActUid())
                .addValue("typeCode", p.getTypeCode())
                .addValue("actClassCode", p.getActClassCode())
                .addValue("addReasonCode", p.getAddReasonCode())
                .addValue("addTime", p.getAddTime())
                .addValue("addUserId", p.getAddUserId())
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
                .addValue("recordStatusTime", p.getRecordStatusTime())
                .addValue("roleSeq", p.getRoleSeq())
                .addValue("statusCode", p.getStatusCode())
                .addValue("statusTime", p.getStatusTime())
                .addValue("subjectClassCode", p.getSubjectClassCode())
                .addValue("toTime", p.getToTime())
                .addValue("typeDescription", p.getTypeDescription())
                .addValue("userAffiliationText", p.getUserAffiliationText());
    }

    public void deleteParticipation(Long subjectEntityUid, Long actUid, String typeCode) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", subjectEntityUid)
                .addValue("actUid", actUid)
                .addValue("typeCode", typeCode);

        jdbcTemplateOdse.update(DELETE_PARTICIPATION, params);
    }

    public void mergeParticipationHist(ParticipationHist hist) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", hist.getSubjectEntityUid())
                .addValue("actUid", hist.getActUid())
                .addValue("typeCd", hist.getTypeCd())
                .addValue("versionCtrlNbr", hist.getVersionCtrlNbr())
                .addValue("actClassCd", hist.getActClassCd())
                .addValue("addReasonCd", hist.getAddReasonCd())
                .addValue("addTime", hist.getAddTime())
                .addValue("addUserId", hist.getAddUserId())
                .addValue("awarenessCd", hist.getAwarenessCd())
                .addValue("awarenessDescTxt", hist.getAwarenessDescTxt())
                .addValue("cd", hist.getCd())
                .addValue("durationAmt", hist.getDurationAmt())
                .addValue("durationUnitCd", hist.getDurationUnitCd())
                .addValue("fromTime", hist.getFromTime())
                .addValue("lastChgReasonCd", hist.getLastChgReasonCd())
                .addValue("lastChgTime", hist.getLastChgTime())
                .addValue("lastChgUserId", hist.getLastChgUserId())
                .addValue("recordStatusCd", hist.getRecordStatusCd())
                .addValue("recordStatusTime", hist.getRecordStatusTime())
                .addValue("roleSeq", hist.getRoleSeq())
                .addValue("statusCd", hist.getStatusCd())
                .addValue("statusTime", hist.getStatusTime())
                .addValue("subjectClassCd", hist.getSubjectClassCd())
                .addValue("toTime", hist.getToTime())
                .addValue("typeDescTxt", hist.getTypeDescTxt())
                .addValue("userAffiliationTxt", hist.getUserAffiliationTxt());

        jdbcTemplateOdse.update(MERGE_PARTICIPATION_HIST, params);
    }

    public List<Participation> findByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("act_uid", actUid);

        return jdbcTemplateOdse.query(
                SELECT_PARTICIPATION_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(Participation.class)
        );
    }

    public List<Participation> findBySubjectUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", subjectEntityUid);

        return jdbcTemplateOdse.query(
                SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_LIST,
                params,
                new BeanPropertyRowMapper<>(Participation.class)
        );
    }

    public List<Participation> selectParticipationBySubjectEntityUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", subjectEntityUid);

        return jdbcTemplateOdse.query(SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID, params, new BeanPropertyRowMapper<>(Participation.class));
    }

    public List<Participation> selectParticipationBySubjectAndActUid(Long subjectEntityUid, Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", subjectEntityUid)
                .addValue("actUid", actUid);

        return jdbcTemplateOdse.query(SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_AND_ACT_UID, params, new BeanPropertyRowMapper<>(Participation.class));
    }



}
