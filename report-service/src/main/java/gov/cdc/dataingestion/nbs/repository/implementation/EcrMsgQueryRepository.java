package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.nbs.repository.IEcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EcrMsgQueryRepository implements IEcrMsgQueryRepository {
    @PersistenceContext(unitName = "nbs")
    private EntityManager entityManager;

    public EcrMsgContainerDto FetchMsgContainerForApplicableEcr() {
        String queryString = loadSqlFromFile("ecr_msg_container.sql");
        Query query = entityManager.createNativeQuery(queryString);

        EcrMsgContainerDto ecrMsgContainerDto = new EcrMsgContainerDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            ecrMsgContainerDto.setMsgContainerUid(nullCheckInt(val[0]));
            ecrMsgContainerDto.setInvLocalId((nullToString(val[1])));
            ecrMsgContainerDto.setNbsInterfaceUid(nullCheckInt(val[2]));
            ecrMsgContainerDto.setReceivingSystem((nullToString(val[3])));
            ecrMsgContainerDto.setOngoingCase((nullToString(val[4])));
            ecrMsgContainerDto.setVersionCtrNbr(nullCheckInt(val[5]));
            ecrMsgContainerDto.setDataMigrationStatus(nullCheckInt(val[6]));
            return ecrMsgContainerDto;

        }
        return null;
    }

    public List<EcrMsgPatientDto> FetchMsgPatientForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_patient.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<Object[]> results = query.getResultList();
        List<EcrMsgPatientDto> dtoList = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgPatientDto dto = new EcrMsgPatientDto();
                dto.setMsgContainerUid(((Number)val[0]).intValue());
                dto.setPatLocalId((nullToString(val[1])));
                dto.setPatAuthorId((nullToString(val[2])));
                dto.setPatAddrAsOfDt((Timestamp) val[3]);
                dto.setPatAddrCityTxt((nullToString(val[4])));
                dto.setPatAddrCommentTxt((nullToString(val[5])));
                dto.setPatAddrCountyCd((nullToString(val[6])));
                dto.setPatAddrCountryCd((nullToString(val[7])));
                dto.setPatAdditionalGenderTxt((nullToString(val[8])));
                dto.setPatAddrCensusTractTxt((nullToString(val[9])));
                dto.setPatAddrStateCd((nullToString(val[10])));
                dto.setPatAddrStreetAddr1Txt((nullToString(val[11])));
                dto.setPatAddrStreetAddr2Txt((nullToString(val[12])));
                dto.setPatAddrZipCodeTxt((nullToString(val[13])));
                dto.setPatBirthCountryCd((nullToString(val[14])));
                dto.setPatBirthDt((Timestamp) val[15]);
                dto.setPatBirthSexCd((nullToString(val[16])));
                dto.setPatCellPhoneNbrTxt((nullToString(val[17])));
                dto.setPatCommentTxt((nullToString(val[18])));
                dto.setPatCurrentSexCd((nullToString(val[19])));
                dto.setPatDeceasedIndCd((nullToString(val[20])));
                dto.setPatDeceasedDt((Timestamp) val[21]);
                dto.setPatEffectiveTime((nullToString(val[22])));
                dto.setPatIdMedicalRecordNbrTxt((nullToString(val[23])));
                dto.setPatIdStateHivCaseNbrTxt((nullToString(val[24])));
                dto.setPatInfoAsOfDt((Timestamp) val[25]);
                dto.setPatIdSsnTxt((nullToString(val[26])));
                dto.setPatEmailAddressTxt((nullToString(val[27])));
                dto.setPatEthnicGroupIndCd((nullToString(val[28])));
                dto.setPatEthnicityUnkReasonCd((nullToString(val[29])));
                dto.setPatHomePhoneNbrTxt((nullToString(val[30])));
                dto.setPatNameAliasTxt((nullToString(val[31])));
                dto.setPatNameAsOfDt((Timestamp) val[32]);
                dto.setPatNameDegreeCd((nullToString(val[33])));
                dto.setPatNameFirstTxt((nullToString(val[34])));
                dto.setPatNameLastTxt((nullToString(val[35])));
                dto.setPatNameMiddleTxt((nullToString(val[36])));
                dto.setPatNamePrefixCd((nullToString(val[37])));
                dto.setPatNameSuffixCd((nullToString(val[38])));
                dto.setPatMaritalStatusCd((nullToString(val[39])));
                dto.setPatPhoneCommentTxt((nullToString(val[40])));

                dto.setPatPhoneCountryCodeTxt(nullCheckInt(val[41]));
                dto.setPatPrimaryLanguageCd((nullToString(val[42])));
                dto.setPatPreferredGenderCd((nullToString(val[43])));
                dto.setPatRaceCategoryCd((nullToString(val[44])));
                dto.setPatRaceDescTxt((nullToString(val[45])));
                dto.setPatReportedAge(nullCheckInt(val[46]));
                dto.setPatReportedAgeUnitCd((nullToString(val[47])));
                dto.setPatSexUnkReasonCd((nullToString(val[48])));
                dto.setPatSpeaksEnglishIndCd((nullToString(val[49])));
                dto.setPatPhoneAsOfDt((Timestamp) val[50]);
                dto.setPatUrlAddressTxt((nullToString(val[51])));
                dto.setPatWorkPhoneNbrTxt((nullToString(val[52])));
                dto.setPatWorkPhoneExtensionTxt(nullCheckInt(val[53]));
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    public List<EcrMsgCaseDto> FetchMsgCaseForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_case.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);

        List<Object[]> results = query.getResultList();
        List<EcrMsgCaseDto> dtos = new ArrayList<>();

        if (results != null) {
            for (Object[] val : results) {
                EcrMsgCaseDto dto = new EcrMsgCaseDto();
                dto.setInvLocalId((nullToString(val[0])));
                dto.setPatLocalId((nullToString(val[1])));
                dto.setInvAuthorId((nullToString(val[2])));
                dto.setInvCaseStatusCd((nullToString(val[3])));
                dto.setInvCloseDt((nullToString(val[4])));
                dto.setInvCommentTxt((nullToString(val[5])));
                dto.setInvConditionCd((nullToString(val[6])));
                dto.setInvContactInvCommentTxt((nullToString(val[7])));
                dto.setInvContactInvPriorityCd((nullToString(val[8])));
                dto.setInvContactInvStatusCd((nullToString(val[9])));
                dto.setInvCurrProcessStateCd((nullToString(val[10])));
                dto.setInvDaycareIndCd((nullToString(val[11])));
                dto.setInvDetectionMethodCd((nullToString(val[12])));
                dto.setInvDiagnosisDt((nullToString(val[13])));
                dto.setInvDiseaseAcquiredLocCd((nullToString(val[14])));
                dto.setInvEffectiveTime((Timestamp) val[15]);
                dto.setInvFoodhandlerIndCd((nullToString(val[16])));
                dto.setInvHospitalizedAdmitDt((nullToString(val[17])));
                dto.setInvHospitalizedDischargeDt((nullToString(val[18])));
                dto.setInvHospitalizedIndCd((nullToString(val[19])));
                dto.setInvHospStayDuration(nullCheckInt(val[20]));
                dto.setInvIllnessStartDt((nullToString(val[21])));
                dto.setInvIllnessEndDt((nullToString(val[22])));
                dto.setInvIllnessDuration(nullCheckInt(val[23]));
                dto.setInvIllnessDurationUnitCd((nullToString(val[24])));
                dto.setInvIllnessOnsetAge(nullCheckInt(val[25]));
                dto.setInvIllnessOnsetAgeUnitCd((nullToString(val[26])));
                dto.setInvInvestigatorAssignedDt((nullToString(val[27])));
                dto.setInvImportCityTxt((nullToString(val[28])));
                dto.setInvImportCountyCd((nullToString(val[29])));
                dto.setInvImportCountryCd((nullToString(val[30])));
                dto.setInvImportStateCd((nullToString(val[31])));
                dto.setInvInfectiousFromDt((nullToString(val[32])));
                dto.setInvInfectiousToDt((nullToString(val[33])));
                dto.setInvLegacyCaseId((nullToString(val[34])));
                dto.setInvMmwrWeekTxt((nullToString(val[35])));
                dto.setInvMmwrYearTxt((nullToString(val[36])));
                dto.setInvOutbreakIndCd((nullToString(val[37])));
                dto.setInvOutbreakNameCd((nullToString(val[38])));
                dto.setInvPatientDeathDt((nullToString(val[39])));
                dto.setInvPatientDeathIndCd((nullToString(val[40])));
                dto.setInvPregnancyIndCd((nullToString(val[41])));
                dto.setInvReferralBasisCd((nullToString(val[42])));
                dto.setInvReportDt((nullToString(val[43])));
                dto.setInvReportToCountyDt((nullToString(val[44])));
                dto.setInvReportToStateDt((nullToString(val[45])));
                dto.setInvReportingCountyCd((nullToString(val[46])));
                dto.setInvSharedIndCd((nullToString(val[47])));
                dto.setInvSourceTypeCd((nullToString(val[48])));
                dto.setInvStartDt((nullToString(val[49])));
                dto.setInvStateId((nullToString(val[50])));
                dto.setInvStatusCd((nullToString(val[51])));
                dto.setInvTransmissionModeCd((nullToString(val[52])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgCaseParticipantDto> FetchMsgCaseParticipantForApplicableEcr(Integer containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_case_participant.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        List<EcrMsgCaseParticipantDto> dtos = new ArrayList<EcrMsgCaseParticipantDto>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val : results) {
                EcrMsgCaseParticipantDto dto = new EcrMsgCaseParticipantDto();
                dto.setMsgEventId((nullToString(val[0])));
                dto.setMsgEventType((nullToString(val[1])));
                dto.setAnswerTxt((nullToString(val[2])));
                dto.setAnswerLargeTxt((nullToString(val[3])));
                dto.setAnswerGroupSeqNbr(nullCheckInt(val[4]));
                dto.setPartTypeCd((nullToString(val[5])));
                dto.setQuestionIdentifier((nullToString(val[6])));
                dto.setQuestionGroupSeqNbr(nullCheckInt(val[7]));
                dto.setSeqNbr(nullCheckInt(val[8]));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgCaseAnswerDto> FetchMsgCaseAnswerForApplicableEcr(Integer containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_case_answer.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        List<EcrMsgCaseAnswerDto> dtos = new ArrayList<EcrMsgCaseAnswerDto>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object [] val : results) {
                EcrMsgCaseAnswerDto dto = new EcrMsgCaseAnswerDto();
                dto.setQuestionIdentifier((nullToString(val[0])));
                dto.setMsgContainerUid(((Number)val[1]).intValue());
                dto.setMsgEventId((nullToString(val[2])));
                dto.setMsgEventType((nullToString(val[3])));
                dto.setAnsCodeSystemCd((nullToString(val[4])));
                dto.setAnsCodeSystemDescTxt((nullToString(val[5])));
                dto.setAnsDisplayTxt((nullToString(val[6])));
                dto.setAnswerTxt((nullToString(val[7])));
                dto.setPartTypeCd((nullToString(val[8])));
                dto.setQuesCodeSystemCd((nullToString(val[9])));
                dto.setQuesCodeSystemDescTxt((nullToString(val[10])));
                dto.setQuesDisplayTxt((nullToString(val[11])));
                dto.setQuestionDisplayName((nullToString(val[12])));
                dto.setAnsToCode((nullToString(val[13])));
                dto.setAnsToCodeSystemCd((nullToString(val[14])));
                dto.setAnsToDisplayNm((nullToString(val[15])));
                dto.setCodeTranslationRequired((nullToString(val[16])));
                dto.setAnsToCodeSystemDescTxt((nullToString(val[17])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgCaseAnswerRepeatDto> FetchMsgCaseAnswerRepeatForApplicableEcr(Integer containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_case_answer_repeat.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        List<EcrMsgCaseAnswerRepeatDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgCaseAnswerRepeatDto dto = new EcrMsgCaseAnswerRepeatDto();
                dto.setQuestionIdentifier((nullToString(val[0])));
                dto.setMsgContainerUid(((Number)val[1]).intValue());
                dto.setMsgEventId((nullToString(val[2])));
                dto.setMsgEventType((nullToString(val[3])));
                dto.setAnsCodeSystemCd((nullToString(val[4])));
                dto.setAnsCodeSystemDescTxt((nullToString(val[5])));
                dto.setAnsDisplayTxt((nullToString(val[6])));
                dto.setAnswerTxt((nullToString(val[7])));
                dto.setPartTypeCd((nullToString(val[8])));
                dto.setQuesCodeSystemCd((nullToString(val[9])));
                dto.setQuesCodeSystemDescTxt((nullToString(val[10])));
                dto.setQuesDisplayTxt((nullToString(val[11])));
                dto.setQuestionDisplayName((nullToString(val[12])));
                dto.setAnsToCode((nullToString(val[13])));
                dto.setAnsToCodeSystemCd((nullToString(val[14])));
                dto.setAnsToDisplayNm((nullToString(val[15])));
                dto.setCodeTranslationRequired((nullToString(val[16])));
                dto.setAnsToCodeSystemDescTxt((nullToString(val[17])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgXmlAnswerDto> FetchMsgXmlAnswerForApplicableEcr(Integer containerId, String invLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_xml_answer.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("INV_LOCAL_ID", invLocalId);
        List<EcrMsgXmlAnswerDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgXmlAnswerDto dto = new EcrMsgXmlAnswerDto();
                dto.setDataType((nullToString(val[0])));
                dto.setAnswerXmlTxt((nullToString(val[1])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgProviderDto> FetchMsgProviderForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_provider.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgProviderDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgProviderDto dto = new EcrMsgProviderDto();
                dto.setPrvLocalId((nullToString(val[0])));
                dto.setPrvAuthorId((nullToString(val[1])));
                dto.setPrvAddrCityTxt((nullToString(val[2])));
                dto.setPrvAddrCommentTxt((nullToString(val[3])));
                dto.setPrvAddrCountyCd((nullToString(val[4])));
                dto.setPrvAddrCountryCd((nullToString(val[5])));
                dto.setPrvAddrStreetAddr1Txt((nullToString(val[6])));
                dto.setPrvAddrStreetAddr2Txt((nullToString(val[7])));
                dto.setPrvAddrStateCd((nullToString(val[8])));
                dto.setPrvAddrZipCodeTxt((nullToString(val[9])));
                dto.setPrvCommentTxt((nullToString(val[10])));
                dto.setPrvIdAltIdNbrTxt((nullToString(val[11])));
                dto.setPrvIdQuickCodeTxt((nullToString(val[12])));
                dto.setPrvIdNbrTxt((nullToString(val[13])));
                dto.setPrvIdNpiTxt((nullToString(val[14])));
                dto.setPrvEffectiveTime((Timestamp)val[15]);
                dto.setPrvEmailAddressTxt((nullToString(val[16])));
                dto.setPrvNameDegreeCd((nullToString(val[17])));
                dto.setPrvNameFirstTxt((nullToString(val[18])));
                dto.setPrvNameLastTxt((nullToString(val[19])));
                dto.setPrvNameMiddleTxt((nullToString(val[20])));
                dto.setPrvNamePrefixCd((nullToString(val[21])));
                dto.setPrvNameSuffixCd((nullToString(val[22])));
                dto.setPrvPhoneCommentTxt((nullToString(val[23])));
                dto.setPrvPhoneCountryCodeTxt((nullToString(val[24])));
                dto.setPrvPhoneExtensionTxt(nullCheckInt(val[25]));
                dto.setPrvPhoneNbrTxt((nullToString(val[26])));
                dto.setPrvRoleCd((nullToString(val[27])));
                dto.setPrvUrlAddressTxt((nullToString(val[28])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgOrganizationDto> FetchMsgOrganizationForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_organization.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgOrganizationDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {

            for(Object[] val: results) {
                EcrMsgOrganizationDto dto = new EcrMsgOrganizationDto();
                dto.setOrgLocalId((nullToString(val[0])));
                dto.setOrgAuthorId((nullToString(val[1])));
                dto.setOrgEffectiveTime((Timestamp)val[2]);
                dto.setOrgNameTxt((nullToString(val[3])));
                dto.setOrgAddrCityTxt((nullToString(val[4])));
                dto.setOrgAddrCommentTxt((nullToString(val[5])));
                dto.setOrgAddrCountyCd((nullToString(val[6])));
                dto.setOrgAddrCountryCd((nullToString(val[7])));
                dto.setOrgAddrStateCd((nullToString(val[8])));
                dto.setOrgAddrStreetAddr1Txt((nullToString(val[9])));
                dto.setOrgAddrStreetAddr2Txt((nullToString(val[10])));
                dto.setOrgAddrZipCodeTxt((nullToString(val[11])));
                dto.setOrgClassCd((nullToString(val[12])));
                dto.setOrgCommentTxt((nullToString(val[13])));
                dto.setOrgEmailAddressTxt((nullToString(val[14])));
                dto.setOrgIdCliaNbrTxt((nullToString(val[15])));
                dto.setOrgIdFacilityIdentifierTxt((nullToString(val[16])));
                dto.setOrgIdQuickCodeTxt((nullToString(val[17])));
                dto.setOrgPhoneCommentTxt((nullToString(val[18])));
                dto.setOrgPhoneCountryCodeTxt((nullToString(val[19])));
                dto.setOrgPhoneExtensionTxt(nullCheckInt(val[20]));
                dto.setOrgPhoneNbrTxt((nullToString(val[21])));
                dto.setOrgRoleCd((nullToString(val[22])));
                dto.setOrgUrlAddressTxt((nullToString(val[23])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgPlaceDto> FetchMsgPlaceForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_place.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgPlaceDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgPlaceDto dto = new EcrMsgPlaceDto();
                dto.setMsgContainerUid(((Number)val[0]).intValue());
                dto.setPlaLocalId((nullToString(val[1])));
                dto.setPlaAuthorId((nullToString(val[2])));
                dto.setPlaEffectiveTime((Timestamp)val[3]);
                dto.setPlaAddrAsOfDt((Timestamp)val[4]);
                dto.setPlaAddrCityTxt((nullToString(val[5])));
                dto.setPlaAddrCountyCd((nullToString(val[6])));
                dto.setPlaAddrCountryCd((nullToString(val[7])));
                dto.setPlaAddrStateCd((nullToString(val[8])));
                dto.setPlaAddrStreetAddr1Txt((nullToString(val[9])));
                dto.setPlaAddrStreetAddr2Txt((nullToString(val[10])));
                dto.setPlaAddrZipCodeTxt((nullToString(val[11])));
                dto.setPlaAddrCommentTxt((nullToString(val[12])));
                dto.setPlaCensusTractTxt((nullToString(val[13])));
                dto.setPlaCommentTxt((nullToString(val[14])));
                dto.setPlaEmailAddressTxt((nullToString(val[15])));
                dto.setPlaIdQuickCode((nullToString(val[16])));
                dto.setPlaNameTxt((nullToString(val[17])));
                dto.setPlaPhoneAsOfDt((Timestamp)val[18]);
                dto.setPlaPhoneCountryCodeTxt((nullToString(val[19])));
                dto.setPlaPhoneExtensionTxt((nullToString(val[20])));
                dto.setPlaPhoneNbrTxt((nullToString(val[21])));
                dto.setPlaPhoneCommentTxt((nullToString(val[22])));
                dto.setPlaTypeCd((nullToString(val[23])));
                dto.setPlaUrlAddressTxt((nullToString(val[24])));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<EcrMsgInterviewDto> FetchMsgInterviewForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_interview.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgInterviewDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgInterviewDto dto = new EcrMsgInterviewDto();
                dto.setMsgContainerUid(((Number)val[0]).intValue());
                dto.setIxsLocalId((nullToString(val[1])));
                dto.setIxsIntervieweeId((nullToString(val[2])));
                dto.setIxsAuthorId((nullToString(val[3])));
                dto.setIxsEffectiveTime((Timestamp)val[4]);
                dto.setIxsInterviewDt((Timestamp)val[5]);
                dto.setIxsInterviewLocCd((nullToString(val[6])));
                dto.setIxsIntervieweeRoleCd((nullToString(val[7])));
                dto.setIxsInterviewTypeCd((nullToString(val[8])));
                dto.setIxsStatusCd((nullToString(val[9])));
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public List<EcrMsgInterviewProviderDto> FetchMsgInterviewProviderForApplicableEcr(Integer containerId,  String ixsLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_interview_provider.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("IXS_LOCAL_ID", ixsLocalId);
        List<EcrMsgInterviewProviderDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgInterviewProviderDto dto = new EcrMsgInterviewProviderDto();
                dto.setPrvLocalId((nullToString(val[0])));
                dto.setPrvAuthorId((nullToString(val[1])));
                dto.setPrvAddrCityTxt((nullToString(val[2])));
                dto.setPrvAddrCommentTxt((nullToString(val[3])));
                dto.setPrvAddrCountyCd((nullToString(val[4])));
                dto.setPrvAddrCountryCd((nullToString(val[5])));
                dto.setPrvAddrStreetAddr1Txt((nullToString(val[6])));
                dto.setPrvAddrStreetAddr2Txt((nullToString(val[7])));
                dto.setPrvAddrStateCd((nullToString(val[8])));
                dto.setPrvAddrZipCodeTxt((nullToString(val[9])));
                dto.setPrvCommentTxt((nullToString(val[10])));
                dto.setPrvIdAltIdNbrTxt((nullToString(val[11])));
                dto.setPrvIdQuickCodeTxt((nullToString(val[12])));
                dto.setPrvIdNbrTxt((nullToString(val[13])));
                dto.setPrvIdNpiTxt((nullToString(val[14])));
                dto.setPrvEffectiveTime((Timestamp)val[15]);
                dto.setPrvEmailAddressTxt((nullToString(val[16])));
                dto.setPrvNameDegreeCd((nullToString(val[17])));
                dto.setPrvNameFirstTxt((nullToString(val[18])));
                dto.setPrvNameLastTxt((nullToString(val[19])));
                dto.setPrvNameMiddleTxt((nullToString(val[20])));
                dto.setPrvNamePrefixCd((nullToString(val[21])));
                dto.setPrvNameSuffixCd((nullToString(val[22])));
                dto.setPrvPhoneCommentTxt((nullToString(val[23])));
                dto.setPrvPhoneCountryCodeTxt((nullToString(val[24])));
                dto.setPrvPhoneExtensionTxt(nullCheckInt(val[25]));
                dto.setPrvPhoneNbrTxt((nullToString(val[26])));
                dto.setPrvRoleCd((nullToString(val[27])));
                dto.setPrvUrlAddressTxt((nullToString(val[28])));
                dtos.add(dto);
            }
        }
        return dtos;
    }


    public List<EcrMsgInterviewAnswerDto> FetchMsgInterviewAnswerForApplicableEcr(Integer containerId, String ixsLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_interview_answer.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("IXS_LOCAL_ID", ixsLocalId);
        List<EcrMsgInterviewAnswerDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgInterviewAnswerDto dto = new EcrMsgInterviewAnswerDto();
                dto.setQuestionIdentifier((nullToString(val[0])));
                dto.setMsgContainerUid(((Number)val[1]).intValue());
                dto.setMsgEventId((nullToString(val[2])));
                dto.setMsgEventType((nullToString(val[3])));
                dto.setAnsCodeSystemCd((nullToString(val[4])));
                dto.setAnsCodeSystemDescTxt((nullToString(val[5])));
                dto.setAnsDisplayTxt((nullToString(val[6])));
                dto.setAnswerTxt((nullToString(val[7])));
                dto.setPartTypeCd((nullToString(val[8])));
                dto.setQuesCodeSystemCd((nullToString(val[9])));
                dto.setQuesCodeSystemDescTxt((nullToString(val[10])));
                dto.setQuesDisplayTxt((nullToString(val[11])));
                dto.setQuestionDisplayName((nullToString(val[12])));
                dto.setAnsToCode((nullToString(val[13])));
                dto.setAnsToCodeSystemCd((nullToString(val[14])));
                dto.setAnsToDisplayNm((nullToString(val[15])));
                dto.setCodeTranslationRequired((nullToString(val[16])));
                dto.setAnsToCodeSystemDescTxt((nullToString(val[17])));
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public List<EcrMsgInterviewAnswerRepeatDto> FetchMsgInterviewAnswerRepeatForApplicableEcr(Integer containerId, String ixsLocalId) {
        String queryString = loadSqlFromFile("ecr_msg_interview_answer_repeat.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        query.setParameter("IXS_LOCAL_ID", ixsLocalId);
        List<EcrMsgInterviewAnswerRepeatDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgInterviewAnswerRepeatDto dto = new EcrMsgInterviewAnswerRepeatDto();
                dto.setQuestionIdentifier((nullToString(val[0])));
                dto.setMsgContainerUid(((Number)val[1]).intValue());
                dto.setMsgEventId((nullToString(val[2])));
                dto.setMsgEventType((nullToString(val[3])));
                dto.setAnsCodeSystemCd((nullToString(val[4])));
                dto.setAnsCodeSystemDescTxt((nullToString(val[5])));
                dto.setAnsDisplayTxt((nullToString(val[6])));
                dto.setAnswerTxt((nullToString(val[7])));
                dto.setPartTypeCd((nullToString(val[8])));
                dto.setQuesCodeSystemCd((nullToString(val[9])));
                dto.setQuesCodeSystemDescTxt((nullToString(val[10])));
                dto.setQuesDisplayTxt((nullToString(val[11])));
                dto.setQuestionDisplayName((nullToString(val[12])));
                dto.setAnsToCode((nullToString(val[13])));
                dto.setAnsToCodeSystemCd((nullToString(val[14])));
                dto.setAnsToDisplayNm((nullToString(val[15])));
                dto.setCodeTranslationRequired((nullToString(val[16])));
                dto.setAnsToCodeSystemDescTxt((nullToString(val[17])));
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public List<EcrMsgTreatmentDto> FetchMsgTreatmentForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_treatment.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgTreatmentDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgTreatmentDto dto = new EcrMsgTreatmentDto();
                dto.setTrtLocalId((nullToString(val[0])));
                dto.setTrtAuthorId((nullToString(val[1])));
                dto.setTrtCompositeCd((nullToString(val[2])));
                dto.setTrtCommentTxt((nullToString(val[3])));
                dto.setTrtCustomTreatmentTxt((nullToString(val[4])));
                dto.setTrtDosageAmt(nullCheckInt(val[5]));
                dto.setTrtDosageUnitCd((nullToString(val[6])));
                dto.setTrtDrugCd((nullToString(val[7])));
                dto.setTrtDurationAmt(nullCheckInt(val[8]));
                dto.setTrtDurationUnitCd((nullToString(val[9])));
                dto.setTrtEffectiveTime((Timestamp) val[10]);
                dto.setTrtFrequencyAmtCd((nullToString(val[11])));
                dto.setTrtRouteCd((nullToString(val[12])));
                dto.setTrtTreatmentDt((Timestamp) val[13]);
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public List<EcrMsgTreatmentProviderDto> FetchMsgTreatmentProviderForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_treatment_provider.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgTreatmentProviderDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgTreatmentProviderDto dto = new EcrMsgTreatmentProviderDto();
                dto.setPrvLocalId((nullToString(val[0])));
                dto.setPrvAuthorId((nullToString(val[1])));
                dto.setPrvAddrCityTxt((nullToString(val[2])));
                dto.setPrvAddrCommentTxt((nullToString(val[3])));
                dto.setPrvAddrCountyCd((nullToString(val[4])));
                dto.setPrvAddrCountryCd((nullToString(val[5])));
                dto.setPrvAddrStreetAddr1Txt((nullToString(val[6])));
                dto.setPrvAddrStreetAddr2Txt((nullToString(val[7])));
                dto.setPrvAddrStateCd((nullToString(val[8])));
                dto.setPrvAddrZipCodeTxt((nullToString(val[9])));
                dto.setPrvCommentTxt((nullToString(val[10])));
                dto.setPrvIdAltIdNbrTxt((nullToString(val[11])));
                dto.setPrvIdQuickCodeTxt((nullToString(val[12])));
                dto.setPrvIdNbrTxt((nullToString(val[13])));
                dto.setPrvIdNpiTxt((nullToString(val[14])));
                dto.setPrvEffectiveTime((Timestamp) val[15]);
                dto.setPrvEmailAddressTxt((nullToString(val[16])));
                dto.setPrvNameDegreeCd((nullToString(val[17])));
                dto.setPrvNameFirstTxt((nullToString(val[18])));
                dto.setPrvNameLastTxt((nullToString(val[19])));
                dto.setPrvNameMiddleTxt((nullToString(val[20])));
                dto.setPrvNamePrefixCd((nullToString(val[21])));
                dto.setPrvNameSuffixCd((nullToString(val[22])));
                dto.setPrvPhoneCommentTxt((nullToString(val[23])));
                dto.setPrvPhoneCountryCodeTxt((nullToString(val[24])));
                dto.setPrvPhoneExtensionTxt(nullCheckInt(val[25]));
                dto.setPrvPhoneNbrTxt((nullToString(val[26])));
                dto.setPrvRoleCd((nullToString(val[27])));
                dto.setPrvUrlAddressTxt((nullToString(val[28])));
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public List<EcrMsgTreatmentOrganizationDto> FetchMsgTreatmentOrganizationForApplicableEcr(Integer containerId) {
        String queryString = loadSqlFromFile("ecr_msg_treatment_organization.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerId);
        List<EcrMsgTreatmentOrganizationDto> dtos = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(Object[] val: results) {
                EcrMsgTreatmentOrganizationDto dto = new EcrMsgTreatmentOrganizationDto();
                dto.setOrgLocalId((nullToString(val[0])));
                dto.setOrgAuthorId((nullToString(val[1])));
                dto.setOrgEffectiveTime((Timestamp) val[2]);
                dto.setOrgNameTxt((nullToString(val[3])));
                dto.setOrgAddrCityTxt((nullToString(val[4])));
                dto.setOrgAddrCommentTxt((nullToString(val[5])));
                dto.setOrgAddrCountyCd((nullToString(val[6])));
                dto.setOrgAddrCountryCd((nullToString(val[7])));
                dto.setOrgAddrStateCd((nullToString(val[8])));
                dto.setOrgAddrStreetAddr1Txt((nullToString(val[9])));
                dto.setOrgAddrStreetAddr2Txt((nullToString(val[10])));
                dto.setOrgAddrZipCodeTxt((nullToString(val[11])));
                dto.setOrgClassCd((nullToString(val[12])));
                dto.setOrgCommentTxt((nullToString(val[13])));
                dto.setOrgEmailAddressTxt((nullToString(val[14])));
                dto.setOrgIdCliaNbrTxt((nullToString(val[15])));
                dto.setOrgIdFacilityIdentifierTxt((nullToString(val[16])));
                dto.setOrgIdQuickCodeTxt((nullToString(val[17])));
                dto.setOrgPhoneCommentTxt((nullToString(val[18])));
                dto.setOrgPhoneCountryCodeTxt((nullToString(val[19])));
                dto.setOrgPhoneExtensionTxt(nullCheckInt(val[20]));
                dto.setOrgPhoneNbrTxt((nullToString(val[21])));
                dto.setOrgRoleCd((nullToString(val[22])));
                dto.setOrgUrlAddressTxt((nullToString(val[23])));
                dtos.add(dto);
            }

        }
        return dtos;
    }

    @Transactional(transactionManager = "nbsTransactionManager")
    public void UpdateMatchEcrRecordForProcessing(Integer containerUid) {
        String queryString = loadSqlFromFile("ecr_msg_container_update_match_record.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("MSG_CONTAINER_UID", containerUid);
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
        return obj != null ? String.valueOf(obj) : null;
    }

    private Integer nullCheckInt(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return ((Number)obj).intValue();
        }
    }
}
