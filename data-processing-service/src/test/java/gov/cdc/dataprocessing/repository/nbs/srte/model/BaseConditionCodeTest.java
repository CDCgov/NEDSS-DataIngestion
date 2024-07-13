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

    @Test
    void testToString() {
        BaseConditionCode baseConditionCode = new BaseConditionCode();
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

        String expectedString = "BaseConditionCode(conditionCd=ConditionCd, conditionCodesetNm=ConditionCodesetNm, conditionSeqNum=1, assigningAuthorityCd=AssigningAuthorityCd, assigningAuthorityDescTxt=AssigningAuthorityDescTxt, codeSystemCd=CodeSystemCd, codeSystemDescTxt=CodeSystemDescTxt, conditionDescTxt=ConditionDescTxt, conditionShortNm=ConditionShortNm, effectiveFromTime=" + baseConditionCode.getEffectiveFromTime() + ", effectiveToTime=" + baseConditionCode.getEffectiveToTime() + ", indentLevelNbr=2, investigationFormCd=InvestigationFormCd, isModifiableInd=IsModifiableInd, nbsUid=3, nndInd=NndInd, parentIsCd=ParentIsCd, progAreaCd=ProgAreaCd, reportableMorbidityInd=ReportableMorbidityInd, reportableSummaryInd=ReportableSummaryInd, statusCd=StatusCd, statusTime=" + baseConditionCode.getStatusTime() + ", nndEntityIdentifier=NndEntityIdentifier, nndSummaryEntityIdentifier=NndSummaryEntityIdentifier, summaryInvestigationFormCd=SummaryInvestigationFormCd, contactTracingEnableInd=ContactTracingEnableInd, vaccineEnableInd=VaccineEnableInd, treatmentEnableInd=TreatmentEnableInd, labReportEnableInd=LabReportEnableInd, morbReportEnableInd=MorbReportEnableInd, portReqIndCd=PortReqIndCd, familyCd=FamilyCd, coinfectionGrpCd=CoinfectionGrpCd)";
        assertEquals(expectedString, baseConditionCode.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        BaseConditionCode baseConditionCode1 = new BaseConditionCode();
        baseConditionCode1.setConditionCd("ConditionCd");
        baseConditionCode1.setConditionCodesetNm("ConditionCodesetNm");
        baseConditionCode1.setConditionSeqNum(1);
        baseConditionCode1.setAssigningAuthorityCd("AssigningAuthorityCd");
        baseConditionCode1.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        baseConditionCode1.setCodeSystemCd("CodeSystemCd");
        baseConditionCode1.setCodeSystemDescTxt("CodeSystemDescTxt");
        baseConditionCode1.setConditionDescTxt("ConditionDescTxt");
        baseConditionCode1.setConditionShortNm("ConditionShortNm");
        baseConditionCode1.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode1.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode1.setIndentLevelNbr(2);
        baseConditionCode1.setInvestigationFormCd("InvestigationFormCd");
        baseConditionCode1.setIsModifiableInd("IsModifiableInd");
        baseConditionCode1.setNbsUid(3L);
        baseConditionCode1.setNndInd("NndInd");
        baseConditionCode1.setParentIsCd("ParentIsCd");
        baseConditionCode1.setProgAreaCd("ProgAreaCd");
        baseConditionCode1.setReportableMorbidityInd("ReportableMorbidityInd");
        baseConditionCode1.setReportableSummaryInd("ReportableSummaryInd");
        baseConditionCode1.setStatusCd("StatusCd");
        baseConditionCode1.setStatusTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode1.setNndEntityIdentifier("NndEntityIdentifier");
        baseConditionCode1.setNndSummaryEntityIdentifier("NndSummaryEntityIdentifier");
        baseConditionCode1.setSummaryInvestigationFormCd("SummaryInvestigationFormCd");
        baseConditionCode1.setContactTracingEnableInd("ContactTracingEnableInd");
        baseConditionCode1.setVaccineEnableInd("VaccineEnableInd");
        baseConditionCode1.setTreatmentEnableInd("TreatmentEnableInd");
        baseConditionCode1.setLabReportEnableInd("LabReportEnableInd");
        baseConditionCode1.setMorbReportEnableInd("MorbReportEnableInd");
        baseConditionCode1.setPortReqIndCd("PortReqIndCd");
        baseConditionCode1.setFamilyCd("FamilyCd");
        baseConditionCode1.setCoinfectionGrpCd("CoinfectionGrpCd");

        BaseConditionCode baseConditionCode2 = new BaseConditionCode();
        baseConditionCode2.setConditionCd("ConditionCd");
        baseConditionCode2.setConditionCodesetNm("ConditionCodesetNm");
        baseConditionCode2.setConditionSeqNum(1);
        baseConditionCode2.setAssigningAuthorityCd("AssigningAuthorityCd");
        baseConditionCode2.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        baseConditionCode2.setCodeSystemCd("CodeSystemCd");
        baseConditionCode2.setCodeSystemDescTxt("CodeSystemDescTxt");
        baseConditionCode2.setConditionDescTxt("ConditionDescTxt");
        baseConditionCode2.setConditionShortNm("ConditionShortNm");
        baseConditionCode2.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode2.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode2.setIndentLevelNbr(2);
        baseConditionCode2.setInvestigationFormCd("InvestigationFormCd");
        baseConditionCode2.setIsModifiableInd("IsModifiableInd");
        baseConditionCode2.setNbsUid(3L);
        baseConditionCode2.setNndInd("NndInd");
        baseConditionCode2.setParentIsCd("ParentIsCd");
        baseConditionCode2.setProgAreaCd("ProgAreaCd");
        baseConditionCode2.setReportableMorbidityInd("ReportableMorbidityInd");
        baseConditionCode2.setReportableSummaryInd("ReportableSummaryInd");
        baseConditionCode2.setStatusCd("StatusCd");
        baseConditionCode2.setStatusTime(new Timestamp(System.currentTimeMillis()));
        baseConditionCode2.setNndEntityIdentifier("NndEntityIdentifier");
        baseConditionCode2.setNndSummaryEntityIdentifier("NndSummaryEntityIdentifier");
        baseConditionCode2.setSummaryInvestigationFormCd("SummaryInvestigationFormCd");
        baseConditionCode2.setContactTracingEnableInd("ContactTracingEnableInd");
        baseConditionCode2.setVaccineEnableInd("VaccineEnableInd");
        baseConditionCode2.setTreatmentEnableInd("TreatmentEnableInd");
        baseConditionCode2.setLabReportEnableInd("LabReportEnableInd");
        baseConditionCode2.setMorbReportEnableInd("MorbReportEnableInd");
        baseConditionCode2.setPortReqIndCd("PortReqIndCd");
        baseConditionCode2.setFamilyCd("FamilyCd");
        baseConditionCode2.setCoinfectionGrpCd("CoinfectionGrpCd");

        BaseConditionCode baseConditionCode3 = new BaseConditionCode();
        baseConditionCode3.setConditionCd("DifferentConditionCd");

        // Assert equals and hashCode
        assertEquals(baseConditionCode1, baseConditionCode2);
        assertEquals(baseConditionCode1.hashCode(), baseConditionCode2.hashCode());

        assertNotEquals(baseConditionCode1, baseConditionCode3);
        assertNotEquals(baseConditionCode1.hashCode(), baseConditionCode3.hashCode());
    }
}
