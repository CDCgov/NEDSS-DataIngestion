package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocument;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NbsDocumentTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NbsDocument nbsDocument = new NbsDocument();

        // Assert
        assertNull(nbsDocument.getNbsDocumentUid());
        assertNull(nbsDocument.getDocPayload());
        assertNull(nbsDocument.getDocTypeCd());
        assertNull(nbsDocument.getLocalId());
        assertNull(nbsDocument.getRecordStatusCd());
        assertNull(nbsDocument.getRecordStatusTime());
        assertNull(nbsDocument.getAddUserId());
        assertNull(nbsDocument.getAddTime());
        assertNull(nbsDocument.getProgAreaCd());
        assertNull(nbsDocument.getJurisdictionCd());
        assertNull(nbsDocument.getTxt());
        assertNull(nbsDocument.getProgramJurisdictionOid());
        assertNull(nbsDocument.getSharedInd());
        assertNull(nbsDocument.getVersionCtrlNbr());
        assertNull(nbsDocument.getCd());
        assertNull(nbsDocument.getLastChgTime());
        assertNull(nbsDocument.getLastChgUserId());
        assertNull(nbsDocument.getDocPurposeCd());
        assertNull(nbsDocument.getDocStatusCd());
        assertNull(nbsDocument.getCdDescTxt());
        assertNull(nbsDocument.getSendingFacilityNm());
        assertNull(nbsDocument.getNbsInterfaceUid());
        assertNull(nbsDocument.getSendingAppEventId());
        assertNull(nbsDocument.getSendingAppPatientId());
        assertNull(nbsDocument.getPhdcDocDerived());
        assertNull(nbsDocument.getPayloadViewIndCd());
        assertNull(nbsDocument.getExternalVersionCtrlNbr());
        assertNull(nbsDocument.getProcessingDecisionTxt());
        assertNull(nbsDocument.getProcessingDecisionCd());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long nbsDocumentUid = 1L;
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
        String phdcDocDerived = "Derived";
        String payloadViewIndCd = "View1";
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
        dto.setExternalVersionCtrlNbr(externalVersionCtrlNbr);
        dto.setProcessingDecisiontxt(processingDecisionTxt);
        dto.setProcessingDecisionCd(processingDecisionCd);

        // Act
        NbsDocument nbsDocument = new NbsDocument(dto);

        // Assert
        assertEquals(nbsDocumentUid, nbsDocument.getNbsDocumentUid());
        assertEquals(docPayload, nbsDocument.getDocPayload());
        assertEquals(docTypeCd, nbsDocument.getDocTypeCd());
        assertEquals(localId, nbsDocument.getLocalId());
        assertEquals(recordStatusCd, nbsDocument.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsDocument.getRecordStatusTime());
        assertEquals(addUserId, nbsDocument.getAddUserId());
        assertEquals(addTime, nbsDocument.getAddTime());
        assertEquals(progAreaCd, nbsDocument.getProgAreaCd());
        assertEquals(jurisdictionCd, nbsDocument.getJurisdictionCd());
        assertEquals(txt, nbsDocument.getTxt());
        assertEquals(programJurisdictionOid, nbsDocument.getProgramJurisdictionOid());
        assertEquals(sharedInd, nbsDocument.getSharedInd());
        assertEquals(versionCtrlNbr, nbsDocument.getVersionCtrlNbr());
        assertEquals(cd, nbsDocument.getCd());
        assertEquals(lastChgTime, nbsDocument.getLastChgTime());
        assertEquals(lastChgUserId, nbsDocument.getLastChgUserId());
        assertEquals(docPurposeCd, nbsDocument.getDocPurposeCd());
        assertEquals(docStatusCd, nbsDocument.getDocStatusCd());
        assertEquals(cdDescTxt, nbsDocument.getCdDescTxt());
        assertEquals(sendingFacilityNm, nbsDocument.getSendingFacilityNm());
        assertEquals(nbsInterfaceUid, nbsDocument.getNbsInterfaceUid());
        assertEquals(sendingAppEventId, nbsDocument.getSendingAppEventId());
        assertEquals(sendingAppPatientId, nbsDocument.getSendingAppPatientId());
        assertEquals(phdcDocDerived, nbsDocument.getPhdcDocDerived());
        assertEquals(payloadViewIndCd, nbsDocument.getPayloadViewIndCd());
        assertEquals(externalVersionCtrlNbr, nbsDocument.getExternalVersionCtrlNbr());
        assertEquals(processingDecisionTxt, nbsDocument.getProcessingDecisionTxt());
        assertEquals(processingDecisionCd, nbsDocument.getProcessingDecisionCd());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NbsDocument nbsDocument = new NbsDocument();

        Long nbsDocumentUid = 1L;
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
        String phdcDocDerived = "Derived";
        String payloadViewIndCd = "View1";
        Integer externalVersionCtrlNbr = 2;
        String processingDecisionTxt = "Decision1";
        String processingDecisionCd = "DecisionCd1";

        // Act
        nbsDocument.setNbsDocumentUid(nbsDocumentUid);
        nbsDocument.setDocPayload(docPayload);
        nbsDocument.setDocTypeCd(docTypeCd);
        nbsDocument.setLocalId(localId);
        nbsDocument.setRecordStatusCd(recordStatusCd);
        nbsDocument.setRecordStatusTime(recordStatusTime);
        nbsDocument.setAddUserId(addUserId);
        nbsDocument.setAddTime(addTime);
        nbsDocument.setProgAreaCd(progAreaCd);
        nbsDocument.setJurisdictionCd(jurisdictionCd);
        nbsDocument.setTxt(txt);
        nbsDocument.setProgramJurisdictionOid(programJurisdictionOid);
        nbsDocument.setSharedInd(sharedInd);
        nbsDocument.setVersionCtrlNbr(versionCtrlNbr);
        nbsDocument.setCd(cd);
        nbsDocument.setLastChgTime(lastChgTime);
        nbsDocument.setLastChgUserId(lastChgUserId);
        nbsDocument.setDocPurposeCd(docPurposeCd);
        nbsDocument.setDocStatusCd(docStatusCd);
        nbsDocument.setCdDescTxt(cdDescTxt);
        nbsDocument.setSendingFacilityNm(sendingFacilityNm);
        nbsDocument.setNbsInterfaceUid(nbsInterfaceUid);
        nbsDocument.setSendingAppEventId(sendingAppEventId);
        nbsDocument.setSendingAppPatientId(sendingAppPatientId);
        nbsDocument.setPhdcDocDerived(phdcDocDerived);
        nbsDocument.setPayloadViewIndCd(payloadViewIndCd);
        nbsDocument.setExternalVersionCtrlNbr(externalVersionCtrlNbr);
        nbsDocument.setProcessingDecisionTxt(processingDecisionTxt);
        nbsDocument.setProcessingDecisionCd(processingDecisionCd);

        // Assert
        assertEquals(nbsDocumentUid, nbsDocument.getNbsDocumentUid());
        assertEquals(docPayload, nbsDocument.getDocPayload());
        assertEquals(docTypeCd, nbsDocument.getDocTypeCd());
        assertEquals(localId, nbsDocument.getLocalId());
        assertEquals(recordStatusCd, nbsDocument.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsDocument.getRecordStatusTime());
        assertEquals(addUserId, nbsDocument.getAddUserId());
        assertEquals(addTime, nbsDocument.getAddTime());
        assertEquals(progAreaCd, nbsDocument.getProgAreaCd());
        assertEquals(jurisdictionCd, nbsDocument.getJurisdictionCd());
        assertEquals(txt, nbsDocument.getTxt());
        assertEquals(programJurisdictionOid, nbsDocument.getProgramJurisdictionOid());
        assertEquals(sharedInd, nbsDocument.getSharedInd());
        assertEquals(versionCtrlNbr, nbsDocument.getVersionCtrlNbr());
        assertEquals(cd, nbsDocument.getCd());
        assertEquals(lastChgTime, nbsDocument.getLastChgTime());
        assertEquals(lastChgUserId, nbsDocument.getLastChgUserId());
        assertEquals(docPurposeCd, nbsDocument.getDocPurposeCd());
        assertEquals(docStatusCd, nbsDocument.getDocStatusCd());
        assertEquals(cdDescTxt, nbsDocument.getCdDescTxt());
        assertEquals(sendingFacilityNm, nbsDocument.getSendingFacilityNm());
        assertEquals(nbsInterfaceUid, nbsDocument.getNbsInterfaceUid());
        assertEquals(sendingAppEventId, nbsDocument.getSendingAppEventId());
        assertEquals(sendingAppPatientId, nbsDocument.getSendingAppPatientId());
        assertEquals(phdcDocDerived, nbsDocument.getPhdcDocDerived());
        assertEquals(payloadViewIndCd, nbsDocument.getPayloadViewIndCd());
        assertEquals(externalVersionCtrlNbr, nbsDocument.getExternalVersionCtrlNbr());
        assertEquals(processingDecisionTxt, nbsDocument.getProcessingDecisionTxt());
        assertEquals(processingDecisionCd, nbsDocument.getProcessingDecisionCd());
    }
}
