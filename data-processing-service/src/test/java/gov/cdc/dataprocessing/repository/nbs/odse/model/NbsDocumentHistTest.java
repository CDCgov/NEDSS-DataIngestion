package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocumentHist;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NbsDocumentHistTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NbsDocumentHist nbsDocumentHist = new NbsDocumentHist();

        // Assert
        assertNull(nbsDocumentHist.getNbsDocumentHistUid());
        assertNull(nbsDocumentHist.getDocPayload());
        assertNull(nbsDocumentHist.getDocTypeCd());
        assertNull(nbsDocumentHist.getLocalId());
        assertNull(nbsDocumentHist.getRecordStatusCd());
        assertNull(nbsDocumentHist.getRecordStatusTime());
        assertNull(nbsDocumentHist.getAddUserId());
        assertNull(nbsDocumentHist.getAddTime());
        assertNull(nbsDocumentHist.getProgAreaCd());
        assertNull(nbsDocumentHist.getJurisdictionCd());
        assertNull(nbsDocumentHist.getTxt());
        assertNull(nbsDocumentHist.getProgramJurisdictionOid());
        assertNull(nbsDocumentHist.getSharedInd());
        assertNull(nbsDocumentHist.getVersionCtrlNbr());
        assertNull(nbsDocumentHist.getCd());
        assertNull(nbsDocumentHist.getLastChgTime());
        assertNull(nbsDocumentHist.getLastChgUserId());
        assertNull(nbsDocumentHist.getDocPurposeCd());
        assertNull(nbsDocumentHist.getDocStatusCd());
        assertNull(nbsDocumentHist.getCdDescTxt());
        assertNull(nbsDocumentHist.getSendingFacilityNm());
        assertNull(nbsDocumentHist.getNbsInterfaceUid());
        assertNull(nbsDocumentHist.getSendingAppEventId());
        assertNull(nbsDocumentHist.getSendingAppPatientId());
        assertNull(nbsDocumentHist.getNbsDocumentUid());
        assertNull(nbsDocumentHist.getPhdcDocDerived());
        assertNull(nbsDocumentHist.getPayloadViewIndCd());
        assertNull(nbsDocumentHist.getNbsDocumentMetadataUid());
        assertNull(nbsDocumentHist.getExternalVersionCtrlNbr());
        assertNull(nbsDocumentHist.getProcessingDecisionTxt());
        assertNull(nbsDocumentHist.getProcessingDecisionCd());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long nbsDocumentHistUid = 1L;
        String docPayload = "Sample Payload";
        String docTypeCd = "Type1";
        String localId = "Local123";
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        String progAreaCd = "Prog1";
        String jurisdictionCd = "Jur1";
        String txt = "Sample Text";
        Long programJurisdictionOid = 3L;
        String sharedInd = "Y";
        Integer versionCtrlNbr = 1;
        String cd = "CD1";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String docPurposeCd = "Purpose1";
        String docStatusCd = "Status1";
        String cdDescTxt = "Description";
        String sendingFacilityNm = "Facility1";
        Long nbsInterfaceUid = 5L;
        String sendingAppEventId = "Event1";
        String sendingAppPatientId = "Patient1";
        Long nbsDocumentUid = 6L;
        String phdcDocDerived = "Derived";
        String payloadViewIndCd = "View1";
        Long nbsDocumentMetadataUid = 7L;
        Integer externalVersionCtrlNbr = 2;
        String processingDecisionTxt = "Decision1";
        String processingDecisionCd = "DecisionCd1";

        NBSDocumentDto dto = new NBSDocumentDto();
        dto.setNbsDocumentUid(nbsDocumentUid);
        dto.setDocPayload(docPayload);
        dto.setDocTypeCd(docTypeCd);
        dto.setLocalId(localId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setAddUserId(addUserId);
        dto.setAddTime(addTime);
        dto.setProgAreaCd(progAreaCd);
        dto.setJurisdictionCd(jurisdictionCd);
        dto.setTxt(txt);
        dto.setProgramJurisdictionOid(programJurisdictionOid);
        dto.setSharedInd(sharedInd);
        dto.setVersionCtrlNbr(versionCtrlNbr);
        dto.setCd(cd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setDocPurposeCd(docPurposeCd);
        dto.setDocStatusCd(docStatusCd);
        dto.setCdDescTxt(cdDescTxt);
        dto.setSendingFacilityNm(sendingFacilityNm);
        dto.setNbsInterfaceUid(nbsInterfaceUid);
        dto.setSendingAppEventId(sendingAppEventId);
        dto.setSendingAppPatientId(sendingAppPatientId);
        dto.setPhdcDocDerived(phdcDocDerived);
        dto.setPayloadViewIndCd(payloadViewIndCd);
        dto.setNbsDocumentMetadataUid(nbsDocumentMetadataUid);
        dto.setExternalVersionCtrlNbr(externalVersionCtrlNbr);
        dto.setProcessingDecisiontxt(processingDecisionTxt);
        dto.setProcessingDecisionCd(processingDecisionCd);

        // Act
        NbsDocumentHist nbsDocumentHist = new NbsDocumentHist(dto);

        // Assert
        assertEquals(docPayload, nbsDocumentHist.getDocPayload());
        assertEquals(docTypeCd, nbsDocumentHist.getDocTypeCd());
        assertEquals(localId, nbsDocumentHist.getLocalId());
        assertEquals(recordStatusCd, nbsDocumentHist.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsDocumentHist.getRecordStatusTime());
        assertEquals(addUserId, nbsDocumentHist.getAddUserId());
        assertEquals(addTime, nbsDocumentHist.getAddTime());
        assertEquals(progAreaCd, nbsDocumentHist.getProgAreaCd());
        assertEquals(jurisdictionCd, nbsDocumentHist.getJurisdictionCd());
        assertEquals(txt, nbsDocumentHist.getTxt());
        assertEquals(programJurisdictionOid, nbsDocumentHist.getProgramJurisdictionOid());
        assertEquals(sharedInd, nbsDocumentHist.getSharedInd());
        assertEquals(versionCtrlNbr, nbsDocumentHist.getVersionCtrlNbr());
        assertEquals(cd, nbsDocumentHist.getCd());
        assertEquals(lastChgTime, nbsDocumentHist.getLastChgTime());
        assertEquals(lastChgUserId, nbsDocumentHist.getLastChgUserId());
        assertEquals(docPurposeCd, nbsDocumentHist.getDocPurposeCd());
        assertEquals(docStatusCd, nbsDocumentHist.getDocStatusCd());
        assertEquals(cdDescTxt, nbsDocumentHist.getCdDescTxt());
        assertEquals(sendingFacilityNm, nbsDocumentHist.getSendingFacilityNm());
        assertEquals(nbsInterfaceUid, nbsDocumentHist.getNbsInterfaceUid());
        assertEquals(sendingAppEventId, nbsDocumentHist.getSendingAppEventId());
        assertEquals(sendingAppPatientId, nbsDocumentHist.getSendingAppPatientId());
        assertEquals(nbsDocumentUid, nbsDocumentHist.getNbsDocumentUid());
        assertEquals(phdcDocDerived, nbsDocumentHist.getPhdcDocDerived());
        assertEquals(payloadViewIndCd, nbsDocumentHist.getPayloadViewIndCd());
        assertEquals(nbsDocumentMetadataUid, nbsDocumentHist.getNbsDocumentMetadataUid());
        assertEquals(externalVersionCtrlNbr, nbsDocumentHist.getExternalVersionCtrlNbr());
        assertEquals(processingDecisionTxt, nbsDocumentHist.getProcessingDecisionTxt());
        assertEquals(processingDecisionCd, nbsDocumentHist.getProcessingDecisionCd());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NbsDocumentHist nbsDocumentHist = new NbsDocumentHist();

        Long nbsDocumentHistUid = 1L;
        String docPayload = "Sample Payload";
        String docTypeCd = "Type1";
        String localId = "Local123";
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        String progAreaCd = "Prog1";
        String jurisdictionCd = "Jur1";
        String txt = "Sample Text";
        Long programJurisdictionOid = 3L;
        String sharedInd = "Y";
        Integer versionCtrlNbr = 1;
        String cd = "CD1";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String docPurposeCd = "Purpose1";
        String docStatusCd = "Status1";
        String cdDescTxt = "Description";
        String sendingFacilityNm = "Facility1";
        Long nbsInterfaceUid = 5L;
        String sendingAppEventId = "Event1";
        String sendingAppPatientId = "Patient1";
        Long nbsDocumentUid = 6L;
        String phdcDocDerived = "Derived";
        String payloadViewIndCd = "View1";
        Long nbsDocumentMetadataUid = 7L;
        Integer externalVersionCtrlNbr = 2;
        String processingDecisionTxt = "Decision1";
        String processingDecisionCd = "DecisionCd1";

        // Act
        nbsDocumentHist.setNbsDocumentHistUid(nbsDocumentHistUid);
        nbsDocumentHist.setDocPayload(docPayload);
        nbsDocumentHist.setDocTypeCd(docTypeCd);
        nbsDocumentHist.setLocalId(localId);
        nbsDocumentHist.setRecordStatusCd(recordStatusCd);
        nbsDocumentHist.setRecordStatusTime(recordStatusTime);
        nbsDocumentHist.setAddUserId(addUserId);
        nbsDocumentHist.setAddTime(addTime);
        nbsDocumentHist.setProgAreaCd(progAreaCd);
        nbsDocumentHist.setJurisdictionCd(jurisdictionCd);
        nbsDocumentHist.setTxt(txt);
        nbsDocumentHist.setProgramJurisdictionOid(programJurisdictionOid);
        nbsDocumentHist.setSharedInd(sharedInd);
        nbsDocumentHist.setVersionCtrlNbr(versionCtrlNbr);
        nbsDocumentHist.setCd(cd);
        nbsDocumentHist.setLastChgTime(lastChgTime);
        nbsDocumentHist.setLastChgUserId(lastChgUserId);
        nbsDocumentHist.setDocPurposeCd(docPurposeCd);
        nbsDocumentHist.setDocStatusCd(docStatusCd);
        nbsDocumentHist.setCdDescTxt(cdDescTxt);
        nbsDocumentHist.setSendingFacilityNm(sendingFacilityNm);
        nbsDocumentHist.setNbsInterfaceUid(nbsInterfaceUid);
        nbsDocumentHist.setSendingAppEventId(sendingAppEventId);
        nbsDocumentHist.setSendingAppPatientId(sendingAppPatientId);
        nbsDocumentHist.setNbsDocumentUid(nbsDocumentUid);
        nbsDocumentHist.setPhdcDocDerived(phdcDocDerived);
        nbsDocumentHist.setPayloadViewIndCd(payloadViewIndCd);
        nbsDocumentHist.setNbsDocumentMetadataUid(nbsDocumentMetadataUid);
        nbsDocumentHist.setExternalVersionCtrlNbr(externalVersionCtrlNbr);
        nbsDocumentHist.setProcessingDecisionTxt(processingDecisionTxt);
        nbsDocumentHist.setProcessingDecisionCd(processingDecisionCd);

        // Assert
        assertEquals(nbsDocumentHistUid, nbsDocumentHist.getNbsDocumentHistUid());
        assertEquals(docPayload, nbsDocumentHist.getDocPayload());
        assertEquals(docTypeCd, nbsDocumentHist.getDocTypeCd());
        assertEquals(localId, nbsDocumentHist.getLocalId());
        assertEquals(recordStatusCd, nbsDocumentHist.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsDocumentHist.getRecordStatusTime());
        assertEquals(addUserId, nbsDocumentHist.getAddUserId());
        assertEquals(addTime, nbsDocumentHist.getAddTime());
        assertEquals(progAreaCd, nbsDocumentHist.getProgAreaCd());
        assertEquals(jurisdictionCd, nbsDocumentHist.getJurisdictionCd());
        assertEquals(txt, nbsDocumentHist.getTxt());
        assertEquals(programJurisdictionOid, nbsDocumentHist.getProgramJurisdictionOid());
        assertEquals(sharedInd, nbsDocumentHist.getSharedInd());
        assertEquals(versionCtrlNbr, nbsDocumentHist.getVersionCtrlNbr());
        assertEquals(cd, nbsDocumentHist.getCd());
        assertEquals(lastChgTime, nbsDocumentHist.getLastChgTime());
        assertEquals(lastChgUserId, nbsDocumentHist.getLastChgUserId());
        assertEquals(docPurposeCd, nbsDocumentHist.getDocPurposeCd());
        assertEquals(docStatusCd, nbsDocumentHist.getDocStatusCd());
        assertEquals(cdDescTxt, nbsDocumentHist.getCdDescTxt());
        assertEquals(sendingFacilityNm, nbsDocumentHist.getSendingFacilityNm());
        assertEquals(nbsInterfaceUid, nbsDocumentHist.getNbsInterfaceUid());
        assertEquals(sendingAppEventId, nbsDocumentHist.getSendingAppEventId());
        assertEquals(sendingAppPatientId, nbsDocumentHist.getSendingAppPatientId());
        assertEquals(nbsDocumentUid, nbsDocumentHist.getNbsDocumentUid());
        assertEquals(phdcDocDerived, nbsDocumentHist.getPhdcDocDerived());
        assertEquals(payloadViewIndCd, nbsDocumentHist.getPayloadViewIndCd());
        assertEquals(nbsDocumentMetadataUid, nbsDocumentHist.getNbsDocumentMetadataUid());
        assertEquals(externalVersionCtrlNbr, nbsDocumentHist.getExternalVersionCtrlNbr());
        assertEquals(processingDecisionTxt, nbsDocumentHist.getProcessingDecisionTxt());
        assertEquals(processingDecisionCd, nbsDocumentHist.getProcessingDecisionCd());
    }
}
