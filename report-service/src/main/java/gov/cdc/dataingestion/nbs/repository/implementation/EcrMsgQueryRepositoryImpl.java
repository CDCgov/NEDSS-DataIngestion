package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.nbs.repository.EcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EcrMsgQueryRepositoryImpl implements EcrMsgQueryRepository {
    @PersistenceContext(unitName = "nbs")
    private EntityManager entityManager;

    public EcrMsgContainerDto FetchMsgContainerForApplicableEcr() {
        String queryString = loadSqlFromFile("ecr_msg_container.sql");
        Query query = entityManager.createNativeQuery(queryString);

        EcrMsgContainerDto ecrMsgContainerDto = new EcrMsgContainerDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            ecrMsgContainerDto.setMsgContainerUid(((Number)val[0]).intValue());
            ecrMsgContainerDto.setInvLocalId(String.valueOf(nullToString(val[1])));
            ecrMsgContainerDto.setNbsInterfaceUid(((Number)val[2]).intValue());
            ecrMsgContainerDto.setReceivingSystem(String.valueOf(nullToString(val[3])));
            ecrMsgContainerDto.setOngoingCase(String.valueOf(nullToString(val[4])));
            ecrMsgContainerDto.setVersionCtrNbr(((Number)val[5]).intValue());
            ecrMsgContainerDto.setDataMigrationStatus(((Number)val[6]).intValue());

            return ecrMsgContainerDto;

        }
        return null;
    }

    public EcrMsgPatientDto FetchMsgPatientForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_patient.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgPatientDto dto = new EcrMsgPatientDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setMsgContainerUid(((Number)val[0]).intValue());
            dto.setPatLocalId(String.valueOf(nullToString(val[1])));
            dto.setPatAuthorId(String.valueOf(nullToString(val[2])));
            dto.setPatAddrAsOfDt((Timestamp) val[3]);
            dto.setPatAddrCityTxt(String.valueOf(nullToString(val[4])));
            dto.setPatAddrCommentTxt(String.valueOf(nullToString(val[5])));
            dto.setPatAddrCountyCd(String.valueOf(nullToString(val[6])));
            dto.setPatAddrCountryCd(String.valueOf(nullToString(val[7])));
            dto.setPatAdditionalGenderTxt(String.valueOf(nullToString(val[8])));
            dto.setPatAddrCensusTractTxt(String.valueOf(nullToString(val[9])));
            dto.setPatAddrStateCd(String.valueOf(nullToString(val[10])));
            dto.setPatAddrStreetAddr1Txt(String.valueOf(nullToString(val[11])));
            dto.setPatAddrStreetAddr2Txt(String.valueOf(nullToString(val[12])));
            dto.setPatAddrZipCodeTxt(String.valueOf(nullToString(val[13])));
            dto.setPatBirthCountryCd(String.valueOf(nullToString(val[14])));
            dto.setPatBirthDt((Timestamp) val[15]);
            dto.setPatBirthSexCd(String.valueOf(nullToString(val[16])));
            dto.setPatCellPhoneNbrTxt(String.valueOf(nullToString(val[17])));
            dto.setPatCommentTxt(String.valueOf(nullToString(val[18])));
            dto.setPatCurrentSexCd(String.valueOf(nullToString(val[19])));
            dto.setPatDeceasedIndCd(String.valueOf(nullToString(val[20])));
            dto.setPatDeceasedDt((Timestamp) val[21]);
            dto.setPatEffectiveTime(String.valueOf(nullToString(val[22])));
            dto.setPatIdMedicalRecordNbrTxt(String.valueOf(nullToString(val[23])));
            dto.setPatIdStateHivCaseNbrTxt(String.valueOf(nullToString(val[24])));
            dto.setPatInfoAsOfDt((Timestamp) val[25]);
            dto.setPatIdSsnTxt(String.valueOf(nullToString(val[26])));
            dto.setPatEmailAddressTxt(String.valueOf(nullToString(val[27])));
            dto.setPatEthnicGroupIndCd(String.valueOf(nullToString(val[28])));
            dto.setPatEthnicityUnkReasonCd(String.valueOf(nullToString(val[29])));
            dto.setPatHomePhoneNbrTxt(String.valueOf(nullToString(val[30])));
            dto.setPatNameAliasTxt(String.valueOf(nullToString(val[31])));
            dto.setPatNameAsOfDt((Timestamp) val[32]);
            dto.setPatNameDegreeCd(String.valueOf(nullToString(val[33])));
            dto.setPatNameFirstTxt(String.valueOf(nullToString(val[34])));
            dto.setPatNameLastTxt(String.valueOf(nullToString(val[35])));
            dto.setPatNameMiddleTxt(String.valueOf(nullToString(val[36])));
            dto.setPatNamePrefixCd(String.valueOf(nullToString(val[37])));
            dto.setPatNameSuffixCd(String.valueOf(nullToString(val[38])));
            dto.setPatMaritalStatusCd(String.valueOf(nullToString(val[39])));
            dto.setPatPhoneCommentTxt(String.valueOf(nullToString(val[40])));
            dto.setPatPhoneCountryCodeTxt(((Number)val[41]).intValue());
            dto.setPatPrimaryLanguageCd(String.valueOf(nullToString(val[42])));
            dto.setPatPreferredGenderCd(String.valueOf(nullToString(val[43])));
            dto.setPatRaceCategoryCd(String.valueOf(nullToString(val[44])));
            dto.setPatRaceDescTxt(String.valueOf(nullToString(val[45])));
            dto.setPatReportedAge(((Number)val[46]).intValue());
            dto.setPatReportedAgeUnitCd(String.valueOf(nullToString(val[47])));
            dto.setPatSexUnkReasonCd(String.valueOf(nullToString(val[48])));
            dto.setPatSpeaksEnglishIndCd(String.valueOf(nullToString(val[49])));
            dto.setPatPhoneAsOfDt((Timestamp) val[50]);
            dto.setPatUrlAddressTxt(String.valueOf(nullToString(val[51])));
            dto.setPatWorkPhoneNbrTxt(String.valueOf(nullToString(val[52])));
            dto.setPatWorkPhoneExtensionTxt(((Number)val[53]).intValue());
            return dto;

        }
        return null;
    }

    public EcrMsgCaseDto FetchMsgCaseForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_case.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgCaseDto dto = new EcrMsgCaseDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setInvLocalId(String.valueOf(nullToString(val[0])));
            dto.setPatLocalId(String.valueOf(nullToString(val[1])));
            dto.setInvAuthorId(String.valueOf(nullToString(val[2])));
            dto.setInvCaseStatusCd(String.valueOf(nullToString(val[3])));
            dto.setInvCloseDt(String.valueOf(nullToString(val[4])));
            dto.setInvCommentTxt(String.valueOf(nullToString(val[5])));
            dto.setInvConditionCd(String.valueOf(nullToString(val[6])));
            dto.setInvContactInvCommentTxt(String.valueOf(nullToString(val[7])));
            dto.setInvContactInvPriorityCd(String.valueOf(nullToString(val[8])));
            dto.setInvContactInvStatusCd(String.valueOf(nullToString(val[9])));
            dto.setInvCurrProcessStateCd(String.valueOf(nullToString(val[10])));
            dto.setInvDaycareIndCd(String.valueOf(nullToString(val[11])));
            dto.setInvDetectionMethodCd(String.valueOf(nullToString(val[12])));
            dto.setInvDiagnosisDt(String.valueOf(nullToString(val[13])));
            dto.setInvDiseaseAcquiredLocCd(String.valueOf(nullToString(val[14])));
            dto.setInvEffectiveTime((Timestamp) val[15]);
            dto.setInvFoodhandlerIndCd(String.valueOf(nullToString(val[16])));
            dto.setInvHospitalizedAdmitDt(String.valueOf(nullToString(val[17])));
            dto.setInvHospitalizedDischargeDt(String.valueOf(nullToString(val[18])));
            dto.setInvHospitalizedIndCd(String.valueOf(nullToString(val[19])));
            dto.setInvHospStayDuration(((Number)val[20]).intValue());
            dto.setInvIllnessStartDt(String.valueOf(nullToString(val[21])));
            dto.setInvIllnessEndDt(String.valueOf(nullToString(val[22])));
            dto.setInvIllnessDuration(((Number)val[23]).intValue());
            dto.setInvIllnessDurationUnitCd(String.valueOf(nullToString(val[24])));
            dto.setInvIllnessOnsetAge(((Number)val[25]).intValue());
            dto.setInvIllnessOnsetAgeUnitCd(String.valueOf(nullToString(val[26])));
            dto.setInvInvestigatorAssignedDt(String.valueOf(nullToString(val[27])));
            dto.setInvImportCityTxt(String.valueOf(nullToString(val[28])));
            dto.setInvImportCountyCd(String.valueOf(nullToString(val[29])));
            dto.setInvImportCountryCd(String.valueOf(nullToString(val[30])));
            dto.setInvImportStateCd(String.valueOf(nullToString(val[31])));
            dto.setInvInfectiousFromDt(String.valueOf(nullToString(val[32])));
            dto.setInvInfectiousToDt(String.valueOf(nullToString(val[33])));
            dto.setInvLegacyCaseId(String.valueOf(nullToString(val[34])));
            dto.setInvMmwrWeekTxt(String.valueOf(nullToString(val[35])));
            dto.setInvMmwrYearTxt(String.valueOf(nullToString(val[36])));
            dto.setInvOutbreakIndCd(String.valueOf(nullToString(val[37])));
            dto.setInvOutbreakNameCd(String.valueOf(nullToString(val[38])));
            dto.setInvPatientDeathDt(String.valueOf(nullToString(val[39])));
            dto.setInvPatientDeathIndCd(String.valueOf(nullToString(val[40])));
            dto.setInvPregnancyIndCd(String.valueOf(nullToString(val[41])));
            dto.setInvReferralBasisCd(String.valueOf(nullToString(val[42])));
            dto.setInvReportDt(String.valueOf(nullToString(val[43])));
            dto.setInvReportToCountyDt(String.valueOf(nullToString(val[44])));
            dto.setInvReportToStateDt(String.valueOf(nullToString(val[45])));
            dto.setInvReportingCountyCd(String.valueOf(nullToString(val[46])));
            dto.setInvSharedIndCd(String.valueOf(nullToString(val[47])));
            dto.setInvSourceTypeCd(String.valueOf(nullToString(val[48])));
            dto.setInvStartDt(String.valueOf(nullToString(val[49])));
            dto.setInvStateId(String.valueOf(nullToString(val[50])));
            dto.setInvStatusCd(String.valueOf(nullToString(val[51])));
            dto.setInvTransmissionModeCd(String.valueOf(nullToString(val[52])));
            return dto;
        }
        return null;
    }

    public EcrMsgCaseParticipantDto FetchMsgCaseParticipantForApplicableEcr(String containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_case_participant.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        EcrMsgCaseParticipantDto dto = new EcrMsgCaseParticipantDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setMsgEventId(String.valueOf(nullToString(val[0])));
            dto.setMsgEventType(String.valueOf(nullToString(val[1])));
            dto.setAnswerTxt(String.valueOf(nullToString(val[2])));
            dto.setAnswerLargeTxt(String.valueOf(nullToString(val[3])));
            dto.setAnswerGroupSeqNbr(((Number)val[4]).intValue());
            dto.setPartTypeCd(String.valueOf(nullToString(val[5])));
            dto.setQuestionIdentifier(String.valueOf(nullToString(val[6])));
            dto.setQuestionGroupSeqNbr(((Number)val[7]).intValue());
            dto.setSeqNbr(((Number)val[8]).intValue());
            return dto;
        }
        return null;
    }

    public EcrMsgCaseAnswerDto FetchMsgCaseAnswerForApplicableEcr(String containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_case_answer.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        EcrMsgCaseAnswerDto dto = new EcrMsgCaseAnswerDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setQuestionIdentifier(String.valueOf(nullToString(val[0])));
            dto.setMsgContainerUid(((Number)val[1]).intValue());
            dto.setMsgEventId(String.valueOf(nullToString(val[2])));
            dto.setMsgEventType(String.valueOf(nullToString(val[3])));
            dto.setAnsCodeSystemCd(String.valueOf(nullToString(val[4])));
            dto.setAnsCodeSystemDescTxt(String.valueOf(nullToString(val[5])));
            dto.setAnsDisplayTxt(String.valueOf(nullToString(val[6])));
            dto.setAnswerTxt(String.valueOf(nullToString(val[7])));
            dto.setPartTypeCd(String.valueOf(nullToString(val[8])));
            dto.setQuesCodeSystemCd(String.valueOf(nullToString(val[9])));
            dto.setQuesCodeSystemDescTxt(String.valueOf(nullToString(val[10])));
            dto.setQuesDisplayTxt(String.valueOf(nullToString(val[11])));
            dto.setQuestionDisplayName(String.valueOf(nullToString(val[12])));
            dto.setAnsToCode(String.valueOf(nullToString(val[13])));
            dto.setAnsToCodeSystemCd(String.valueOf(nullToString(val[14])));
            dto.setAnsToDisplayNm(String.valueOf(nullToString(val[15])));
            dto.setCodeTranslationRequired(String.valueOf(nullToString(val[16])));
            dto.setAnsToCodeSystemDescTxt(String.valueOf(nullToString(val[17])));
            return dto;
        }
        return null;
    }

    public EcrMsgCaseAnswerDtoRepeat FetchMsgCaseAnswerRepeatForApplicableEcr(String containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_case_answer_repeat.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        EcrMsgCaseAnswerDtoRepeat dto = new EcrMsgCaseAnswerDtoRepeat();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setQuestionIdentifier(String.valueOf(nullToString(val[0])));
            dto.setMsgContainerUid(((Number)val[1]).intValue());
            dto.setMsgEventId(String.valueOf(nullToString(val[2])));
            dto.setMsgEventType(String.valueOf(nullToString(val[3])));
            dto.setAnsCodeSystemCd(String.valueOf(nullToString(val[4])));
            dto.setAnsCodeSystemDescTxt(String.valueOf(nullToString(val[5])));
            dto.setAnsDisplayTxt(String.valueOf(nullToString(val[6])));
            dto.setAnswerTxt(String.valueOf(nullToString(val[7])));
            dto.setPartTypeCd(String.valueOf(nullToString(val[8])));
            dto.setQuesCodeSystemCd(String.valueOf(nullToString(val[9])));
            dto.setQuesCodeSystemDescTxt(String.valueOf(nullToString(val[10])));
            dto.setQuesDisplayTxt(String.valueOf(nullToString(val[11])));
            dto.setQuestionDisplayName(String.valueOf(nullToString(val[12])));
            dto.setAnsToCode(String.valueOf(nullToString(val[13])));
            dto.setAnsToCodeSystemCd(String.valueOf(nullToString(val[14])));
            dto.setAnsToDisplayNm(String.valueOf(nullToString(val[15])));
            dto.setCodeTranslationRequired(String.valueOf(nullToString(val[16])));
            dto.setAnsToCodeSystemDescTxt(String.valueOf(nullToString(val[17])));
            return dto;
        }
        return null;
    }

    public EcrMsgXmlAnswerDto FetchMsgXmlAnswerForApplicableEcr(String containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_xml_answer.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        EcrMsgXmlAnswerDto dto = new EcrMsgXmlAnswerDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setDataType(String.valueOf(nullToString(val[0])));
            dto.setAnswerXmlTxt(String.valueOf(nullToString(val[1])));
            return dto;
        }
        return null;
    }

    public EcrMsgProviderDto FetchMsgProviderForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_provider.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgProviderDto dto = new EcrMsgProviderDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setPrvLocalId(String.valueOf(nullToString(val[0])));
            dto.setPrvAuthorId(String.valueOf(nullToString(val[1])));
            dto.setPrvAddrCityTxt(String.valueOf(nullToString(val[2])));
            dto.setPrvAddrCommentTxt(String.valueOf(nullToString(val[3])));
            dto.setPrvAddrCountyCd(String.valueOf(nullToString(val[4])));
            dto.setPrvAddrCountryCd(String.valueOf(nullToString(val[5])));
            dto.setPrvAddrStreetAddr1Txt(String.valueOf(nullToString(val[6])));
            dto.setPrvAddrStreetAddr2Txt(String.valueOf(nullToString(val[7])));
            dto.setPrvAddrStateCd(String.valueOf(nullToString(val[8])));
            dto.setPrvAddrZipCodeTxt(String.valueOf(nullToString(val[9])));
            dto.setPrvCommentTxt(String.valueOf(nullToString(val[10])));
            dto.setPrvIdAltIdNbrTxt(String.valueOf(nullToString(val[11])));
            dto.setPrvIdQuickCodeTxt(String.valueOf(nullToString(val[12])));
            dto.setPrvIdNbrTxt(String.valueOf(nullToString(val[13])));
            dto.setPrvIdNpiTxt(String.valueOf(nullToString(val[14])));
            dto.setPrvEffectiveTime((Timestamp)val[15]);
            dto.setPrvEmailAddressTxt(String.valueOf(nullToString(val[16])));
            dto.setPrvNameDegreeCd(String.valueOf(nullToString(val[17])));
            dto.setPrvNameFirstTxt(String.valueOf(nullToString(val[18])));
            dto.setPrvNameLastTxt(String.valueOf(nullToString(val[19])));
            dto.setPrvNameMiddleTxt(String.valueOf(nullToString(val[20])));
            dto.setPrvNamePrefixCd(String.valueOf(nullToString(val[21])));
            dto.setPrvNameSuffixCd(String.valueOf(nullToString(val[22])));
            dto.setPrvPhoneCommentTxt(String.valueOf(nullToString(val[23])));
            dto.setPrvPhoneCountryCodeTxt(String.valueOf(nullToString(val[24])));
            dto.setPrvPhoneExtensionTxt(((Number)val[25]).intValue());
            dto.setPrvPhoneNbrTxt(String.valueOf(nullToString(val[26])));
            dto.setPrvRoleCd(String.valueOf(nullToString(val[27])));
            dto.setPrvUrlAddressTxt(String.valueOf(nullToString(val[28])));
            return dto;
        }
        return null;
    }

    public EcrMsgOrganizationDto FetchMsgOrganizationForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_organization.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgOrganizationDto dto = new EcrMsgOrganizationDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setOrgLocalId(String.valueOf(nullToString(val[0])));
            dto.setOrgAuthorId(String.valueOf(nullToString(val[1])));
            dto.setOrgEffectiveTime((Timestamp)val[2]);
            dto.setOrgNameTxt(String.valueOf(nullToString(val[3])));
            dto.setOrgAddrCityTxt(String.valueOf(nullToString(val[4])));
            dto.setOrgAddrCommentTxt(String.valueOf(nullToString(val[5])));
            dto.setOrgAddrCountyCd(String.valueOf(nullToString(val[6])));
            dto.setOrgAddrCountryCd(String.valueOf(nullToString(val[7])));
            dto.setOrgAddrStateCd(String.valueOf(nullToString(val[8])));
            dto.setOrgAddrStreetAddr1Txt(String.valueOf(nullToString(val[9])));
            dto.setOrgAddrStreetAddr2Txt(String.valueOf(nullToString(val[10])));
            dto.setOrgAddrZipCodeTxt(String.valueOf(nullToString(val[11])));
            dto.setOrgClassCd(String.valueOf(nullToString(val[12])));
            dto.setOrgCommentTxt(String.valueOf(nullToString(val[13])));
            dto.setOrgEmailAddressTxt(String.valueOf(nullToString(val[14])));
            dto.setOrgIdCliaNbrTxt(String.valueOf(nullToString(val[15])));
            dto.setOrgIdFacilityIdentifierTxt(String.valueOf(nullToString(val[16])));
            dto.setOrgIdQuickCodeTxt(String.valueOf(nullToString(val[17])));
            dto.setOrgPhoneCommentTxt(String.valueOf(nullToString(val[18])));
            dto.setOrgPhoneCountryCodeTxt(String.valueOf(nullToString(val[19])));
            dto.setOrgPhoneExtensionTxt(((Number)val[20]).intValue());
            dto.setOrgPhoneNbrTxt(String.valueOf(nullToString(val[21])));
            dto.setOrgRoleCd(String.valueOf(nullToString(val[22])));
            dto.setOrgUrlAddressTxt(String.valueOf(nullToString(val[23])));
            return dto;
        }
        return null;
    }

    public EcrMsgPlaceDto FetchMsgPlaceForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_place.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgPlaceDto dto = new EcrMsgPlaceDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setMsgContainerUid(((Number)val[0]).intValue());
            dto.setPlaLocalId(String.valueOf(nullToString(val[1])));
            dto.setPlaAuthorId(String.valueOf(nullToString(val[2])));
            dto.setPlaEffectiveTime((Timestamp)val[3]);
            dto.setPlaAddrAsOfDt((Timestamp)val[4]);
            dto.setPlaAddrCityTxt(String.valueOf(nullToString(val[5])));
            dto.setPlaAddrCountyCd(String.valueOf(nullToString(val[6])));
            dto.setPlaAddrCountryCd(String.valueOf(nullToString(val[7])));
            dto.setPlaAddrStateCd(String.valueOf(nullToString(val[8])));
            dto.setPlaAddrStreetAddr1Txt(String.valueOf(nullToString(val[9])));
            dto.setPlaAddrStreetAddr2Txt(String.valueOf(nullToString(val[10])));
            dto.setPlaAddrZipCodeTxt(String.valueOf(nullToString(val[11])));
            dto.setPlaAddrCommentTxt(String.valueOf(nullToString(val[12])));
            dto.setPlaCensusTractTxt(String.valueOf(nullToString(val[13])));
            dto.setPlaCommentTxt(String.valueOf(nullToString(val[14])));
            dto.setPlaEmailAddressTxt(String.valueOf(nullToString(val[15])));
            dto.setPlaIdQuickCode(String.valueOf(nullToString(val[16])));
            dto.setPlaNameTxt(String.valueOf(nullToString(val[17])));
            dto.setPlaPhoneAsOfDt((Timestamp)val[18]);
            dto.setPlaPhoneCountryCodeTxt(String.valueOf(nullToString(val[19])));
            dto.setPlaPhoneExtensionTxt(String.valueOf(nullToString(val[20])));
            dto.setPlaPhoneNbrTxt(String.valueOf(nullToString(val[21])));
            dto.setPlaPhoneCommentTxt(String.valueOf(nullToString(val[22])));
            dto.setPlaTypeCd(String.valueOf(nullToString(val[23])));
            dto.setPlaUrlAddressTxt(String.valueOf(nullToString(val[24])));
            return dto;
        }
        return null;
    }

    public EcrMsgInterviewDto FetchMsgInterviewForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_interview.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgInterviewDto dto = new EcrMsgInterviewDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setMsgContainerUid(((Number)val[0]).intValue());
            dto.setIxsLocalId(String.valueOf(nullToString(val[1])));
            dto.setIxsIntervieweeId(String.valueOf(nullToString(val[2])));
            dto.setIxsAuthorId(String.valueOf(nullToString(val[3])));
            dto.setIxsEffectiveTime((Timestamp)val[4]);
            dto.setIxsInterviewDt((Timestamp)val[5]);
            dto.setIxsInterviewLocCd(String.valueOf(nullToString(val[6])));
            dto.setIxsIntervieweeRoleCd(String.valueOf(nullToString(val[7])));
            dto.setIxsInterviewTypeCd(String.valueOf(nullToString(val[8])));
            dto.setIxsStatusCd(String.valueOf(nullToString(val[9])));
            return dto;
        }
        return null;
    }

    public EcrMsgInterviewAnswerDto FetchMsgInterviewAnswerForApplicableEcr(String containerId, String ixsLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_interview_answer.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("IXS_LOCAL_ID", ixsLocalId);
        EcrMsgInterviewAnswerDto dto = new EcrMsgInterviewAnswerDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setQuestionIdentifier(String.valueOf(nullToString(val[0])));
            dto.setMsgContainerUid(((Number)val[1]).intValue());
            dto.setMsgEventId(String.valueOf(nullToString(val[2])));
            dto.setMsgEventType(String.valueOf(nullToString(val[3])));
            dto.setAnsCodeSystemCd(String.valueOf(nullToString(val[4])));
            dto.setAnsCodeSystemDescTxt(String.valueOf(nullToString(val[5])));
            dto.setAnsDisplayTxt(String.valueOf(nullToString(val[6])));
            dto.setAnswerTxt(String.valueOf(nullToString(val[7])));
            dto.setPartTypeCd(String.valueOf(nullToString(val[8])));
            dto.setQuesCodeSystemCd(String.valueOf(nullToString(val[9])));
            dto.setQuesCodeSystemDescTxt(String.valueOf(nullToString(val[10])));
            dto.setQuesDisplayTxt(String.valueOf(nullToString(val[11])));
            dto.setQuestionDisplayName(String.valueOf(nullToString(val[12])));
            dto.setAnsToCode(String.valueOf(nullToString(val[13])));
            dto.setAnsToCodeSystemCd(String.valueOf(nullToString(val[14])));
            dto.setAnsToDisplayNm(String.valueOf(nullToString(val[15])));
            dto.setCodeTranslationRequired(String.valueOf(nullToString(val[16])));
            dto.setAnsToCodeSystemDescTxt(String.valueOf(nullToString(val[17])));
            return dto;
        }
        return null;
    }

    public EcrMsgInterviewAnswerDtoRepeat FetchMsgInterviewAnswerRepeatForApplicableEcr(String containerId, String ixsLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_interview_answer_repeat.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("IXS_LOCAL_ID", ixsLocalId);
        EcrMsgInterviewAnswerDtoRepeat dto = new EcrMsgInterviewAnswerDtoRepeat();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setQuestionIdentifier(String.valueOf(nullToString(val[0])));
            dto.setMsgContainerUid(((Number)val[1]).intValue());
            dto.setMsgEventId(String.valueOf(nullToString(val[2])));
            dto.setMsgEventType(String.valueOf(nullToString(val[3])));
            dto.setAnsCodeSystemCd(String.valueOf(nullToString(val[4])));
            dto.setAnsCodeSystemDescTxt(String.valueOf(nullToString(val[5])));
            dto.setAnsDisplayTxt(String.valueOf(nullToString(val[6])));
            dto.setAnswerTxt(String.valueOf(nullToString(val[7])));
            dto.setPartTypeCd(String.valueOf(nullToString(val[8])));
            dto.setQuesCodeSystemCd(String.valueOf(nullToString(val[9])));
            dto.setQuesCodeSystemDescTxt(String.valueOf(nullToString(val[10])));
            dto.setQuesDisplayTxt(String.valueOf(nullToString(val[11])));
            dto.setQuestionDisplayName(String.valueOf(nullToString(val[12])));
            dto.setAnsToCode(String.valueOf(nullToString(val[13])));
            dto.setAnsToCodeSystemCd(String.valueOf(nullToString(val[14])));
            dto.setAnsToDisplayNm(String.valueOf(nullToString(val[15])));
            dto.setCodeTranslationRequired(String.valueOf(nullToString(val[16])));
            dto.setAnsToCodeSystemDescTxt(String.valueOf(nullToString(val[17])));
            return dto;
        }
        return null;
    }

    public EcrMsgTreatmentDto FetchMsgTreatmentForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_treatment.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgTreatmentDto dto = new EcrMsgTreatmentDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setTrtLocalId(String.valueOf(nullToString(val[0])));
            dto.setTrtAuthorId(String.valueOf(nullToString(val[1])));
            dto.setTrtCompositeCd(String.valueOf(nullToString(val[2])));
            dto.setTrtCommentTxt(String.valueOf(nullToString(val[3])));
            dto.setTrtCustomTreatmentTxt(String.valueOf(nullToString(val[4])));
            dto.setTrtDosageAmt(val[5] == null ? null : ((Number)val[5]).intValue());
            dto.setTrtDosageUnitCd(String.valueOf(nullToString(val[6])));
            dto.setTrtDrugCd(String.valueOf(nullToString(val[7])));
            dto.setTrtDurationAmt(val[8] == null ? null : ((Number)val[8]).intValue());
            dto.setTrtDurationUnitCd(String.valueOf(nullToString(val[9])));
            dto.setTrtEffectiveTime((Timestamp) val[10]);
            dto.setTrtFrequencyAmtCd(String.valueOf(nullToString(val[11])));
            dto.setTrtRouteCd(String.valueOf(nullToString(val[12])));
            dto.setTrtTreatmentDt((Timestamp) val[13]);
            return dto;
        }
        return null;
    }

    public EcrMsgTreatmentProviderDto FetchMsgTreatmentProviderForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_treatment_provider.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgTreatmentProviderDto dto = new EcrMsgTreatmentProviderDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setPrvLocalId(String.valueOf(nullToString(val[0])));
            dto.setPrvAuthorId(String.valueOf(nullToString(val[1])));
            dto.setPrvAddrCityTxt(String.valueOf(nullToString(val[2])));
            dto.setPrvAddrCommentTxt(String.valueOf(nullToString(val[3])));
            dto.setPrvAddrCountyCd(String.valueOf(nullToString(val[4])));
            dto.setPrvAddrCountryCd(String.valueOf(nullToString(val[5])));
            dto.setPrvAddrStreetAddr1Txt(String.valueOf(nullToString(val[6])));
            dto.setPrvAddrStreetAddr2Txt(String.valueOf(nullToString(val[7])));
            dto.setPrvAddrStateCd(String.valueOf(nullToString(val[8])));
            dto.setPrvAddrZipCodeTxt(String.valueOf(nullToString(val[9])));
            dto.setPrvCommentTxt(String.valueOf(nullToString(val[10])));
            dto.setPrvIdAltIdNbrTxt(String.valueOf(nullToString(val[11])));
            dto.setPrvIdQuickCodeTxt(String.valueOf(nullToString(val[12])));
            dto.setPrvIdNbrTxt(String.valueOf(nullToString(val[13])));
            dto.setPrvIdNpiTxt(String.valueOf(nullToString(val[14])));
            dto.setPrvEffectiveTime((Timestamp) val[15]);
            dto.setPrvEmailAddressTxt(String.valueOf(nullToString(val[16])));
            dto.setPrvNameDegreeCd(String.valueOf(nullToString(val[17])));
            dto.setPrvNameFirstTxt(String.valueOf(nullToString(val[18])));
            dto.setPrvNameLastTxt(String.valueOf(nullToString(val[19])));
            dto.setPrvNameMiddleTxt(String.valueOf(nullToString(val[20])));
            dto.setPrvNamePrefixCd(String.valueOf(nullToString(val[21])));
            dto.setPrvNameSuffixCd(String.valueOf(nullToString(val[22])));
            dto.setPrvPhoneCommentTxt(String.valueOf(nullToString(val[23])));
            dto.setPrvPhoneCountryCodeTxt(String.valueOf(nullToString(val[24])));
            dto.setPrvPhoneExtensionTxt(val[25] == null ? null : ((Number)val[25]).intValue());
            dto.setPrvPhoneNbrTxt(String.valueOf(nullToString(val[26])));
            dto.setPrvRoleCd(String.valueOf(nullToString(val[27])));
            dto.setPrvUrlAddressTxt(String.valueOf(nullToString(val[28])));
            return dto;
        }
        return null;
    }

    public EcrMsgTreatmentOrganizationDto FetchMsgTreatmentOrganizationForApplicableEcr(String containerId) {
        String queryString = loadSqlFromFile("ecr_msg_treatment_organization.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        EcrMsgTreatmentOrganizationDto dto = new EcrMsgTreatmentOrganizationDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            dto.setOrgLocalId(String.valueOf(nullToString(val[0])));
            dto.setOrgAuthorId(String.valueOf(nullToString(val[1])));
            dto.setOrgEffectiveTime((Timestamp) val[2]);
            dto.setOrgNameTxt(String.valueOf(nullToString(val[3])));
            dto.setOrgAddrCityTxt(String.valueOf(nullToString(val[4])));
            dto.setOrgAddrCommentTxt(String.valueOf(nullToString(val[5])));
            dto.setOrgAddrCountyCd(String.valueOf(nullToString(val[6])));
            dto.setOrgAddrCountryCd(String.valueOf(nullToString(val[7])));
            dto.setOrgAddrStateCd(String.valueOf(nullToString(val[8])));
            dto.setOrgAddrStreetAddr1Txt(String.valueOf(nullToString(val[9])));
            dto.setOrgAddrStreetAddr2Txt(String.valueOf(nullToString(val[10])));
            dto.setOrgAddrZipCodeTxt(String.valueOf(nullToString(val[11])));
            dto.setOrgClassCd(String.valueOf(nullToString(val[12])));
            dto.setOrgCommentTxt(String.valueOf(nullToString(val[13])));
            dto.setOrgEmailAddressTxt(String.valueOf(nullToString(val[14])));
            dto.setOrgIdCliaNbrTxt(String.valueOf(nullToString(val[15])));
            dto.setOrgIdFacilityIdentifierTxt(String.valueOf(nullToString(val[16])));
            dto.setOrgIdQuickCodeTxt(String.valueOf(nullToString(val[17])));
            dto.setOrgPhoneCommentTxt(String.valueOf(nullToString(val[18])));
            dto.setOrgPhoneCountryCodeTxt(String.valueOf(nullToString(val[19])));
            dto.setOrgPhoneExtensionTxt(val[20] == null ? null : ((Number)val[20]).intValue());
            dto.setOrgPhoneNbrTxt(String.valueOf(nullToString(val[21])));
            dto.setOrgRoleCd(String.valueOf(nullToString(val[22])));
            dto.setOrgUrlAddressTxt(String.valueOf(nullToString(val[23])));
            return dto;
        }
        return null;
    }

    public void UpdateMatchEcrRecordForProcessing(Integer containerUid) {
        String queryString = loadSqlFromFile("ecr_msg_container_update_match_record.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("customValue", containerUid);
        query.executeUpdate();
    }

    private String loadSqlFromFile(String filename) {
        try (InputStream is = getClass().getResourceAsStream("/queries/ecr/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SQL file: " + filename, e);
        }
    }

    public static String nullToString(Object obj) {
        return obj != null ? String.valueOf(obj) : "";
    }
}
