package gov.cdc.dataprocessing.model.dto.log;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NNDActivityLogDtoTest {

    @Test
    void testGettersAndSetters() {
        NNDActivityLogDto dto = new NNDActivityLogDto();

        Long nndActivityLogUid = 1L;
        Integer nndActivityLogSeq = 123;
        String errorMessageTxt = "Sample error message";
        String localId = "Local ID";
        String recordStatusCd = "Record Status";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "Status Code";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String subjectNm = "Subject Name";
        String service = "Service Name";

        dto.setNndActivityLogUid(nndActivityLogUid);
        dto.setNndActivityLogSeq(nndActivityLogSeq);
        dto.setErrorMessageTxt(errorMessageTxt);
        dto.setLocalId(localId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setSubjectNm(subjectNm);
        dto.setService(service);

        assertEquals(nndActivityLogUid, dto.getNndActivityLogUid());
        assertEquals(nndActivityLogSeq, dto.getNndActivityLogSeq());
        assertEquals(errorMessageTxt, dto.getErrorMessageTxt());
        assertEquals(localId, dto.getLocalId());
        assertEquals(recordStatusCd, dto.getRecordStatusCd());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals(statusCd, dto.getStatusCd());
        assertEquals(statusTime, dto.getStatusTime());
        assertEquals(subjectNm, dto.getSubjectNm());
        assertEquals(service, dto.getService());
    }
}
