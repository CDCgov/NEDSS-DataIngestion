package gov.cdc.dataprocessing.model.dto.edx;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EDXDocumentDtoTest {

    @Test
    void testGettersAndSetters() {
        EDXDocumentDto dto = new EDXDocumentDto();

        Long eDXDocumentUid = 1L;
        Long actUid = 2L;
        String payload = "payload";
        String recordStatusCd = "recordStatusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        String docTypeCd = "docTypeCd";
        Long nbsDocumentMetadataUid = 3L;
        String originalPayload = "originalPayload";
        String originalDocTypeCd = "originalDocTypeCd";
        Long edxDocumentParentUid = 4L;
        String documentViewXsl = "documentViewXsl";
        String xmlSchemaLocation = "xmlSchemaLocation";
        String progAreaCd = "progAreaCd";
        String jurisdictionCd = "jurisdictionCd";
        Long programJurisdictionOid = 5L;
        String sharedInd = "sharedInd";
        String versionNbr = "versionNbr";
        String viewLink = "viewLink";

        dto.setEDXDocumentUid(eDXDocumentUid);
        dto.setActUid(actUid);
        dto.setPayload(payload);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setAddTime(addTime);
        dto.setDocTypeCd(docTypeCd);
        dto.setNbsDocumentMetadataUid(nbsDocumentMetadataUid);
        dto.setOriginalPayload(originalPayload);
        dto.setOriginalDocTypeCd(originalDocTypeCd);
        dto.setEdxDocumentParentUid(edxDocumentParentUid);
        dto.setDocumentViewXsl(documentViewXsl);
        dto.setXmlSchemaLocation(xmlSchemaLocation);
        dto.setProgAreaCd(progAreaCd);
        dto.setJurisdictionCd(jurisdictionCd);
        dto.setProgramJurisdictionOid(programJurisdictionOid);
        dto.setSharedInd(sharedInd);
        dto.setVersionNbr(versionNbr);
        dto.setViewLink(viewLink);

        assertEquals(eDXDocumentUid, dto.getEDXDocumentUid());
        assertEquals(actUid, dto.getActUid());
        assertEquals(payload, dto.getPayload());
        assertEquals(recordStatusCd, dto.getRecordStatusCd());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(docTypeCd, dto.getDocTypeCd());
        assertEquals(nbsDocumentMetadataUid, dto.getNbsDocumentMetadataUid());
        assertEquals(originalPayload, dto.getOriginalPayload());
        assertEquals(originalDocTypeCd, dto.getOriginalDocTypeCd());
        assertEquals(edxDocumentParentUid, dto.getEdxDocumentParentUid());
        assertEquals(documentViewXsl, dto.getDocumentViewXsl());
        assertEquals(xmlSchemaLocation, dto.getXmlSchemaLocation());
        assertEquals(progAreaCd, dto.getProgAreaCd());
        assertEquals(jurisdictionCd, dto.getJurisdictionCd());
        assertEquals(programJurisdictionOid, dto.getProgramJurisdictionOid());
        assertEquals(sharedInd, dto.getSharedInd());
        assertEquals(versionNbr, dto.getVersionNbr());
        assertEquals(viewLink, dto.getViewLink());
    }
}
