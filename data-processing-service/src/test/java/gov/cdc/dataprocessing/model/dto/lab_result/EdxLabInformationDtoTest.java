package gov.cdc.dataprocessing.model.dto.lab_result;


import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.service.model.wds.WdsReport;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EdxLabInformationDtoTest {

    @Test
    void testGettersAndSetters() {
        EdxLabInformationDto dto = new EdxLabInformationDto();

        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Timestamp orderEffectiveDate = new Timestamp(System.currentTimeMillis() + 1000);
        String role = "role";
        long rootObservationUid = 12345L;
        PersonContainer orderingProviderVO = new PersonContainer();
        String sendingFacilityClia = "sendingFacilityClia";
        String sendingFacilityName = "sendingFacilityName";
        long patientUid = 54321L;
        long userId = 11111L;
        int nextUid = 5;
        String fillerNumber = "fillerNumber";
        String messageControlID = "messageControlID";
        long parentObservationUid = 22222L;
        boolean isOrderingProvider = true;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        String localId = "localId";
        boolean isParentObsInd = true;
        Collection<EdxLabIdentiferDto> edxLabIdentiferDTColl = new ArrayList<>();
        String entityName = "entityName";
        String reportingSourceName = "reportingSourceName";
        String userName = "userName";
        String universalIdType = "universalIdType";
        Long associatedPublicHealthCaseUid = 33333L;
        long publicHealthCaseUid = 44444L;
        long notificationUid = 55555L;
        long originalAssociatedPHCUid = 66666L;
        long nbsInterfaceUid = 77777L;
        Timestamp specimenCollectionTime = new Timestamp(System.currentTimeMillis() + 2000);
        String jurisdictionName = "jurisdictionName";
        String programAreaName = "programAreaName";
        boolean jurisdictionAndProgramAreaSuccessfullyDerived = true;
        boolean algorithmHasInvestigation = true;
        boolean investigationSuccessfullyCreated = true;
        boolean investigationMissingFields = false;
        boolean algorithmHasNotification = true;
        boolean notificationSuccessfullyCreated = true;
        boolean notificationMissingFields = false;
        boolean labIsCreate = true;
        boolean labIsCreateSuccess = true;
        boolean labIsUpdateDRRQ = true;
        boolean labIsUpdateDRSA = true;
        boolean labIsUpdateSuccess = true;
        boolean labIsMarkedAsReviewed = true;
        Map<Object, Object> resultedTest = new HashMap<>();
        String conditionCode = "conditionCode";
        Object proxyVO = new Object();
        Map<Object, Object> edxSusLabDTMap = new HashMap<>();
        String addReasonCd = "addReasonCd";
        ObservationContainer rootObservationContainer = new ObservationContainer();
        boolean multipleSubjectMatch = true;
        boolean multipleOrderingProvider = true;
        boolean multipleCollector = true;
        boolean multiplePrincipalInterpreter = true;
        boolean multipleOrderingFacility = true;
        boolean multipleSpecimen = true;
        boolean ethnicityCodeTranslated = true;
        boolean obsMethodTranslated = true;
        boolean raceTranslated = true;
        boolean sexTranslated = true;
        boolean ssnInvalid = true;
        boolean nullClia = true;
        boolean nextOfKin = true;
        boolean isProvider = true;
        boolean fillerNumberPresent = true;
        boolean finalPostCorrected = true;
        boolean preliminaryPostFinal = true;
        boolean preliminaryPostCorrected = true;
        boolean activityTimeOutOfSequence = true;
        boolean multiplePerformingLab = true;
        boolean orderTestNameMissing = true;
        boolean reflexOrderedTestCdMissing = true;
        boolean reflexResultedTestCdMissing = true;
        boolean resultedTestNameMissing = true;
        boolean drugNameMissing = true;
        boolean obsStatusTranslated = true;
        String dangerCode = "dangerCode";
        String relationship = "relationship";
        String relationshipDesc = "relationshipDesc";
        boolean activityToTimeMissing = true;
        boolean systemException = true;
        boolean universalServiceIdMissing = true;
        boolean missingOrderingProvider = true;
        boolean missingOrderingFacility = true;
        boolean multipleReceivingFacility = true;
        long personParentUid = 88888L;
        boolean patientMatch = true;
        boolean multipleOBR = true;
        boolean multipleSubject = true;
        boolean noSubject = true;
        boolean orderOBRWithParent = true;
        boolean childOBRWithoutParent = true;
        boolean invalidXML = true;
        boolean missingOrderingProviderandFacility = true;
        boolean createLabPermission = true;
        boolean updateLabPermission = true;
        boolean markAsReviewPermission = true;
        boolean createInvestigationPermission = true;
        boolean createNotificationPermission = true;
        boolean matchingAlgorithm = true;
        boolean unexpectedResultType = true;
        boolean childSuscWithoutParentResult = true;
        boolean fieldTruncationError = true;
        boolean invalidDateError = true;
        String algorithmAndOrLogic = "algorithmAndOrLogic";
        boolean labAssociatedToInv = true;
        boolean observationMatch = true;
        boolean reasonforStudyCdMissing = true;
        Collection<PublicHealthCaseDto> matchingPublicHealthCaseDtoColl = new ArrayList<>();
        String investigationType = "investigationType";
        NbsInterfaceStatus status = NbsInterfaceStatus.Failure;
        List<WdsReport> wdsReports = new ArrayList<>();

        dto.setAddTime(addTime);
        dto.setOrderEffectiveDate(orderEffectiveDate);
        dto.setRole(role);
        dto.setRootObserbationUid(rootObservationUid);
        dto.setOrderingProviderVO(orderingProviderVO);
        dto.setSendingFacilityClia(sendingFacilityClia);
        dto.setSendingFacilityName(sendingFacilityName);
        dto.setPatientUid(patientUid);
        dto.setUserId(userId);
        dto.setNextUid(nextUid);
        dto.setFillerNumber(fillerNumber);
        dto.setMessageControlID(messageControlID);
        dto.setParentObservationUid(parentObservationUid);
        dto.setOrderingProvider(isOrderingProvider);
        dto.setLabResultProxyContainer(labResultProxyContainer);
        dto.setLocalId(localId);
        dto.setParentObsInd(isParentObsInd);
        dto.setEdxLabIdentiferDTColl(edxLabIdentiferDTColl);
        dto.setEntityName(entityName);
        dto.setReportingSourceName(reportingSourceName);
        dto.setUserName(userName);
        dto.setUniversalIdType(universalIdType);
        dto.setAssociatedPublicHealthCaseUid(associatedPublicHealthCaseUid);
        dto.setPublicHealthCaseUid(publicHealthCaseUid);
        dto.setNotificationUid(notificationUid);
        dto.setOriginalAssociatedPHCUid(originalAssociatedPHCUid);
        dto.setNbsInterfaceUid(nbsInterfaceUid);
        dto.setSpecimenCollectionTime(specimenCollectionTime);
        dto.setJurisdictionName(jurisdictionName);
        dto.setProgramAreaName(programAreaName);
        dto.setJurisdictionAndProgramAreaSuccessfullyDerived(jurisdictionAndProgramAreaSuccessfullyDerived);
        dto.setAlgorithmHasInvestigation(algorithmHasInvestigation);
        dto.setInvestigationSuccessfullyCreated(investigationSuccessfullyCreated);
        dto.setInvestigationMissingFields(investigationMissingFields);
        dto.setAlgorithmHasNotification(algorithmHasNotification);
        dto.setNotificationSuccessfullyCreated(notificationSuccessfullyCreated);
        dto.setNotificationMissingFields(notificationMissingFields);
        dto.setLabIsCreate(labIsCreate);
        dto.setLabIsCreateSuccess(labIsCreateSuccess);
        dto.setLabIsUpdateDRRQ(labIsUpdateDRRQ);
        dto.setLabIsUpdateDRSA(labIsUpdateDRSA);
        dto.setLabIsUpdateSuccess(labIsUpdateSuccess);
        dto.setLabIsMarkedAsReviewed(labIsMarkedAsReviewed);
        dto.setResultedTest(resultedTest);
        dto.setConditionCode(conditionCode);
        dto.setProxyVO(proxyVO);
        dto.setEdxSusLabDTMap(edxSusLabDTMap);
        dto.setAddReasonCd(addReasonCd);
        dto.setRootObservationContainer(rootObservationContainer);
        dto.setMultipleSubjectMatch(multipleSubjectMatch);
        dto.setMultipleOrderingProvider(multipleOrderingProvider);
        dto.setMultipleCollector(multipleCollector);
        dto.setMultiplePrincipalInterpreter(multiplePrincipalInterpreter);
        dto.setMultipleOrderingFacility(multipleOrderingFacility);
        dto.setMultipleSpecimen(multipleSpecimen);
        dto.setEthnicityCodeTranslated(ethnicityCodeTranslated);
        dto.setObsMethodTranslated(obsMethodTranslated);
        dto.setRaceTranslated(raceTranslated);
        dto.setSexTranslated(sexTranslated);
        dto.setSsnInvalid(ssnInvalid);
        dto.setNullClia(nullClia);
        dto.setNextOfKin(nextOfKin);
        dto.setProvider(isProvider);
        dto.setFillerNumberPresent(fillerNumberPresent);
        dto.setFinalPostCorrected(finalPostCorrected);
        dto.setPreliminaryPostFinal(preliminaryPostFinal);
        dto.setPreliminaryPostCorrected(preliminaryPostCorrected);
        dto.setActivityTimeOutOfSequence(activityTimeOutOfSequence);
        dto.setMultiplePerformingLab(multiplePerformingLab);
        dto.setOrderTestNameMissing(orderTestNameMissing);
        dto.setReflexOrderedTestCdMissing(reflexOrderedTestCdMissing);
        dto.setReflexResultedTestCdMissing(reflexResultedTestCdMissing);
        dto.setResultedTestNameMissing(resultedTestNameMissing);
        dto.setDrugNameMissing(drugNameMissing);
        dto.setObsStatusTranslated(obsStatusTranslated);
        dto.setDangerCode(dangerCode);
        dto.setRelationship(relationship);
        dto.setRelationshipDesc(relationshipDesc);
        dto.setActivityToTimeMissing(activityToTimeMissing);
        dto.setSystemException(systemException);
        dto.setUniversalServiceIdMissing(universalServiceIdMissing);
        dto.setMissingOrderingProvider(missingOrderingProvider);
        dto.setMissingOrderingFacility(missingOrderingFacility);
        dto.setMultipleReceivingFacility(multipleReceivingFacility);
        dto.setPersonParentUid(personParentUid);
        dto.setPatientMatch(patientMatch);
        dto.setMultipleOBR(multipleOBR);
        dto.setMultipleSubject(multipleSubject);
        dto.setNoSubject(noSubject);
        dto.setOrderOBRWithParent(orderOBRWithParent);
        dto.setChildOBRWithoutParent(childOBRWithoutParent);
        dto.setInvalidXML(invalidXML);
        dto.setMissingOrderingProviderandFacility(missingOrderingProviderandFacility);
        dto.setCreateLabPermission(createLabPermission);
        dto.setUpdateLabPermission(updateLabPermission);
        dto.setMarkAsReviewPermission(markAsReviewPermission);
        dto.setCreateInvestigationPermission(createInvestigationPermission);
        dto.setCreateNotificationPermission(createNotificationPermission);
        dto.setMatchingAlgorithm(matchingAlgorithm);
        dto.setUnexpectedResultType(unexpectedResultType);
        dto.setChildSuscWithoutParentResult(childSuscWithoutParentResult);
        dto.setFieldTruncationError(fieldTruncationError);
        dto.setInvalidDateError(invalidDateError);
        dto.setAlgorithmAndOrLogic(algorithmAndOrLogic);
        dto.setLabAssociatedToInv(labAssociatedToInv);
        dto.setObservationMatch(observationMatch);
        dto.setReasonforStudyCdMissing(reasonforStudyCdMissing);
        dto.setMatchingPublicHealthCaseDtoColl(matchingPublicHealthCaseDtoColl);
        dto.setInvestigationType(investigationType);
        dto.setStatus(status);
        dto.setWdsReports(wdsReports);

        assertEquals(addTime, dto.getAddTime());
        assertEquals(orderEffectiveDate, dto.getOrderEffectiveDate());
        assertEquals(role, dto.getRole());
        assertEquals(rootObservationUid, dto.getRootObserbationUid());
        assertEquals(orderingProviderVO, dto.getOrderingProviderVO());
        assertEquals(sendingFacilityClia, dto.getSendingFacilityClia());
        assertEquals(sendingFacilityName, dto.getSendingFacilityName());
        assertEquals(patientUid, dto.getPatientUid());
        assertEquals(userId, dto.getUserId());
        assertEquals(4, dto.getNextUid());
        assertEquals(fillerNumber, dto.getFillerNumber());
        assertEquals(messageControlID, dto.getMessageControlID());
        assertEquals(parentObservationUid, dto.getParentObservationUid());
        assertTrue(dto.isOrderingProvider());
        assertEquals(labResultProxyContainer, dto.getLabResultProxyContainer());
        assertEquals(localId, dto.getLocalId());
        assertTrue(dto.isParentObsInd());
        assertEquals(edxLabIdentiferDTColl, dto.getEdxLabIdentiferDTColl());
        assertEquals(entityName, dto.getEntityName());
        assertEquals(reportingSourceName, dto.getReportingSourceName());
        assertEquals(userName, dto.getUserName());
        assertEquals(universalIdType, dto.getUniversalIdType());
        assertEquals(associatedPublicHealthCaseUid, dto.getAssociatedPublicHealthCaseUid());
        assertEquals(publicHealthCaseUid, dto.getPublicHealthCaseUid());
        assertEquals(notificationUid, dto.getNotificationUid());
        assertEquals(originalAssociatedPHCUid, dto.getOriginalAssociatedPHCUid());
        assertEquals(nbsInterfaceUid, dto.getNbsInterfaceUid());
        assertEquals(specimenCollectionTime, dto.getSpecimenCollectionTime());
        assertEquals(jurisdictionName, dto.getJurisdictionName());
        assertEquals(programAreaName, dto.getProgramAreaName());
        assertTrue(dto.isJurisdictionAndProgramAreaSuccessfullyDerived());
        assertTrue(dto.isAlgorithmHasInvestigation());
        assertTrue(dto.isInvestigationSuccessfullyCreated());
        assertFalse(dto.isInvestigationMissingFields());
        assertTrue(dto.isAlgorithmHasNotification());
        assertTrue(dto.isNotificationSuccessfullyCreated());
        assertFalse(dto.isNotificationMissingFields());
        assertTrue(dto.isLabIsCreate());
        assertTrue(dto.isLabIsCreateSuccess());
        assertTrue(dto.isLabIsUpdateDRRQ());
        assertTrue(dto.isLabIsUpdateDRSA());
        assertTrue(dto.isLabIsUpdateSuccess());
        assertTrue(dto.isLabIsMarkedAsReviewed());
        assertEquals(resultedTest, dto.getResultedTest());
        assertEquals(conditionCode, dto.getConditionCode());
        assertEquals(proxyVO, dto.getProxyVO());
        assertEquals(edxSusLabDTMap, dto.getEdxSusLabDTMap());
        assertEquals(addReasonCd, dto.getAddReasonCd());
        assertEquals(rootObservationContainer, dto.getRootObservationContainer());
        assertTrue(dto.isMultipleSubjectMatch());
        assertTrue(dto.isMultipleOrderingProvider());
        assertTrue(dto.isMultipleCollector());
        assertTrue(dto.isMultiplePrincipalInterpreter());
        assertTrue(dto.isMultipleOrderingFacility());
        assertTrue(dto.isMultipleSpecimen());
        assertTrue(dto.isEthnicityCodeTranslated());
        assertTrue(dto.isObsMethodTranslated());
        assertTrue(dto.isRaceTranslated());
        assertTrue(dto.isSexTranslated());
        assertTrue(dto.isSsnInvalid());
        assertTrue(dto.isNullClia());
        assertTrue(dto.isNextOfKin());
        assertTrue(dto.isProvider());
        assertTrue(dto.isFillerNumberPresent());
        assertTrue(dto.isFinalPostCorrected());
        assertTrue(dto.isPreliminaryPostFinal());
        assertTrue(dto.isPreliminaryPostCorrected());
        assertTrue(dto.isActivityTimeOutOfSequence());
        assertTrue(dto.isMultiplePerformingLab());
        assertTrue(dto.isOrderTestNameMissing());
        assertTrue(dto.isReflexOrderedTestCdMissing());
        assertTrue(dto.isReflexResultedTestCdMissing());
        assertTrue(dto.isResultedTestNameMissing());
        assertTrue(dto.isDrugNameMissing());
        assertTrue(dto.isObsStatusTranslated());
        assertEquals(dangerCode, dto.getDangerCode());
        assertEquals(relationship, dto.getRelationship());
        assertEquals(relationshipDesc, dto.getRelationshipDesc());
        assertTrue(dto.isActivityToTimeMissing());
        assertTrue(dto.isSystemException());
        assertTrue(dto.isUniversalServiceIdMissing());
        assertTrue(dto.isMissingOrderingProvider());
        assertTrue(dto.isMissingOrderingFacility());
        assertTrue(dto.isMultipleReceivingFacility());
        assertEquals(personParentUid, dto.getPersonParentUid());
        assertTrue(dto.isPatientMatch());
        assertTrue(dto.isMultipleOBR());
        assertTrue(dto.isMultipleSubject());
        assertTrue(dto.isNoSubject());
        assertTrue(dto.isOrderOBRWithParent());
        assertTrue(dto.isChildOBRWithoutParent());
        assertTrue(dto.isInvalidXML());
        assertTrue(dto.isMissingOrderingProviderandFacility());
        assertTrue(dto.isCreateLabPermission());
        assertTrue(dto.isUpdateLabPermission());
        assertTrue(dto.isMarkAsReviewPermission());
        assertTrue(dto.isCreateInvestigationPermission());
        assertTrue(dto.isCreateNotificationPermission());
        assertTrue(dto.isMatchingAlgorithm());
        assertTrue(dto.isUnexpectedResultType());
        assertTrue(dto.isChildSuscWithoutParentResult());
        assertTrue(dto.isFieldTruncationError());
        assertTrue(dto.isInvalidDateError());
        assertEquals(algorithmAndOrLogic, dto.getAlgorithmAndOrLogic());
        assertTrue(dto.isLabAssociatedToInv());
        assertTrue(dto.isObservationMatch());
        assertTrue(dto.isReasonforStudyCdMissing());
        assertEquals(matchingPublicHealthCaseDtoColl, dto.getMatchingPublicHealthCaseDtoColl());
        assertEquals(investigationType, dto.getInvestigationType());
        assertEquals(status, dto.getStatus());
        assertEquals(wdsReports, dto.getWdsReports());
    }

    @Test
    void testNextUid() {
        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setNextUid(10);
        assertEquals(9, dto.getNextUid());
        assertEquals(8, dto.getNextUid());
        assertEquals(7, dto.getNextUid());
    }
}