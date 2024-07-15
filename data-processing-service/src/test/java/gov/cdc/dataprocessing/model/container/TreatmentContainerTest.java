package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.TreatmentContainer;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreatmentContainerTest {

    @Test
    void testGettersAndSetters() {
        TreatmentContainer container = new TreatmentContainer();

        String arTypeCd = "arTypeCd";
        String index = "index";
        Long personUid = 1L;
        String yesNoFlag = "yesNoFlag";
        String treatmentNameCode = "treatmentNameCode";
        String customTreatmentNameCode = "customTreatmentNameCode";
        String treatmentAdministered = "treatmentAdministered";
        Long treatmentUid = 2L;
        Long uid = 3L;
        String localId = "localId";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis() + 1000);
        String recordStatusCd = "recordStatusCd";
        Long phcUid = 4L;
        Long parentUid = 5L;
        Collection<Object> morbReportSummaryVOColl = new ArrayList<>();
        boolean isTouched = true;
        boolean isAssociated = false;
        Character isRadioBtnAssociated = 'Y';
        String actionLink = "actionLink";
        String checkBoxId = "checkBoxId";
        Timestamp createDate = new Timestamp(System.currentTimeMillis() - 2000);
        Map<Object, Object> associationMap = Map.of();
        String providerFirstName = "providerFirstName";
        Long nbsDocumentUid = 6L;
        String providerLastName = "providerLastName";
        String providerSuffix = "providerSuffix";
        String providerPrefix = "providerPrefix";
        String degree = "degree";

        Long lastChgUserId = 7L;
        String jurisdictionCd = "jurisdictionCd";
        String progAreaCd = "progAreaCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis() - 3000);
        Long addUserId = 8L;
        String lastChgReasonCd = "lastChgReasonCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis() - 4000);
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis() - 5000);
        Long programJurisdictionOid = 9L;
        String sharedInd = "sharedInd";
        Integer versionCtrlNbr = 10;
        Timestamp addTime = new Timestamp(System.currentTimeMillis() - 6000);

        container.setArTypeCd(arTypeCd);
        container.setIndex(index);
        container.setPersonUid(personUid);
        container.setYesNoFlag(yesNoFlag);
        container.setTreatmentNameCode(treatmentNameCode);
        container.setCustomTreatmentNameCode(customTreatmentNameCode);
        container.setTreatmentAdministered(treatmentAdministered);
        container.setTreatmentUid(treatmentUid);
        container.setUid(uid);
        container.setLocalId(localId);
        container.setActivityFromTime(activityFromTime);
        container.setActivityToTime(activityToTime);
        container.setRecordStatusCd(recordStatusCd);
        container.setPhcUid(phcUid);
        container.setParentUid(parentUid);
        container.setMorbReportSummaryVOColl(morbReportSummaryVOColl);
        container.setTouched(isTouched);
        container.setAssociated(isAssociated);
        container.setIsRadioBtnAssociated(isRadioBtnAssociated);
        container.setActionLink(actionLink);
        container.setCheckBoxId(checkBoxId);
        container.setCreateDate(createDate);
        container.setAssociationMap(associationMap);
        container.setProviderFirstName(providerFirstName);
        container.setNbsDocumentUid(nbsDocumentUid);
        container.setProviderLastName(providerLastName);
        container.setProviderSuffix(providerSuffix);
        container.setProviderPrefix(providerPrefix);
        container.setDegree(degree);

        container.setLastChgUserId(lastChgUserId);
        container.setJurisdictionCd(jurisdictionCd);
        container.setProgAreaCd(progAreaCd);
        container.setLastChgTime(lastChgTime);
        container.setAddUserId(addUserId);
        container.setLastChgReasonCd(lastChgReasonCd);
        container.setRecordStatusTime(recordStatusTime);
        container.setStatusCd(statusCd);
        container.setStatusTime(statusTime);
        container.setProgramJurisdictionOid(programJurisdictionOid);
        container.setSharedInd(sharedInd);
        container.setVersionCtrlNbr(versionCtrlNbr);
        container.setAddTime(addTime);

        assertEquals(arTypeCd, container.getArTypeCd());
        assertEquals(index, container.getIndex());
        assertEquals(personUid, container.getPersonUid());
        assertEquals(yesNoFlag, container.getYesNoFlag());
        assertEquals(treatmentNameCode, container.getTreatmentNameCode());
        assertEquals(customTreatmentNameCode, container.getCustomTreatmentNameCode());
        assertEquals(treatmentAdministered, container.getTreatmentAdministered());
        assertEquals(treatmentUid, container.getTreatmentUid());
        assertEquals(3, container.getUid());
        assertEquals(localId, container.getLocalId());
        assertEquals(activityFromTime, container.getActivityFromTime());
        assertEquals(activityToTime, container.getActivityToTime());
        assertEquals(recordStatusCd, container.getRecordStatusCd());
        assertEquals(phcUid, container.getPhcUid());
        assertEquals(parentUid, container.getParentUid());
        assertEquals(morbReportSummaryVOColl, container.getMorbReportSummaryVOColl());
        assertEquals(isTouched, container.isTouched());
        assertEquals(isAssociated, container.isAssociated());
        assertEquals(isRadioBtnAssociated, container.getIsRadioBtnAssociated());
        assertEquals(actionLink, container.getActionLink());
        assertEquals(checkBoxId, container.getCheckBoxId());
        assertEquals(createDate, container.getCreateDate());
        assertEquals(associationMap, container.getAssociationMap());
        assertEquals(providerFirstName, container.getProviderFirstName());
        assertEquals(nbsDocumentUid, container.getNbsDocumentUid());
        assertEquals(providerLastName, container.getProviderLastName());
        assertEquals(providerSuffix, container.getProviderSuffix());
        assertEquals(providerPrefix, container.getProviderPrefix());
        assertEquals(degree, container.getDegree());

        assertEquals(lastChgUserId, container.getLastChgUserId());
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
        assertEquals(progAreaCd, container.getProgAreaCd());
        assertEquals(lastChgTime, container.getLastChgTime());
        assertEquals(addUserId, container.getAddUserId());
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
        assertEquals(recordStatusTime, container.getRecordStatusTime());
        assertEquals(statusCd, container.getStatusCd());
        assertEquals(statusTime, container.getStatusTime());
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
        assertEquals(sharedInd, container.getSharedInd());
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
        assertEquals(addTime, container.getAddTime());
    }
}

