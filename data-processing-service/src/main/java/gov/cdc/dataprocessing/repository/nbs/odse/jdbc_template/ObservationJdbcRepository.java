package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ObservationQuery.*;

@Component
public class ObservationJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ObservationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public Observation findObservationByUid(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);
        return  jdbcTemplateOdse.queryForObject(
                SELECT_OBSERVATION_BY_UID,
                params,
                new BeanPropertyRowMapper<>(Observation.class));
    }

    public List<Observation_Question> retrieveObservationQuestion(Long targetActUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("target_act_uid", targetActUid);
        return  jdbcTemplateOdse.query(
                RETRIEVE_OBSERVATION_QUESTION_SQL,
                params,
                new BeanPropertyRowMapper<>(Observation_Question.class));
    }

    public void insertObservation(Observation observation) {
        jdbcTemplateOdse.update(INSERT_OBSERVATION, new MapSqlParameterSource()
                .addValue("observation_uid", observation.getObservationUid())
                .addValue("activity_duration_amt", observation.getActivityDurationAmt())
                .addValue("activity_duration_unit_cd", observation.getActivityDurationUnitCd())
                .addValue("activity_from_time", observation.getActivityFromTime())
                .addValue("activity_to_time", observation.getActivityToTime())
                .addValue("add_reason_cd", observation.getAddReasonCd())
                .addValue("add_time", observation.getAddTime())
                .addValue("add_user_id", observation.getAddUserId())
                .addValue("cd", observation.getCd())
                .addValue("cd_desc_txt", observation.getCdDescTxt())
                .addValue("cd_system_cd", observation.getCdSystemCd())
                .addValue("cd_system_desc_txt", observation.getCdSystemDescTxt())
                .addValue("confidentiality_cd", observation.getConfidentialityCd())
                .addValue("confidentiality_desc_txt", observation.getConfidentialityDescTxt())
                .addValue("ctrl_cd_display_form", observation.getCtrlCdDisplayForm())
                .addValue("ctrl_cd_user_defined_1", observation.getCtrlCdUserDefined1())
                .addValue("ctrl_cd_user_defined_2", observation.getCtrlCdUserDefined2())
                .addValue("ctrl_cd_user_defined_3", observation.getCtrlCdUserDefined3())
                .addValue("ctrl_cd_user_defined_4", observation.getCtrlCdUserDefined4())
                .addValue("derivation_exp", observation.getDerivationExp())
                .addValue("effective_duration_amt", observation.getEffectiveDurationAmt())
                .addValue("effective_duration_unit_cd", observation.getEffectiveDurationUnitCd())
                .addValue("effective_from_time", observation.getEffectiveFromTime())
                .addValue("effective_to_time", observation.getEffectiveToTime())
                .addValue("electronic_ind", observation.getElectronicInd())
                .addValue("group_level_cd", observation.getGroupLevelCd())
                .addValue("jurisdiction_cd", observation.getJurisdictionCd())
                .addValue("lab_condition_cd", observation.getLabConditionCd())
                .addValue("last_chg_reason_cd", observation.getLastChgReasonCd())
                .addValue("last_chg_time", observation.getLastChgTime())
                .addValue("last_chg_user_id", observation.getLastChgUserId())
                .addValue("local_id", observation.getLocalId())
                .addValue("method_cd", observation.getMethodCd())
                .addValue("method_desc_txt", observation.getMethodDescTxt())
                .addValue("obs_domain_cd", observation.getObsDomainCd())
                .addValue("obs_domain_cd_st_1", observation.getObsDomainCdSt1())
                .addValue("pnu_cd", observation.getPnuCd())
                .addValue("priority_cd", observation.getPriorityCd())
                .addValue("priority_desc_txt", observation.getPriorityDescTxt())
                .addValue("prog_area_cd", observation.getProgAreaCd())
                .addValue("record_status_cd", observation.getRecordStatusCd())
                .addValue("record_status_time", observation.getRecordStatusTime())
                .addValue("repeat_nbr", observation.getRepeatNbr())
                .addValue("status_cd", observation.getStatusCd())
                .addValue("status_time", observation.getStatusTime())
                .addValue("subject_person_uid", observation.getSubjectPersonUid())
                .addValue("target_site_cd", observation.getTargetSiteCd())
                .addValue("target_site_desc_txt", observation.getTargetSiteDescTxt())
                .addValue("txt", observation.getTxt())
                .addValue("user_affiliation_txt", observation.getUserAffiliationTxt())
                .addValue("value_cd", observation.getValueCd())
                .addValue("ynu_cd", observation.getYnuCd())
                .addValue("program_jurisdiction_oid", observation.getProgramJurisdictionOid())
                .addValue("shared_ind", observation.getSharedInd())
                .addValue("version_ctrl_nbr", observation.getVersionCtrlNbr())
                .addValue("alt_cd", observation.getAltCd())
                .addValue("alt_cd_desc_txt", observation.getAltCdDescTxt())
                .addValue("alt_cd_system_cd", observation.getAltCdSystemCd())
                .addValue("alt_cd_system_desc_txt", observation.getAltCdSystemDescTxt())
                .addValue("cd_derived_ind", observation.getCdDerivedInd())
                .addValue("rpt_to_state_time", observation.getRptToStateTime())
                .addValue("cd_version", observation.getCdVersion())
                .addValue("processing_decision_cd", observation.getProcessingDecisionCd())
                .addValue("pregnant_ind_cd", observation.getPregnantIndCd())
                .addValue("pregnant_week", observation.getPregnantWeek())
                .addValue("processing_decision_txt", observation.getProcessingDecisionTxt())
        );
    }

    public void updateObservation(Observation observation) {
        jdbcTemplateOdse.update(UPDATE_OBSERVATION, new MapSqlParameterSource()
                .addValue("observation_uid", observation.getObservationUid())
                .addValue("activity_duration_amt", observation.getActivityDurationAmt())
                .addValue("activity_duration_unit_cd", observation.getActivityDurationUnitCd())
                .addValue("activity_from_time", observation.getActivityFromTime())
                .addValue("activity_to_time", observation.getActivityToTime())
                .addValue("add_reason_cd", observation.getAddReasonCd())
                .addValue("add_time", observation.getAddTime())
                .addValue("add_user_id", observation.getAddUserId())
                .addValue("cd", observation.getCd())
                .addValue("cd_desc_txt", observation.getCdDescTxt())
                .addValue("cd_system_cd", observation.getCdSystemCd())
                .addValue("cd_system_desc_txt", observation.getCdSystemDescTxt())
                .addValue("confidentiality_cd", observation.getConfidentialityCd())
                .addValue("confidentiality_desc_txt", observation.getConfidentialityDescTxt())
                .addValue("ctrl_cd_display_form", observation.getCtrlCdDisplayForm())
                .addValue("ctrl_cd_user_defined_1", observation.getCtrlCdUserDefined1())
                .addValue("ctrl_cd_user_defined_2", observation.getCtrlCdUserDefined2())
                .addValue("ctrl_cd_user_defined_3", observation.getCtrlCdUserDefined3())
                .addValue("ctrl_cd_user_defined_4", observation.getCtrlCdUserDefined4())
                .addValue("derivation_exp", observation.getDerivationExp())
                .addValue("effective_duration_amt", observation.getEffectiveDurationAmt())
                .addValue("effective_duration_unit_cd", observation.getEffectiveDurationUnitCd())
                .addValue("effective_from_time", observation.getEffectiveFromTime())
                .addValue("effective_to_time", observation.getEffectiveToTime())
                .addValue("electronic_ind", observation.getElectronicInd())
                .addValue("group_level_cd", observation.getGroupLevelCd())
                .addValue("jurisdiction_cd", observation.getJurisdictionCd())
                .addValue("lab_condition_cd", observation.getLabConditionCd())
                .addValue("last_chg_reason_cd", observation.getLastChgReasonCd())
                .addValue("last_chg_time", observation.getLastChgTime())
                .addValue("last_chg_user_id", observation.getLastChgUserId())
                .addValue("local_id", observation.getLocalId())
                .addValue("method_cd", observation.getMethodCd())
                .addValue("method_desc_txt", observation.getMethodDescTxt())
                .addValue("obs_domain_cd", observation.getObsDomainCd())
                .addValue("obs_domain_cd_st_1", observation.getObsDomainCdSt1())
                .addValue("pnu_cd", observation.getPnuCd())
                .addValue("priority_cd", observation.getPriorityCd())
                .addValue("priority_desc_txt", observation.getPriorityDescTxt())
                .addValue("prog_area_cd", observation.getProgAreaCd())
                .addValue("record_status_cd", observation.getRecordStatusCd())
                .addValue("record_status_time", observation.getRecordStatusTime())
                .addValue("repeat_nbr", observation.getRepeatNbr())
                .addValue("status_cd", observation.getStatusCd())
                .addValue("status_time", observation.getStatusTime())
                .addValue("subject_person_uid", observation.getSubjectPersonUid())
                .addValue("target_site_cd", observation.getTargetSiteCd())
                .addValue("target_site_desc_txt", observation.getTargetSiteDescTxt())
                .addValue("txt", observation.getTxt())
                .addValue("user_affiliation_txt", observation.getUserAffiliationTxt())
                .addValue("value_cd", observation.getValueCd())
                .addValue("ynu_cd", observation.getYnuCd())
                .addValue("program_jurisdiction_oid", observation.getProgramJurisdictionOid())
                .addValue("shared_ind", observation.getSharedInd())
                .addValue("version_ctrl_nbr", observation.getVersionCtrlNbr())
                .addValue("alt_cd", observation.getAltCd())
                .addValue("alt_cd_desc_txt", observation.getAltCdDescTxt())
                .addValue("alt_cd_system_cd", observation.getAltCdSystemCd())
                .addValue("alt_cd_system_desc_txt", observation.getAltCdSystemDescTxt())
                .addValue("cd_derived_ind", observation.getCdDerivedInd())
                .addValue("rpt_to_state_time", observation.getRptToStateTime())
                .addValue("cd_version", observation.getCdVersion())
                .addValue("processing_decision_cd", observation.getProcessingDecisionCd())
                .addValue("pregnant_ind_cd", observation.getPregnantIndCd())
                .addValue("pregnant_week", observation.getPregnantWeek())
                .addValue("processing_decision_txt", observation.getProcessingDecisionTxt())
        );
    }

    public void insertObservationReason(ObservationReason reason) {
        jdbcTemplateOdse.update(INSERT_SQL_OBS_REASON, buildParamsObsReason(reason));
    }

    public void updateObservationReason(ObservationReason reason) {
        jdbcTemplateOdse.update(UPDATE_SQL_OBS_REASON, buildParamsObsReason(reason));
    }

    public void deleteObservationReason(ObservationReason reason) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", reason.getObservationUid())
                .addValue("reason_cd", reason.getReasonCd());

        jdbcTemplateOdse.update(DELETE_SQL_OBS_REASON, params);
    }

    private MapSqlParameterSource buildParamsObsReason(ObservationReason r) {
        return new MapSqlParameterSource()
                .addValue("observation_uid", r.getObservationUid())
                .addValue("reason_cd", r.getReasonCd())
                .addValue("reason_desc_txt", r.getReasonDescTxt());
    }

    public void insertObservationInterp(ObservationInterp interp) {
        jdbcTemplateOdse.update(INSERT_SQL_OBS_INTERP, buildParamsObsInterp(interp));
    }

    public void updateObservationInterp(ObservationInterp interp) {
        jdbcTemplateOdse.update(UPDATE_SQL_OBS_INTERP, buildParamsObsInterp(interp));
    }

    public void deleteObservationInterp(ObservationInterp interp) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", interp.getObservationUid())
                .addValue("interpretation_cd", interp.getInterpretationCd());

        jdbcTemplateOdse.update(DELETE_SQL_OBS_INTERP, params);
    }

    private MapSqlParameterSource buildParamsObsInterp(ObservationInterp interp) {
        return new MapSqlParameterSource()
                .addValue("observation_uid", interp.getObservationUid())
                .addValue("interpretation_cd", interp.getInterpretationCd())
                .addValue("interpretation_desc_txt", interp.getInterpretationDescTxt());
    }

    public void insertObsValueCoded(ObsValueCoded obs) {
        jdbcTemplateOdse.update(INSERT_SQL_OBS_VALUE_CODED, buildParamsObsValueCoded(obs));
    }

    public void updateObsValueCoded(ObsValueCoded obs) {
        jdbcTemplateOdse.update(UPDATE_SQL_OBS_VALUE_CODED, buildParamsObsValueCoded(obs));
    }

    public void deleteObsValueCoded(ObsValueCoded obs) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("code", obs.getCode());
        jdbcTemplateOdse.update(DELETE_SQL_OBS_VALUE_CODED, params);
    }

    private MapSqlParameterSource buildParamsObsValueCoded(ObsValueCoded obs) {
        return new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("code", obs.getCode())
                .addValue("code_system_cd", obs.getCodeSystemCd())
                .addValue("code_system_desc_txt", obs.getCodeSystemDescTxt())
                .addValue("code_version", obs.getCodeVersion())
                .addValue("display_name", obs.getDisplayName())
                .addValue("original_txt", obs.getOriginalTxt())
                .addValue("alt_cd", obs.getAltCd())
                .addValue("alt_cd_desc_txt", obs.getAltCdDescTxt())
                .addValue("alt_cd_system_cd", obs.getAltCdSystemCd())
                .addValue("alt_cd_system_desc_txt", obs.getAltCdSystemDescTxt())
                .addValue("code_derived_ind", obs.getCodeDerivedInd());
    }

    public void insertObsValueTxt(ObsValueTxt obs) {
        jdbcTemplateOdse.update(INSERT_SQL_OBS_VALUE_TXT, buildParamsObsValueTxt(obs));
    }

    public void updateObsValueTxt(ObsValueTxt obs) {
        jdbcTemplateOdse.update(UPDATE_SQL_OBS_VALUE_TXT, buildParamsObsValueTxt(obs));
    }

    public void deleteObsValueTxt(ObsValueTxt obs) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("obs_value_txt_seq", obs.getObsValueTxtSeq());
        jdbcTemplateOdse.update(DELETE_SQL_OBS_VALUE_TXT, params);
    }

    private MapSqlParameterSource buildParamsObsValueTxt(ObsValueTxt obs) {
        return new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("obs_value_txt_seq", obs.getObsValueTxtSeq())
                .addValue("data_subtype_cd", obs.getDataSubtypeCd())
                .addValue("encoding_type_cd", obs.getEncodingTypeCd())
                .addValue("txt_type_cd", obs.getTxtTypeCd())
                .addValue("value_image_txt", obs.getValueImageTxt(), Types.BINARY)
                .addValue("value_txt", obs.getValueTxt());
    }

    private byte[] convertToBytes(String text) {
        return text != null ? text.getBytes(StandardCharsets.UTF_8) : null;
    }

    public void insertObsValueDate(ObsValueDate obs) {
        jdbcTemplateOdse.update(INSERT_SQL_OBS_VALUE_DATE, buildParamsObsValueDate(obs));
    }

    public void updateObsValueDate(ObsValueDate obs) {
        jdbcTemplateOdse.update(UPDATE_SQL_OBS_VALUE_DATE, buildParamsObsValueDate(obs));
    }

    public void deleteObsValueDate(ObsValueDate obs) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("obs_value_date_seq", obs.getObsValueDateSeq());
        jdbcTemplateOdse.update(DELETE_SQL_OBS_VALUE_DATE, params);
    }

    private MapSqlParameterSource buildParamsObsValueDate(ObsValueDate obs) {
        return new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("obs_value_date_seq", obs.getObsValueDateSeq())
                .addValue("duration_amt", obs.getDurationAmt())
                .addValue("duration_unit_cd", obs.getDurationUnitCd())
                .addValue("from_time", obs.getFromTime())
                .addValue("to_time", obs.getToTime());
    }

    public void insertObsValueNumeric(ObsValueNumeric obs) {
        jdbcTemplateOdse.update(INSERT_SQL_OBS_VALUE_NUMERIC, buildParamsObsValueNumeric(obs));
    }

    public void updateObsValueNumeric(ObsValueNumeric obs) {
        jdbcTemplateOdse.update(UPDATE_SQL_OBS_VALUE_NUMERIC, buildParamsObsValueNumeric(obs));
    }

    public void deleteObsValueNumeric(ObsValueNumeric obs) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("obs_value_numeric_seq", obs.getObsValueNumericSeq());
        jdbcTemplateOdse.update(DELETE_SQL_OBS_VALUE_NUMERIC, params);
    }

    private MapSqlParameterSource buildParamsObsValueNumeric(ObsValueNumeric obs) {
        return new MapSqlParameterSource()
                .addValue("observation_uid", obs.getObservationUid())
                .addValue("obs_value_numeric_seq", obs.getObsValueNumericSeq())
                .addValue("high_range", obs.getHighRange())
                .addValue("low_range", obs.getLowRange())
                .addValue("comparator_cd_1", obs.getComparatorCd1())
                .addValue("numeric_value_1", obs.getNumericValue1())
                .addValue("numeric_value_2", obs.getNumericValue2())
                .addValue("numeric_unit_cd", obs.getNumericUnitCd())
                .addValue("separator_cd", obs.getSeparatorCd())
                .addValue("numeric_scale_1", obs.getNumericScale1())
                .addValue("numeric_scale_2", obs.getNumericScale2());
    }

    public List<ObservationReason> findByObservationReasons(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_OBS_REASONS,
                params,
                new BeanPropertyRowMapper<>(ObservationReason.class)
        );
    }

    public List<ObservationInterp> findByObservationInterp(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_OBS_INTERP_UID,
                params,
                new BeanPropertyRowMapper<>(ObservationInterp.class)
        );
    }

    public List<ObsValueCoded> findByObservationCodedUid(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_OBS_CODED_UID,
                params,
                new BeanPropertyRowMapper<>(ObsValueCoded.class)
        );
    }

    public List<ObsValueTxt> findByObservationTxtUid(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_OBS_TXT,
                params,
                new BeanPropertyRowMapper<>(ObsValueTxt.class)
        );
    }

    public List<ObsValueDate> findByObservationDateUid(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_OBS_DATE_UID,
                params,
                new BeanPropertyRowMapper<>(ObsValueDate.class)
        );
    }

    public List<ObsValueNumeric> findByObservationNumericUid(Long observationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("observation_uid", observationUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_OBS_NUMERIC_UID,
                params,
                new BeanPropertyRowMapper<>(ObsValueNumeric.class)
        );
    }
}
