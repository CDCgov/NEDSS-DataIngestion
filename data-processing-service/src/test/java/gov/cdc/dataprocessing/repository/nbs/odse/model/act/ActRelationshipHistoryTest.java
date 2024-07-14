package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ActRelationshipHistoryTest {

    @Test
    void testDefaultConstructor() {
        ActRelationshipHistory history = new ActRelationshipHistory();

        assertNull(history.getSourceActUid());
        assertNull(history.getTargetActUid());
        assertNull(history.getTypeCd());
        assertNull(history.getVersionCrl());
        assertNull(history.getAddReasonCd());
        assertNull(history.getAddTime());
        assertNull(history.getAddUserId());
        assertNull(history.getDurationAmt());
        assertNull(history.getDurationUnitCd());
        assertNull(history.getFromTime());
        assertNull(history.getLastChgReasonCd());
        assertNull(history.getLastChgTime());
        assertNull(history.getLastChgUserId());
        assertNull(history.getRecordStatusCd());
        assertNull(history.getRecordStatusTime());
        assertNull(history.getSequenceNbr());
        assertNull(history.getStatusCd());
        assertNull(history.getStatusTime());
        assertNull(history.getSourceClassCd());
        assertNull(history.getTargetClassCd());
        assertNull(history.getToTime());
        assertNull(history.getTypeDescTxt());
        assertNull(history.getUserAffiliationTxt());
    }

    @Test
    void testParameterizedConstructor() {
        Long sourceActUid = 1L;
        Long targetActUid = 2L;
        String typeCd = "Type";
        String addReasonCd = "AddReason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 3L;
        String durationAmt = "DurationAmt";
        String durationUnitCd = "DurationUnit";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "LastChgReason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String recordStatusCd = "RecordStatus";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Integer sequenceNbr = 5;
        String statusCd = "Status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String sourceClassCd = "SourceClass";
        String targetClassCd = "TargetClass";
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String typeDescTxt = "TypeDesc";
        String userAffiliationTxt = "UserAffiliation";

        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setSourceActUid(sourceActUid);
        dto.setTargetActUid(targetActUid);
        dto.setTypeCd(typeCd);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setDurationAmt(durationAmt);
        dto.setDurationUnitCd(durationUnitCd);
        dto.setFromTime(fromTime);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setSequenceNbr(sequenceNbr);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setSourceClassCd(sourceClassCd);
        dto.setTargetClassCd(targetClassCd);
        dto.setToTime(toTime);
        dto.setTypeDescTxt(typeDescTxt);
        dto.setUserAffiliationTxt(userAffiliationTxt);

        ActRelationshipHistory history = new ActRelationshipHistory(dto);

        assertEquals(sourceActUid, history.getSourceActUid());
        assertEquals(targetActUid, history.getTargetActUid());
        assertEquals(typeCd, history.getTypeCd());
        assertEquals(1, history.getVersionCrl()); // Default value set in the constructor
        assertEquals(addReasonCd, history.getAddReasonCd());
        assertEquals(addTime, history.getAddTime());
        assertEquals(addUserId, history.getAddUserId());
        assertEquals(durationAmt, history.getDurationAmt());
        assertEquals(durationUnitCd, history.getDurationUnitCd());
        assertEquals(fromTime, history.getFromTime());
        assertEquals(lastChgReasonCd, history.getLastChgReasonCd());
        assertEquals(lastChgTime, history.getLastChgTime());
        assertEquals(lastChgUserId, history.getLastChgUserId());
        assertEquals(recordStatusCd, history.getRecordStatusCd());
        assertEquals(recordStatusTime, history.getRecordStatusTime());
        assertEquals(sequenceNbr, history.getSequenceNbr());
        assertEquals(statusCd, history.getStatusCd());
        assertEquals(statusTime, history.getStatusTime());
        assertEquals(sourceClassCd, history.getSourceClassCd());
        assertEquals(targetClassCd, history.getTargetClassCd());
        assertEquals(toTime, history.getToTime());
        assertEquals(typeDescTxt, history.getTypeDescTxt());
        assertEquals(userAffiliationTxt, history.getUserAffiliationTxt());
    }
}
