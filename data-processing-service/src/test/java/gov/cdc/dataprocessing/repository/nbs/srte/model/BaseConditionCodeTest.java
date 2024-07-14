package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class BaseConditionCodeTest {

    @Test
    void testGettersAndSetters() {
        BaseConditionCode baseConditionCode = new BaseConditionCode();

        // Set values
        baseConditionCode.setConditionCd("ConditionCd");
        baseConditionCode.setConditionCodesetNm("ConditionCodesetNm");
        baseConditionCode.setConditionSeqNum(1);
        baseConditionCode.setAssigningAuthorityCd("AssigningAuthorityCd");
        baseConditionCode.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        baseConditionCode.setCodeSystemCd("CodeSystemCd");
        baseConditionCode.setCodeSystemDescTxt("CodeSystemDescTxt");
        baseConditionCode.setConditionDescTxt("ConditionDescTxt");
        baseConditionCode.setConditionShortNm("ConditionShortNm");
        baseConditionCode.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode.setIndentLevelNbr(2);
        baseConditionCode.setInvestigationFormCd("InvestigationFormCd");
        baseConditionCode.setIsModifiableInd("IsModifiableInd");
        baseConditionCode.setNbsUid(3L);
        baseConditionCode.setNndInd("NndInd");
        baseConditionCode.setParentIsCd("ParentIsCd");
        baseConditionCode.setProgAreaCd("ProgAreaCd");
        baseConditionCode.setReportableMorbidityInd("ReportableMorbidityInd");
        baseConditionCode.setReportableSummaryInd("ReportableSummaryInd");
        baseConditionCode.setStatusCd("StatusCd");
        baseConditionCode.setStatusTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode.setNndEntityIdentifier("NndEntityIdentifier");
        baseConditionCode.setNndSummaryEntityIdentifier("NndSummaryEntityIdentifier");
        baseConditionCode.setSummaryInvestigationFormCd("SummaryInvestigationFormCd");
        baseConditionCode.setContactTracingEnableInd("ContactTracingEnableInd");
        baseConditionCode.setVaccineEnableInd("VaccineEnableInd");
        baseConditionCode.setTreatmentEnableInd("TreatmentEnableInd");
        baseConditionCode.setLabReportEnableInd("LabReportEnableInd");
        baseConditionCode.setMorbReportEnableInd("MorbReportEnableInd");
        baseConditionCode.setPortReqIndCd("PortReqIndCd");
        baseConditionCode.setFamilyCd("FamilyCd");
        baseConditionCode.setCoinfectionGrpCd("CoinfectionGrpCd");

        // Assert values
        assertEquals("ConditionCd", baseConditionCode.getConditionCd());
        assertEquals("ConditionCodesetNm", baseConditionCode.getConditionCodesetNm());
        assertEquals(1, baseConditionCode.getConditionSeqNum());
        assertEquals("AssigningAuthorityCd", baseConditionCode.getAssigningAuthorityCd());
        assertEquals("AssigningAuthorityDescTxt", baseConditionCode.getAssigningAuthorityDescTxt());
        assertEquals("CodeSystemCd", baseConditionCode.getCodeSystemCd());
        assertEquals("CodeSystemDescTxt", baseConditionCode.getCodeSystemDescTxt());
        assertEquals("ConditionDescTxt", baseConditionCode.getConditionDescTxt());
        assertEquals("ConditionShortNm", baseConditionCode.getConditionShortNm());
        assertNotNull(baseConditionCode.getEffectiveFromTime());
        assertNotNull(baseConditionCode.getEffectiveToTime());
        assertEquals(2, baseConditionCode.getIndentLevelNbr());
        assertEquals("InvestigationFormCd", baseConditionCode.getInvestigationFormCd());
        assertEquals("IsModifiableInd", baseConditionCode.getIsModifiableInd());
        assertEquals(3L, baseConditionCode.getNbsUid());
        assertEquals("NndInd", baseConditionCode.getNndInd());
        assertEquals("ParentIsCd", baseConditionCode.getParentIsCd());
        assertEquals("ProgAreaCd", baseConditionCode.getProgAreaCd());
        assertEquals("ReportableMorbidityInd", baseConditionCode.getReportableMorbidityInd());
        assertEquals("ReportableSummaryInd", baseConditionCode.getReportableSummaryInd());
        assertEquals("StatusCd", baseConditionCode.getStatusCd());
        assertNotNull(baseConditionCode.getStatusTime());
        assertEquals("NndEntityIdentifier", baseConditionCode.getNndEntityIdentifier());
        assertEquals("NndSummaryEntityIdentifier", baseConditionCode.getNndSummaryEntityIdentifier());
        assertEquals("SummaryInvestigationFormCd", baseConditionCode.getSummaryInvestigationFormCd());
        assertEquals("ContactTracingEnableInd", baseConditionCode.getContactTracingEnableInd());
        assertEquals("VaccineEnableInd", baseConditionCode.getVaccineEnableInd());
        assertEquals("TreatmentEnableInd", baseConditionCode.getTreatmentEnableInd());
        assertEquals("LabReportEnableInd", baseConditionCode.getLabReportEnableInd());
        assertEquals("MorbReportEnableInd", baseConditionCode.getMorbReportEnableInd());
        assertEquals("PortReqIndCd", baseConditionCode.getPortReqIndCd());
        assertEquals("FamilyCd", baseConditionCode.getFamilyCd());
        assertEquals("CoinfectionGrpCd", baseConditionCode.getCoinfectionGrpCd());
    }


}
