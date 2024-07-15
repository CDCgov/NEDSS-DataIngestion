package gov.cdc.dataprocessing.model.dto.edx;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EDXEventProcessDtoTest {

    @Test
    void testGettersAndSetters() {
        EDXEventProcessDto dto = new EDXEventProcessDto();

        Long eDXEventProcessUid = 1L;
        Long nbsDocumentUid = 2L;
        String sourceEventId = "sourceEventId";
        Long nbsEventUid = 3L;
        String docEventTypeCd = "docEventTypeCd";
        String docEventSource = "docEventSource";
        Long addUserId = 4L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        String jurisdictionCd = "jurisdictionCd";
        String progAreaCd = "progAreaCd";
        Long programJurisdictionOid = 5L;
        String localId = "localId";
        String parsedInd = "parsedInd";
        Long edxDocumentUid = 6L;

        dto.setEDXEventProcessUid(eDXEventProcessUid);
        dto.setNbsDocumentUid(nbsDocumentUid);
        dto.setSourceEventId(sourceEventId);
        dto.setNbsEventUid(nbsEventUid);
        dto.setDocEventTypeCd(docEventTypeCd);
        dto.setDocEventSource(docEventSource);
        dto.setAddUserId(addUserId);
        dto.setAddTime(addTime);
        dto.setJurisdictionCd(jurisdictionCd);
        dto.setProgAreaCd(progAreaCd);
        dto.setProgramJurisdictionOid(programJurisdictionOid);
        dto.setLocalId(localId);
        dto.setParsedInd(parsedInd);
        dto.setEdxDocumentUid(edxDocumentUid);

        assertEquals(eDXEventProcessUid, dto.getEDXEventProcessUid());
        assertEquals(nbsDocumentUid, dto.getNbsDocumentUid());
        assertEquals(sourceEventId, dto.getSourceEventId());
        assertEquals(nbsEventUid, dto.getNbsEventUid());
        assertEquals(docEventTypeCd, dto.getDocEventTypeCd());
        assertEquals(docEventSource, dto.getDocEventSource());
        assertEquals(addUserId, dto.getAddUserId());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(jurisdictionCd, dto.getJurisdictionCd());
        assertEquals(progAreaCd, dto.getProgAreaCd());
        assertEquals(programJurisdictionOid, dto.getProgramJurisdictionOid());
        assertEquals(localId, dto.getLocalId());
        assertEquals(parsedInd, dto.getParsedInd());
        assertEquals(edxDocumentUid, dto.getEdxDocumentUid());
    }
}
