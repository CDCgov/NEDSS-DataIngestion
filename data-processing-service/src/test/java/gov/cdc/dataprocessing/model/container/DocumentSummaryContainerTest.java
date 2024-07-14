package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.DocumentSummaryContainer;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentSummaryContainerTest {

    @Test
    void testGettersAndSetters() {
        DocumentSummaryContainer container = new DocumentSummaryContainer();

        Long nbsDocumentUid = 12345L;
        String docPayload = "Payload";
        String docType = "Type";
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Long addUserID = 67890L;
        String txt = "Text";
        Long MPRUid = 98765L;
        String jurisdiction = "Jurisdiction";
        String programArea = "ProgramArea";
        String type = "Type";
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        String localId = "LocalId";
        Collection<Object> theDocumentResultedTestSummaryVO = null;
        String cd = "CD";
        String cdDescTxt = "Description";
        String firstName = "John";
        String lastName = "Doe";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Map<Object, Object> associationMap = null;
        String sendingFacilityNm = "Facility";
        String progAreaCd = "ProgramAreaCd";

        container.setNbsDocumentUid(nbsDocumentUid);
        container.setDocPayload(docPayload);
        container.setDocType(docType);
        container.setRecordStatusCd(recordStatusCd);
        container.setRecordStatusTime(recordStatusTime);
        container.setAddUserId(addUserID);
        container.setTxt(txt);
        container.setMPRUid(MPRUid);
        container.setJurisdiction(jurisdiction);
        container.setProgramArea(programArea);
        container.setType(type);
        container.setDateReceived(dateReceived);
        container.setLocalId(localId);
        container.setTheDocumentResultedTestSummaryVO(theDocumentResultedTestSummaryVO);
        container.setCd(cd);
        container.setCdDescTxt(cdDescTxt);
        container.setFirstName(firstName);
        container.setLastName(lastName);
        container.setAddTime(addTime);
        container.setAssociationMap(associationMap);
        container.setSendingFacilityNm(sendingFacilityNm);
        container.setProgAreaCd(progAreaCd);

        assertEquals(nbsDocumentUid, container.getNbsDocumentUid());
        assertEquals(docPayload, container.getDocPayload());
        assertEquals(docType, container.getDocType());
        assertEquals(recordStatusCd, container.getRecordStatusCd());
        assertEquals(recordStatusTime, container.getRecordStatusTime());
        assertEquals(addUserID, container.getAddUserId());
        assertEquals(txt, container.getTxt());
        assertEquals(MPRUid, container.getMPRUid());
        assertEquals(jurisdiction, container.getJurisdiction());
        assertEquals(programArea, container.getProgramArea());
        assertEquals(type, container.getType());
        assertEquals(dateReceived, container.getDateReceived());
        assertEquals(localId, container.getLocalId());
        assertEquals(theDocumentResultedTestSummaryVO, container.getTheDocumentResultedTestSummaryVO());
        assertEquals(cd, container.getCd());
        assertEquals(cdDescTxt, container.getCdDescTxt());
        assertEquals(firstName, container.getFirstName());
        assertEquals(lastName, container.getLastName());
        assertEquals(addTime, container.getAddTime());
        assertEquals(associationMap, container.getAssociationMap());
        assertEquals(sendingFacilityNm, container.getSendingFacilityNm());
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testRootDtoInterfaceMethods() {
        DocumentSummaryContainer container = new DocumentSummaryContainer();

        // These methods return null and do not modify state, so we just call them to ensure they are present.
        assertNull(container.getLastChgUserId());
        assertNull(container.getJurisdictionCd());
        assertNull(container.getProgAreaCd());
        assertNull(container.getLastChgTime());
        assertNull(container.getLocalId());
        assertNull(container.getAddUserId());
        assertNull(container.getLastChgReasonCd());
        assertNull(container.getRecordStatusCd());
        assertNull(container.getRecordStatusTime());
        assertNull(container.getStatusCd());
        assertNull(container.getStatusTime());
        assertNull(container.getUid());
        assertNull(container.getAddTime());
        assertNull(container.getProgramJurisdictionOid());
        assertNull(container.getSharedInd());
        assertNull(container.getVersionCtrlNbr());

        // Call setters and check they don't throw exceptions
        container.setLastChgUserId(123L);
        container.setJurisdictionCd("123");
        container.setProgAreaCd("PA123");
        container.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        container.setLocalId("Local123");
        container.setAddUserId(456L);
        container.setLastChgReasonCd("Reason123");
        container.setRecordStatusCd("Active");
        container.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        container.setStatusCd("Status123");
        container.setStatusTime(new Timestamp(System.currentTimeMillis()));
        container.setAddTime(new Timestamp(System.currentTimeMillis()));
        container.setProgramJurisdictionOid(789L);
        container.setSharedInd("Y");
    }
}