package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PublicHealthCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.PublicHealthCaseQuery.*;

@Component
public class PublicHealthCaseJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public PublicHealthCaseJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public PublicHealthCase findById(Long publicHealthCaseUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(PUBLIC_HEALTH_CASE_UID_JAVA, publicHealthCaseUid);

        return jdbcTemplateOdse.queryForObject(
                SELECT_PHC_BY_UID,
                params,
                new BeanPropertyRowMapper<>(PublicHealthCase.class)
        );
    }

    public void insertPublicHealthCase(PublicHealthCase phc) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PUBLIC_HEALTH_CASE_UID_JAVA, phc.getPublicHealthCaseUid());
        params.addValue("activityDurationAmt", phc.getActivityDurationAmt());
        params.addValue("activityDurationUnitCd", phc.getActivityDurationUnitCd());
        params.addValue("activityFromTime", phc.getActivityFromTime());
        params.addValue("activityToTime", phc.getActivityToTime());
        params.addValue(ADD_REASON_CD_JAVA, phc.getAddReasonCd());
        params.addValue(ADD_TIME_JAVA, phc.getAddTime());
        params.addValue(ADD_USER_ID_JAVA, phc.getAddUserId());
        params.addValue("caseClassCd", phc.getCaseClassCd());
        params.addValue("caseTypeCd", phc.getCaseTypeCd());
        params.addValue("cd", phc.getCd());
        params.addValue("cdDescTxt", phc.getCdDescTxt());
        params.addValue("cdSystemCd", phc.getCdSystemCd());
        params.addValue("cdSystemDescTxt", phc.getCdSystemDescTxt());
        params.addValue("confidentialityCd", phc.getConfidentialityCd());
        params.addValue("confidentialityDescTxt", phc.getConfidentialityDescTxt());
        params.addValue("detectionMethodCd", phc.getDetectionMethodCd());
        params.addValue("detectionMethodDescTxt", phc.getDetectionMethodDescTxt());
        params.addValue("diagnosisTime", phc.getDiagnosisTime());
        params.addValue("diseaseImportedCd", phc.getDiseaseImportedCd());
        params.addValue("diseaseImportedDescTxt", phc.getDiseaseImportedDescTxt());
        params.addValue("effectiveDurationAmt", phc.getEffectiveDurationAmt());
        params.addValue("effectiveDurationUnitCd", phc.getEffectiveDurationUnitCd());
        params.addValue("effectiveFromTime", phc.getEffectiveFromTime());
        params.addValue("effectiveToTime", phc.getEffectiveToTime());
        params.addValue("groupCaseCnt", phc.getGroupCaseCnt());
        params.addValue("investigationStatusCd", phc.getInvestigationStatusCd());
        params.addValue("jurisdictionCd", phc.getJurisdictionCd());
        params.addValue(LAST_CHG_REASON_CD_JAVA, phc.getLastChgReasonCd());
        params.addValue(LAST_CHG_TIME_JAVA, phc.getLastChgTime());
        params.addValue(LAST_CHG_USER_ID_JAVA, phc.getLastChgUserId());
        params.addValue("localId", phc.getLocalId());
        params.addValue("mmwrWeek", phc.getMmwrWeek());
        params.addValue("mmwrYear", phc.getMmwrYear());
        params.addValue("outbreakInd", phc.getOutbreakInd());
        params.addValue("outbreakFromTime", phc.getOutbreakFromTime());
        params.addValue("outbreakToTime", phc.getOutbreakToTime());
        params.addValue("outbreakName", phc.getOutbreakName());
        params.addValue("outcomeCd", phc.getOutcomeCd());
        params.addValue("patAgeAtOnset", phc.getPatAgeAtOnset());
        params.addValue("patAgeAtOnsetUnitCd", phc.getPatAgeAtOnsetUnitCd());
        params.addValue("patientGroupId", phc.getPatientGroupId());
        params.addValue("progAreaCd", phc.getProgAreaCd());
        params.addValue(RECORD_STATUS_CD_JAVA, phc.getRecordStatusCd());
        params.addValue(RECORD_STATUS_TIME_JAVA, phc.getRecordStatusTime());
        params.addValue("repeatNbr", phc.getRepeatNbr());
        params.addValue("rptCntyCd", phc.getRptCntyCd());
        params.addValue("rptFormCmpltTime", phc.getRptFormCmpltTime());
        params.addValue("rptSourceCd", phc.getRptSourceCd());
        params.addValue("rptSourceCdDescTxt", phc.getRptSourceCdDescTxt());
        params.addValue("rptToCountyTime", phc.getRptToCountyTime());
        params.addValue("rptToStateTime", phc.getRptToStateTime());
        params.addValue(STATUS_CD_JAVA, phc.getStatusCd());
        params.addValue(STATUS_TIME_JAVA, phc.getStatusTime());
        params.addValue("transmissionModeCd", phc.getTransmissionModeCd());
        params.addValue("transmissionModeDescTxt", phc.getTransmissionModeDescTxt());
        params.addValue("txt", phc.getTxt());
        params.addValue(USER_AFFILIATION_TXT_JAVA, phc.getUserAffiliationTxt());
        params.addValue("programJurisdictionOid", phc.getProgramJurisdictionOid());
        params.addValue("sharedInd", phc.getSharedInd());
        params.addValue("versionCtrlNbr", phc.getVersionCtrlNbr());
        params.addValue("investigatorAssignedTime", phc.getInvestigatorAssignedTime());
        params.addValue("hospitalizedIndCd", phc.getHospitalizedIndCd());
        params.addValue("hospitalizedAdminTime", phc.getHospitalizedAdminTime());
        params.addValue("hospitalizedDischargeTime", phc.getHospitalizedDischargeTime());
        params.addValue("hospitalizedDurationAmt", phc.getHospitalizedDurationAmt());
        params.addValue("pregnantIndCd", phc.getPregnantIndCd());
        params.addValue("dayCareIndCd", phc.getDayCareIndCd());
        params.addValue("foodHandlerIndCd", phc.getFoodHandlerIndCd());
        params.addValue("importedCountryCd", phc.getImportedCountryCd());
        params.addValue("importedStateCd", phc.getImportedStateCd());
        params.addValue("importedCityDescTxt", phc.getImportedCityDescTxt());
        params.addValue("importedCountyCd", phc.getImportedCountyCd());
        params.addValue("deceasedTime", phc.getDeceasedTime());
        params.addValue("countIntervalCd", phc.getCountIntervalCd());
        params.addValue("priorityCd", phc.getPriorityCd());
        params.addValue("contactInvTxt", phc.getContactInvTxt());
        params.addValue("infectiousFromDate", phc.getInfectiousFromDate());
        params.addValue("infectiousToDate", phc.getInfectiousToDate());
        params.addValue("contactInvStatusCd", phc.getContactInvStatusCd());
        params.addValue("referralBasisCd", phc.getReferralBasisCd());
        params.addValue("currProcessStateCd", phc.getCurrProcessStateCd());
        params.addValue("invPriorityCd", phc.getInvPriorityCd());
        params.addValue("coinfectionId", phc.getCoinfectionId());


        jdbcTemplateOdse.update(INSERT_PHC, params);
    }

    public int updatePublicHealthCase(PublicHealthCase phc) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PUBLIC_HEALTH_CASE_UID_JAVA, phc.getPublicHealthCaseUid());
        params.addValue("activityDurationAmt", phc.getActivityDurationAmt());
        params.addValue("activityDurationUnitCd", phc.getActivityDurationUnitCd());
        params.addValue("activityFromTime", phc.getActivityFromTime());
        params.addValue("activityToTime", phc.getActivityToTime());
        params.addValue(ADD_REASON_CD_JAVA, phc.getAddReasonCd());
        params.addValue(ADD_TIME_JAVA, phc.getAddTime());
        params.addValue(ADD_USER_ID_JAVA, phc.getAddUserId());
        params.addValue("caseClassCd", phc.getCaseClassCd());
        params.addValue("caseTypeCd", phc.getCaseTypeCd());
        params.addValue("cd", phc.getCd());
        params.addValue("cdDescTxt", phc.getCdDescTxt());
        params.addValue("cdSystemCd", phc.getCdSystemCd());
        params.addValue("cdSystemDescTxt", phc.getCdSystemDescTxt());
        params.addValue("confidentialityCd", phc.getConfidentialityCd());
        params.addValue("confidentialityDescTxt", phc.getConfidentialityDescTxt());
        params.addValue("detectionMethodCd", phc.getDetectionMethodCd());
        params.addValue("detectionMethodDescTxt", phc.getDetectionMethodDescTxt());
        params.addValue("diagnosisTime", phc.getDiagnosisTime());
        params.addValue("diseaseImportedCd", phc.getDiseaseImportedCd());
        params.addValue("diseaseImportedDescTxt", phc.getDiseaseImportedDescTxt());
        params.addValue("effectiveDurationAmt", phc.getEffectiveDurationAmt());
        params.addValue("effectiveDurationUnitCd", phc.getEffectiveDurationUnitCd());
        params.addValue("effectiveFromTime", phc.getEffectiveFromTime());
        params.addValue("effectiveToTime", phc.getEffectiveToTime());
        params.addValue("groupCaseCnt", phc.getGroupCaseCnt());
        params.addValue("investigationStatusCd", phc.getInvestigationStatusCd());
        params.addValue("jurisdictionCd", phc.getJurisdictionCd());
        params.addValue(LAST_CHG_REASON_CD_JAVA, phc.getLastChgReasonCd());
        params.addValue(LAST_CHG_TIME_JAVA, phc.getLastChgTime());
        params.addValue(LAST_CHG_USER_ID_JAVA, phc.getLastChgUserId());
        params.addValue("localId", phc.getLocalId());
        params.addValue("mmwrWeek", phc.getMmwrWeek());
        params.addValue("mmwrYear", phc.getMmwrYear());
        params.addValue("outbreakInd", phc.getOutbreakInd());
        params.addValue("outbreakFromTime", phc.getOutbreakFromTime());
        params.addValue("outbreakToTime", phc.getOutbreakToTime());
        params.addValue("outbreakName", phc.getOutbreakName());
        params.addValue("outcomeCd", phc.getOutcomeCd());
        params.addValue("patAgeAtOnset", phc.getPatAgeAtOnset());
        params.addValue("patAgeAtOnsetUnitCd", phc.getPatAgeAtOnsetUnitCd());
        params.addValue("patientGroupId", phc.getPatientGroupId());
        params.addValue("progAreaCd", phc.getProgAreaCd());
        params.addValue(RECORD_STATUS_CD_JAVA, phc.getRecordStatusCd());
        params.addValue(RECORD_STATUS_TIME_JAVA, phc.getRecordStatusTime());
        params.addValue("repeatNbr", phc.getRepeatNbr());
        params.addValue("rptCntyCd", phc.getRptCntyCd());
        params.addValue("rptFormCmpltTime", phc.getRptFormCmpltTime());
        params.addValue("rptSourceCd", phc.getRptSourceCd());
        params.addValue("rptSourceCdDescTxt", phc.getRptSourceCdDescTxt());
        params.addValue("rptToCountyTime", phc.getRptToCountyTime());
        params.addValue("rptToStateTime", phc.getRptToStateTime());
        params.addValue(STATUS_CD_JAVA, phc.getStatusCd());
        params.addValue(STATUS_TIME_JAVA, phc.getStatusTime());
        params.addValue("transmissionModeCd", phc.getTransmissionModeCd());
        params.addValue("transmissionModeDescTxt", phc.getTransmissionModeDescTxt());
        params.addValue("txt", phc.getTxt());
        params.addValue(USER_AFFILIATION_TXT_JAVA, phc.getUserAffiliationTxt());
        params.addValue("programJurisdictionOid", phc.getProgramJurisdictionOid());
        params.addValue("sharedInd", phc.getSharedInd());
        params.addValue("versionCtrlNbr", phc.getVersionCtrlNbr());
        params.addValue("investigatorAssignedTime", phc.getInvestigatorAssignedTime());
        params.addValue("hospitalizedIndCd", phc.getHospitalizedIndCd());
        params.addValue("hospitalizedAdminTime", phc.getHospitalizedAdminTime());
        params.addValue("hospitalizedDischargeTime", phc.getHospitalizedDischargeTime());
        params.addValue("hospitalizedDurationAmt", phc.getHospitalizedDurationAmt());
        params.addValue("pregnantIndCd", phc.getPregnantIndCd());
        params.addValue("dayCareIndCd", phc.getDayCareIndCd());
        params.addValue("foodHandlerIndCd", phc.getFoodHandlerIndCd());
        params.addValue("importedCountryCd", phc.getImportedCountryCd());
        params.addValue("importedStateCd", phc.getImportedStateCd());
        params.addValue("importedCityDescTxt", phc.getImportedCityDescTxt());
        params.addValue("importedCountyCd", phc.getImportedCountyCd());
        params.addValue("deceasedTime", phc.getDeceasedTime());
        params.addValue("countIntervalCd", phc.getCountIntervalCd());
        params.addValue("priorityCd", phc.getPriorityCd());
        params.addValue("contactInvTxt", phc.getContactInvTxt());
        params.addValue("infectiousFromDate", phc.getInfectiousFromDate());
        params.addValue("infectiousToDate", phc.getInfectiousToDate());
        params.addValue("contactInvStatusCd", phc.getContactInvStatusCd());
        params.addValue("referralBasisCd", phc.getReferralBasisCd());
        params.addValue("currProcessStateCd", phc.getCurrProcessStateCd());
        params.addValue("invPriorityCd", phc.getInvPriorityCd());
        params.addValue("coinfectionId", phc.getCoinfectionId());

        return jdbcTemplateOdse.update(UPDATE_PHC, params);
    }




}
