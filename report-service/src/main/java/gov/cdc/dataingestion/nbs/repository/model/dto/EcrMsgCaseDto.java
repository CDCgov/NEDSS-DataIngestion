package gov.cdc.dataingestion.nbs.repository.model.dto;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.share.helper.EcrXmlModelingHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EcrMsgCaseDto {
    private String invLocalId;
    private String patLocalId;
    private String invAuthorId;
    private String invCaseStatusCd;
    private String invCloseDt;
    private String invCommentTxt;
    private String invConditionCd;
    private String invContactInvCommentTxt;
    private String invContactInvPriorityCd;
    private String invContactInvStatusCd;
    private String invCurrProcessStateCd;
    private String invDaycareIndCd;
    private String invDetectionMethodCd;
    private String invDiagnosisDt;
    private String invDiseaseAcquiredLocCd;
    private Timestamp invEffectiveTime;
    private String invFoodhandlerIndCd;
    private String invHospitalizedAdmitDt;
    private String invHospitalizedDischargeDt;
    private String invHospitalizedIndCd;
    private Integer invHospStayDuration;
    private String invIllnessStartDt;
    private String invIllnessEndDt;
    private Integer invIllnessDuration;
    private String invIllnessDurationUnitCd;
    private Integer invIllnessOnsetAge;
    private String invIllnessOnsetAgeUnitCd;
    private String invInvestigatorAssignedDt;
    private String invImportCityTxt;
    private String invImportCountyCd;
    private String invImportCountryCd;
    private String invImportStateCd;
    private String invInfectiousFromDt;
    private String invInfectiousToDt;
    private String invLegacyCaseId;
    private String invMmwrWeekTxt;
    private String invMmwrYearTxt;
    private String invOutbreakIndCd;
    private String invOutbreakNameCd;
    private String invPatientDeathDt;
    private String invPatientDeathIndCd;
    private String invPregnancyIndCd;
    private String invReferralBasisCd;
    private String invReportDt;
    private String invReportToCountyDt;
    private String invReportToStateDt;
    private String invReportingCountyCd;
    private String invSharedIndCd;
    private String invSourceTypeCd;
    private String invStartDt;
    private String invStateId;
    private String invStatusCd;
    private String invTransmissionModeCd;
    private Map<String, Object> dataMap;
    public EcrMsgCaseDto() {
        // empty constructor
    }

    public void initDataMap() throws EcrCdaXmlException {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgCaseDto.class.getDeclaredFields();
        EcrXmlModelingHelper helper = new EcrXmlModelingHelper();
        dataMap = helper.setupDataMap(fields, dataMap, this);
    }


}
