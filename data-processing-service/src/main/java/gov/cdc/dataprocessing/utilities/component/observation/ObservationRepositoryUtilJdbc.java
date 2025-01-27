package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ObservationRepositoryUtilJdbc {
    private final JdbcTemplate jdbcTemplateOdse;

    public ObservationRepositoryUtilJdbc(@Qualifier("odseJdbcTemplate") JdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    @Transactional
    public void saveObservationAndAct(Act act, Observation observation, boolean insert) {
        if (insert)
        {
            insertAct(act);
            insertObservation(observation);
        }
        else
        {
            updateAct(act);
            updateObservation(observation);
        }
    }


    @Transactional
    public void updateObservation(Observation observation) {
        String sql = "UPDATE Observation SET " +
                "activity_duration_amt = ?, activity_duration_unit_cd = ?, activity_from_time = ?, activity_to_time = ?, " +
                "add_reason_cd = ?, add_time = ?, add_user_id = ?, cd = ?, cd_desc_txt = ?, cd_system_cd = ?, cd_system_desc_txt = ?, " +
                "confidentiality_cd = ?, confidentiality_desc_txt = ?, ctrl_cd_display_form = ?, ctrl_cd_user_defined_1 = ?, " +
                "ctrl_cd_user_defined_2 = ?, ctrl_cd_user_defined_3 = ?, ctrl_cd_user_defined_4 = ?, derivation_exp = ?, " +
                "effective_duration_amt = ?, effective_duration_unit_cd = ?, effective_from_time = ?, effective_to_time = ?, " +
                "electronic_ind = ?, group_level_cd = ?, jurisdiction_cd = ?, lab_condition_cd = ?, last_chg_reason_cd = ?, " +
                "last_chg_time = ?, last_chg_user_id = ?, local_id = ?, method_cd = ?, method_desc_txt = ?, obs_domain_cd = ?, " +
                "obs_domain_cd_st_1 = ?, pnu_cd = ?, priority_cd = ?, priority_desc_txt = ?, prog_area_cd = ?, record_status_cd = ?, " +
                "record_status_time = ?, repeat_nbr = ?, status_cd = ?, status_time = ?, subject_person_uid = ?, target_site_cd = ?, " +
                "target_site_desc_txt = ?, txt = ?, user_affiliation_txt = ?, value_cd = ?, ynu_cd = ?, program_jurisdiction_oid = ?, " +
                "shared_ind = ?, version_ctrl_nbr = ?, alt_cd = ?, alt_cd_desc_txt = ?, alt_cd_system_cd = ?, alt_cd_system_desc_txt = ?, " +
                "cd_derived_ind = ?, rpt_to_state_time = ?, cd_version = ?, processing_decision_cd = ?, pregnant_ind_cd = ?, " +
                "pregnant_week = ?, processing_decision_txt = ? WHERE observation_uid = ?";

        jdbcTemplateOdse.update(sql,
                observation.getActivityDurationAmt(),
                observation.getActivityDurationUnitCd(),
                observation.getActivityFromTime(),
                observation.getActivityToTime(),
                observation.getAddReasonCd(),
                observation.getAddTime(),
                observation.getAddUserId(),
                observation.getCd(),
                observation.getCdDescTxt(),
                observation.getCdSystemCd(),
                observation.getCdSystemDescTxt(),
                observation.getConfidentialityCd(),
                observation.getConfidentialityDescTxt(),
                observation.getCtrlCdDisplayForm(),
                observation.getCtrlCdUserDefined1(),
                observation.getCtrlCdUserDefined2(),
                observation.getCtrlCdUserDefined3(),
                observation.getCtrlCdUserDefined4(),
                observation.getDerivationExp(),
                observation.getEffectiveDurationAmt(),
                observation.getEffectiveDurationUnitCd(),
                observation.getEffectiveFromTime(),
                observation.getEffectiveToTime(),
                observation.getElectronicInd(),
                observation.getGroupLevelCd(),
                observation.getJurisdictionCd(),
                observation.getLabConditionCd(),
                observation.getLastChgReasonCd(),
                observation.getLastChgTime(),
                observation.getLastChgUserId(),
                observation.getLocalId(),
                observation.getMethodCd(),
                observation.getMethodDescTxt(),
                observation.getObsDomainCd(),
                observation.getObsDomainCdSt1(),
                observation.getPnuCd(),
                observation.getPriorityCd(),
                observation.getPriorityDescTxt(),
                observation.getProgAreaCd(),
                observation.getRecordStatusCd(),
                observation.getRecordStatusTime(),
                observation.getRepeatNbr(),
                observation.getStatusCd(),
                observation.getStatusTime(),
                observation.getSubjectPersonUid(),
                observation.getTargetSiteCd(),
                observation.getTargetSiteDescTxt(),
                observation.getTxt(),
                observation.getUserAffiliationTxt(),
                observation.getValueCd(),
                observation.getYnuCd(),
                observation.getProgramJurisdictionOid(),
                observation.getSharedInd(),
                observation.getVersionCtrlNbr(),
                observation.getAltCd(),
                observation.getAltCdDescTxt(),
                observation.getAltCdSystemCd(),
                observation.getAltCdSystemDescTxt(),
                observation.getCdDerivedInd(),
                observation.getRptToStateTime(),
                observation.getCdVersion(),
                observation.getProcessingDecisionCd(),
                observation.getPregnantIndCd(),
                observation.getPregnantWeek(),
                observation.getProcessingDecisionTxt(),
                observation.getObservationUid());
    }

    private void insertObservation(Observation observation) {
        String sql = "INSERT INTO Observation (observation_uid, activity_duration_amt, activity_duration_unit_cd, activity_from_time, activity_to_time, add_reason_cd, add_time, add_user_id, cd, cd_desc_txt, cd_system_cd, cd_system_desc_txt, confidentiality_cd, confidentiality_desc_txt, ctrl_cd_display_form, ctrl_cd_user_defined_1, ctrl_cd_user_defined_2, ctrl_cd_user_defined_3, ctrl_cd_user_defined_4, derivation_exp, effective_duration_amt, effective_duration_unit_cd, effective_from_time, effective_to_time, electronic_ind, group_level_cd, jurisdiction_cd, lab_condition_cd, last_chg_reason_cd, last_chg_time, last_chg_user_id, local_id, method_cd, method_desc_txt, obs_domain_cd, obs_domain_cd_st_1, pnu_cd, priority_cd, priority_desc_txt, prog_area_cd, record_status_cd, record_status_time, repeat_nbr, status_cd, status_time, subject_person_uid, target_site_cd, target_site_desc_txt, txt, user_affiliation_txt, value_cd, ynu_cd, program_jurisdiction_oid, shared_ind, version_ctrl_nbr, alt_cd, alt_cd_desc_txt, alt_cd_system_cd, alt_cd_system_desc_txt, cd_derived_ind, rpt_to_state_time, cd_version, processing_decision_cd, pregnant_ind_cd, pregnant_week, processing_decision_txt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplateOdse.update(sql,
                observation.getObservationUid(),
                observation.getActivityDurationAmt(),
                observation.getActivityDurationUnitCd(),
                observation.getActivityFromTime(),
                observation.getActivityToTime(),
                observation.getAddReasonCd(),
                observation.getAddTime(),
                observation.getAddUserId(),
                observation.getCd(),
                observation.getCdDescTxt(),
                observation.getCdSystemCd(),
                observation.getCdSystemDescTxt(),
                observation.getConfidentialityCd(),
                observation.getConfidentialityDescTxt(),
                observation.getCtrlCdDisplayForm(),
                observation.getCtrlCdUserDefined1(),
                observation.getCtrlCdUserDefined2(),
                observation.getCtrlCdUserDefined3(),
                observation.getCtrlCdUserDefined4(),
                observation.getDerivationExp(),
                observation.getEffectiveDurationAmt(),
                observation.getEffectiveDurationUnitCd(),
                observation.getEffectiveFromTime(),
                observation.getEffectiveToTime(),
                observation.getElectronicInd(),
                observation.getGroupLevelCd(),
                observation.getJurisdictionCd(),
                observation.getLabConditionCd(),
                observation.getLastChgReasonCd(),
                observation.getLastChgTime(),
                observation.getLastChgUserId(),
                observation.getLocalId(),
                observation.getMethodCd(),
                observation.getMethodDescTxt(),
                observation.getObsDomainCd(),
                observation.getObsDomainCdSt1(),
                observation.getPnuCd(),
                observation.getPriorityCd(),
                observation.getPriorityDescTxt(),
                observation.getProgAreaCd(),
                observation.getRecordStatusCd(),
                observation.getRecordStatusTime(),
                observation.getRepeatNbr(),
                observation.getStatusCd(),
                observation.getStatusTime(),
                observation.getSubjectPersonUid(),
                observation.getTargetSiteCd(),
                observation.getTargetSiteDescTxt(),
                observation.getTxt(),
                observation.getUserAffiliationTxt(),
                observation.getValueCd(),
                observation.getYnuCd(),
                observation.getProgramJurisdictionOid(),
                observation.getSharedInd(),
                observation.getVersionCtrlNbr(),
                observation.getAltCd(),
                observation.getAltCdDescTxt(),
                observation.getAltCdSystemCd(),
                observation.getAltCdSystemDescTxt(),
                observation.getCdDerivedInd(),
                observation.getRptToStateTime(),
                observation.getCdVersion(),
                observation.getProcessingDecisionCd(),
                observation.getPregnantIndCd(),
                observation.getPregnantWeek(),
                observation.getProcessingDecisionTxt());
    }

    private void insertAct(Act act) {
        String sql = "INSERT INTO Act (act_uid, class_cd, mood_cd) VALUES (?, ?, ?)";
        jdbcTemplateOdse.update(sql, act.getActUid(), act.getClassCode(), act.getMoodCode());
    }

    private void updateAct(Act act) {
        String sql = "UPDATE Act SET class_cd = ?, mood_cd = ? WHERE act_uid = ?";
        jdbcTemplateOdse.update(sql, act.getClassCode(), act.getMoodCode(), act.getActUid());
    }

    @Transactional
    public void saveObservationReason(ObservationReason observationReason, boolean insert) {
        if (insert) {
            insertObservationReason(observationReason);
        }
        else {
            updateObservationReason(observationReason);
        }
    }

    private void insertObservationReason(ObservationReason observationReason) {
        String sql = "INSERT INTO Observation_reason (observation_uid, reason_cd, reason_desc_txt) VALUES (?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                observationReason.getObservationUid(),
                observationReason.getReasonCd(),
                observationReason.getReasonDescTxt());
    }

    private void updateObservationReason(ObservationReason observationReason) {
        String sql = "UPDATE Observation_reason SET reason_desc_txt = ? WHERE observation_uid = ? AND reason_cd = ?";
        jdbcTemplateOdse.update(sql,
                observationReason.getReasonDescTxt(),
                observationReason.getObservationUid(),
                observationReason.getReasonCd());
    }

    @Transactional
    public void saveActId(ActId actId, boolean update) {
        if (update) {
            updateActId(actId);
        }
        else {
            insertActId(actId);
        }
    }


    private void insertActId(ActId actId) {
        String sql = "INSERT INTO Act_id (act_uid, act_id_seq, add_reason_cd, add_time, add_user_id, assigning_authority_cd, assigning_authority_desc_txt, " +
                "duration_amt, duration_unit_cd, last_chg_reason_cd, last_chg_time, last_chg_user_id, record_status_cd, record_status_time, root_extension_txt, " +
                "status_cd, status_time, type_cd, type_desc_txt, user_affiliation_txt, valid_from_time, valid_to_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                actId.getActUid(),
                actId.getActIdSeq(),
                actId.getAddReasonCd(),
                actId.getAddTime(),
                actId.getAddUserId(),
                actId.getAssigningAuthorityCd(),
                actId.getAssigningAuthorityDescTxt(),
                actId.getDurationAmt(),
                actId.getDurationUnitCd(),
                actId.getLastChgReasonCd(),
                actId.getLastChgTime(),
                actId.getLastChgUserId(),
                actId.getRecordStatusCd(),
                actId.getRecordStatusTime(),
                actId.getRootExtensionTxt(),
                actId.getStatusCd(),
                actId.getStatusTime(),
                actId.getTypeCd(),
                actId.getTypeDescTxt(),
                actId.getUserAffiliationTxt(),
                actId.getValidFromTime(),
                actId.getValidToTime());
    }

    private void updateActId(ActId actId) {
        String sql = "UPDATE Act_id SET add_reason_cd = ?, add_time = ?, add_user_id = ?, assigning_authority_cd = ?, assigning_authority_desc_txt = ?, " +
                "duration_amt = ?, duration_unit_cd = ?, last_chg_reason_cd = ?, last_chg_time = ?, last_chg_user_id = ?, record_status_cd = ?, " +
                "record_status_time = ?, root_extension_txt = ?, status_cd = ?, status_time = ?, type_cd = ?, type_desc_txt = ?, " +
                "user_affiliation_txt = ?, valid_from_time = ?, valid_to_time = ? WHERE act_uid = ? AND act_id_seq = ?";
        jdbcTemplateOdse.update(sql,
                actId.getAddReasonCd(),
                actId.getAddTime(),
                actId.getAddUserId(),
                actId.getAssigningAuthorityCd(),
                actId.getAssigningAuthorityDescTxt(),
                actId.getDurationAmt(),
                actId.getDurationUnitCd(),
                actId.getLastChgReasonCd(),
                actId.getLastChgTime(),
                actId.getLastChgUserId(),
                actId.getRecordStatusCd(),
                actId.getRecordStatusTime(),
                actId.getRootExtensionTxt(),
                actId.getStatusCd(),
                actId.getStatusTime(),
                actId.getTypeCd(),
                actId.getTypeDescTxt(),
                actId.getUserAffiliationTxt(),
                actId.getValidFromTime(),
                actId.getValidToTime(),
                actId.getActUid(),
                actId.getActIdSeq());
    }


    @Transactional
    public void saveObservationInterp(ObservationInterp observationInterp, boolean insert) {
        if (insert) {
            insertObservationInterp(observationInterp);
        } else {
            updateObservationInterp(observationInterp);
        }
    }

    private void insertObservationInterp(ObservationInterp observationInterp) {
        String sql = "INSERT INTO Observation_interp (observation_uid, interpretation_cd, interpretation_desc_txt) VALUES (?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                observationInterp.getObservationUid(),
                observationInterp.getInterpretationCd(),
                observationInterp.getInterpretationDescTxt());
    }

    private void updateObservationInterp(ObservationInterp observationInterp) {
        String sql = "UPDATE Observation_interp SET interpretation_desc_txt = ? WHERE observation_uid = ? AND interpretation_cd = ?";
        jdbcTemplateOdse.update(sql,
                observationInterp.getInterpretationDescTxt(),
                observationInterp.getObservationUid(),
                observationInterp.getInterpretationCd());
    }


    @Transactional
    public void saveObsValueCoded(ObsValueCoded obsValueCoded, boolean insert) {
        if (insert) {
            insertObsValueCoded(obsValueCoded);
        } else {
            updateObsValueCoded(obsValueCoded);
        }
    }

    private void insertObsValueCoded(ObsValueCoded obsValueCoded) {
        String sql = "INSERT INTO Obs_value_coded (observation_uid, code, code_system_cd, code_system_desc_txt, code_version, display_name, original_txt, " +
                "alt_cd, alt_cd_desc_txt, alt_cd_system_cd, alt_cd_system_desc_txt, code_derived_ind) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                obsValueCoded.getObservationUid(),
                obsValueCoded.getCode(),
                obsValueCoded.getCodeSystemCd(),
                obsValueCoded.getCodeSystemDescTxt(),
                obsValueCoded.getCodeVersion(),
                obsValueCoded.getDisplayName(),
                obsValueCoded.getOriginalTxt(),
                obsValueCoded.getAltCd(),
                obsValueCoded.getAltCdDescTxt(),
                obsValueCoded.getAltCdSystemCd(),
                obsValueCoded.getAltCdSystemDescTxt(),
                obsValueCoded.getCodeDerivedInd());
    }

    private void updateObsValueCoded(ObsValueCoded obsValueCoded) {
        String sql = "UPDATE Obs_value_coded SET code_system_cd = ?, code_system_desc_txt = ?, code_version = ?, display_name = ?, original_txt = ?, " +
                "alt_cd = ?, alt_cd_desc_txt = ?, alt_cd_system_cd = ?, alt_cd_system_desc_txt = ?, code_derived_ind = ? " +
                "WHERE observation_uid = ? AND code = ?";
        jdbcTemplateOdse.update(sql,
                obsValueCoded.getCodeSystemCd(),
                obsValueCoded.getCodeSystemDescTxt(),
                obsValueCoded.getCodeVersion(),
                obsValueCoded.getDisplayName(),
                obsValueCoded.getOriginalTxt(),
                obsValueCoded.getAltCd(),
                obsValueCoded.getAltCdDescTxt(),
                obsValueCoded.getAltCdSystemCd(),
                obsValueCoded.getAltCdSystemDescTxt(),
                obsValueCoded.getCodeDerivedInd(),
                obsValueCoded.getObservationUid(),
                obsValueCoded.getCode());
    }


    @Transactional
    public void saveObsValueTxt(ObsValueTxt obsValueTxt, boolean insert) {
        if (insert) {
            insertObsValueTxt(obsValueTxt);
        } else {
            updateObsValueTxt(obsValueTxt);
        }
    }

    private void insertObsValueTxt(ObsValueTxt obsValueTxt) {
        String sql = "INSERT INTO Obs_value_txt (observation_uid, obs_value_txt_seq, data_subtype_cd, encoding_type_cd, txt_type_cd, value_txt) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                obsValueTxt.getObservationUid(),
                obsValueTxt.getObsValueTxtSeq(),
                obsValueTxt.getDataSubtypeCd(),
                obsValueTxt.getEncodingTypeCd(),
                obsValueTxt.getTxtTypeCd(),
                obsValueTxt.getValueTxt());
    }

    private void updateObsValueTxt(ObsValueTxt obsValueTxt) {
        String sql = "UPDATE Obs_value_txt SET data_subtype_cd = ?, encoding_type_cd = ?, txt_type_cd = ?, value_txt = ? " +
                "WHERE observation_uid = ? AND obs_value_txt_seq = ?";
        jdbcTemplateOdse.update(sql,
                obsValueTxt.getDataSubtypeCd(),
                obsValueTxt.getEncodingTypeCd(),
                obsValueTxt.getTxtTypeCd(),
                obsValueTxt.getValueTxt(),
                obsValueTxt.getObservationUid(),
                obsValueTxt.getObsValueTxtSeq());
    }


    @Transactional
    public void saveObsValueDate(ObsValueDate obsValueDate, boolean insert) {
        if (insert) {
            insertObsValueDate(obsValueDate);
        } else {
            updateObsValueDate(obsValueDate);
        }
    }

    private void insertObsValueDate(ObsValueDate obsValueDate) {
        String sql = "INSERT INTO Obs_value_date (observation_uid, obs_value_date_seq, duration_amt, duration_unit_cd, from_time, to_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                obsValueDate.getObservationUid(),
                obsValueDate.getObsValueDateSeq(),
                obsValueDate.getDurationAmt(),
                obsValueDate.getDurationUnitCd(),
                obsValueDate.getFromTime(),
                obsValueDate.getToTime());
    }

    private void updateObsValueDate(ObsValueDate obsValueDate) {
        String sql = "UPDATE Obs_value_date SET duration_amt = ?, duration_unit_cd = ?, from_time = ?, to_time = ? " +
                "WHERE observation_uid = ? AND obs_value_date_seq = ?";
        jdbcTemplateOdse.update(sql,
                obsValueDate.getDurationAmt(),
                obsValueDate.getDurationUnitCd(),
                obsValueDate.getFromTime(),
                obsValueDate.getToTime(),
                obsValueDate.getObservationUid(),
                obsValueDate.getObsValueDateSeq());
    }


    @Transactional
    public void saveObsValueNumeric(ObsValueNumeric obsValueNumeric, boolean insert) {
        if (insert) {
            insertObsValueNumeric(obsValueNumeric);
        } else {
            updateObsValueNumeric(obsValueNumeric);
        }
    }

    private void insertObsValueNumeric(ObsValueNumeric obsValueNumeric) {
        String sql = "INSERT INTO Obs_value_numeric (observation_uid, obs_value_numeric_seq, high_range, low_range, comparator_cd_1, numeric_value_1, numeric_value_2, " +
                "numeric_unit_cd, separator_cd, numeric_scale_1, numeric_scale_2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplateOdse.update(sql,
                obsValueNumeric.getObservationUid(),
                obsValueNumeric.getObsValueNumericSeq(),
                obsValueNumeric.getHighRange(),
                obsValueNumeric.getLowRange(),
                obsValueNumeric.getComparatorCd1(),
                obsValueNumeric.getNumericValue1(),
                obsValueNumeric.getNumericValue2(),
                obsValueNumeric.getNumericUnitCd(),
                obsValueNumeric.getSeparatorCd(),
                obsValueNumeric.getNumericScale1(),
                obsValueNumeric.getNumericScale2());
    }

    private void updateObsValueNumeric(ObsValueNumeric obsValueNumeric) {
        String sql = "UPDATE Obs_value_numeric SET high_range = ?, low_range = ?, comparator_cd_1 = ?, numeric_value_1 = ?, numeric_value_2 = ?, " +
                "numeric_unit_cd = ?, separator_cd = ?, numeric_scale_1 = ?, numeric_scale_2 = ? WHERE observation_uid = ? AND obs_value_numeric_seq = ?";
        jdbcTemplateOdse.update(sql,
                obsValueNumeric.getHighRange(),
                obsValueNumeric.getLowRange(),
                obsValueNumeric.getComparatorCd1(),
                obsValueNumeric.getNumericValue1(),
                obsValueNumeric.getNumericValue2(),
                obsValueNumeric.getNumericUnitCd(),
                obsValueNumeric.getSeparatorCd(),
                obsValueNumeric.getNumericScale1(),
                obsValueNumeric.getNumericScale2(),
                obsValueNumeric.getObservationUid(),
                obsValueNumeric.getObsValueNumericSeq());
    }


    public Observation findObservationByUid(Long observationUid) {
        String sql = "SELECT * FROM Observation WHERE observation_uid = ?";
        return jdbcTemplateOdse.queryForObject(sql, new Object[]{observationUid}, (rs, rowNum) -> {
            Observation observation = new Observation();
            observation.setObservationUid(rs.getLong("observation_uid"));
            observation.setActivityDurationAmt(rs.getString("activity_duration_amt"));
            observation.setActivityDurationUnitCd(rs.getString("activity_duration_unit_cd"));
            observation.setActivityFromTime(rs.getTimestamp("activity_from_time"));
            observation.setActivityToTime(rs.getTimestamp("activity_to_time"));
            observation.setAddReasonCd(rs.getString("add_reason_cd"));
            observation.setAddTime(rs.getTimestamp("add_time"));
            observation.setAddUserId(rs.getLong("add_user_id"));
            observation.setCd(rs.getString("cd"));
            observation.setCdDescTxt(rs.getString("cd_desc_txt"));
            observation.setCdSystemCd(rs.getString("cd_system_cd"));
            observation.setCdSystemDescTxt(rs.getString("cd_system_desc_txt"));
            observation.setConfidentialityCd(rs.getString("confidentiality_cd"));
            observation.setConfidentialityDescTxt(rs.getString("confidentiality_desc_txt"));
            observation.setCtrlCdDisplayForm(rs.getString("ctrl_cd_display_form"));
            observation.setCtrlCdUserDefined1(rs.getString("ctrl_cd_user_defined_1"));
            observation.setCtrlCdUserDefined2(rs.getString("ctrl_cd_user_defined_2"));
            observation.setCtrlCdUserDefined3(rs.getString("ctrl_cd_user_defined_3"));
            observation.setCtrlCdUserDefined4(rs.getString("ctrl_cd_user_defined_4"));
            observation.setDerivationExp(rs.getInt("derivation_exp"));
            observation.setEffectiveDurationAmt(rs.getString("effective_duration_amt"));
            observation.setEffectiveDurationUnitCd(rs.getString("effective_duration_unit_cd"));
            observation.setEffectiveFromTime(rs.getTimestamp("effective_from_time"));
            observation.setEffectiveToTime(rs.getTimestamp("effective_to_time"));
            observation.setElectronicInd(rs.getString("electronic_ind"));
            observation.setGroupLevelCd(rs.getString("group_level_cd"));
            observation.setJurisdictionCd(rs.getString("jurisdiction_cd"));
            observation.setLabConditionCd(rs.getString("lab_condition_cd"));
            observation.setLastChgReasonCd(rs.getString("last_chg_reason_cd"));
            observation.setLastChgTime(rs.getTimestamp("last_chg_time"));
            observation.setLastChgUserId(rs.getLong("last_chg_user_id"));
            observation.setLocalId(rs.getString("local_id"));
            observation.setMethodCd(rs.getString("method_cd"));
            observation.setMethodDescTxt(rs.getString("method_desc_txt"));
            observation.setObsDomainCd(rs.getString("obs_domain_cd"));
            observation.setObsDomainCdSt1(rs.getString("obs_domain_cd_st_1"));
            observation.setPnuCd(rs.getString("pnu_cd"));
            observation.setPriorityCd(rs.getString("priority_cd"));
            observation.setPriorityDescTxt(rs.getString("priority_desc_txt"));
            observation.setProgAreaCd(rs.getString("prog_area_cd"));
            observation.setRecordStatusCd(rs.getString("record_status_cd"));
            observation.setRecordStatusTime(rs.getTimestamp("record_status_time"));
            observation.setRepeatNbr(rs.getInt("repeat_nbr"));
            observation.setStatusCd(rs.getString("status_cd"));
            observation.setStatusTime(rs.getTimestamp("status_time"));
            observation.setSubjectPersonUid(rs.getLong("subject_person_uid"));
            observation.setTargetSiteCd(rs.getString("target_site_cd"));
            observation.setTargetSiteDescTxt(rs.getString("target_site_desc_txt"));
            observation.setTxt(rs.getString("txt"));
            observation.setUserAffiliationTxt(rs.getString("user_affiliation_txt"));
            observation.setValueCd(rs.getString("value_cd"));
            observation.setYnuCd(rs.getString("ynu_cd"));
            observation.setProgramJurisdictionOid(rs.getLong("program_jurisdiction_oid"));
            observation.setSharedInd(rs.getString("shared_ind"));
            observation.setVersionCtrlNbr(rs.getInt("version_ctrl_nbr"));
            observation.setAltCd(rs.getString("alt_cd"));
            observation.setAltCdDescTxt(rs.getString("alt_cd_desc_txt"));
            observation.setAltCdSystemCd(rs.getString("alt_cd_system_cd"));
            observation.setAltCdSystemDescTxt(rs.getString("alt_cd_system_desc_txt"));
            observation.setCdDerivedInd(rs.getString("cd_derived_ind"));
            observation.setRptToStateTime(rs.getTimestamp("rpt_to_state_time"));
            observation.setCdVersion(rs.getString("cd_version"));
            observation.setProcessingDecisionCd(rs.getString("processing_decision_cd"));
            observation.setPregnantIndCd(rs.getString("pregnant_ind_cd"));
            observation.setPregnantWeek(rs.getInt("pregnant_week"));
            observation.setProcessingDecisionTxt(rs.getString("processing_decision_txt"));
            return observation;
        });
    }


}
