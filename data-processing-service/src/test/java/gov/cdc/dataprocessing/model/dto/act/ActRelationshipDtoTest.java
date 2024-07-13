package gov.cdc.dataprocessing.model.dto.act;


import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActRelationshipDtoTest {

    @Test
    void testGettersAndSetters() {
        ActRelationshipDto dto = new ActRelationshipDto();

        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 1L;
        String durationAmt = "durationAmt";
        String durationUnitCd = "durationUnitCd";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "lastChgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 2L;
        String recordStatusCd = "recordStatusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Integer sequenceNbr = 3;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String userAffiliationTxt = "userAffiliationTxt";
        Long sourceActUid = 4L;
        String typeDescTxt = "typeDescTxt";
        Long targetActUid = 5L;
        String sourceClassCd = "sourceClassCd";
        String targetClassCd = "targetClassCd";
        String typeCd = "typeCd";
        boolean isShareInd = true;
        boolean isNNDInd = true;
        boolean isExportInd = true;

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
        dto.setToTime(toTime);
        dto.setUserAffiliationTxt(userAffiliationTxt);
        dto.setSourceActUid(sourceActUid);
        dto.setTypeDescTxt(typeDescTxt);
        dto.setTargetActUid(targetActUid);
        dto.setSourceClassCd(sourceClassCd);
        dto.setTargetClassCd(targetClassCd);
        dto.setTypeCd(typeCd);
        dto.setShareInd(isShareInd);
        dto.setNNDInd(isNNDInd);
        dto.setExportInd(isExportInd);

        assertEquals(addReasonCd, dto.getAddReasonCd());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(addUserId, dto.getAddUserId());
        assertEquals(durationAmt, dto.getDurationAmt());
        assertEquals(durationUnitCd, dto.getDurationUnitCd());
        assertEquals(fromTime, dto.getFromTime());
        assertEquals(lastChgReasonCd, dto.getLastChgReasonCd());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(lastChgUserId, dto.getLastChgUserId());
        assertEquals(recordStatusCd, dto.getRecordStatusCd());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals(sequenceNbr, dto.getSequenceNbr());
        assertEquals(statusCd, dto.getStatusCd());
        assertEquals(statusTime, dto.getStatusTime());
        assertEquals(toTime, dto.getToTime());
        assertEquals(userAffiliationTxt, dto.getUserAffiliationTxt());
        assertEquals(sourceActUid, dto.getSourceActUid());
        assertEquals(typeDescTxt, dto.getTypeDescTxt());
        assertEquals(targetActUid, dto.getTargetActUid());
        assertEquals(sourceClassCd, dto.getSourceClassCd());
        assertEquals(targetClassCd, dto.getTargetClassCd());
        assertEquals(typeCd, dto.getTypeCd());
        assertEquals(isShareInd, dto.isShareInd());
        assertEquals(isNNDInd, dto.isNNDInd());
        assertEquals(isExportInd, dto.isExportInd());
    }

    @Test
    void testConstructor() {
        ActRelationship actRelationship = new ActRelationship();
        actRelationship.setAddReasonCd("reasonCd");
        actRelationship.setAddTime(new Timestamp(System.currentTimeMillis()));
        actRelationship.setAddUserId(1L);
        actRelationship.setDurationAmt("durationAmt");
        actRelationship.setDurationUnitCd("durationUnitCd");
        actRelationship.setFromTime(new Timestamp(System.currentTimeMillis()));
        actRelationship.setLastChgReasonCd("lastChgReasonCd");
        actRelationship.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        actRelationship.setLastChgUserId(2L);
        actRelationship.setRecordStatusCd("recordStatusCd");
        actRelationship.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        actRelationship.setSequenceNbr(3);
        actRelationship.setStatusCd("statusCd");
        actRelationship.setStatusTime(new Timestamp(System.currentTimeMillis()));
        actRelationship.setToTime(new Timestamp(System.currentTimeMillis()));
        actRelationship.setUserAffiliationTxt("userAffiliationTxt");
        actRelationship.setSourceActUid(4L);
        actRelationship.setTypeDescTxt("typeDescTxt");
        actRelationship.setTargetActUid(5L);
        actRelationship.setSourceClassCd("sourceClassCd");
        actRelationship.setTargetClassCd("targetClassCd");
        actRelationship.setTypeCd("typeCd");

        ActRelationshipDto dto = new ActRelationshipDto(actRelationship);

        assertEquals(actRelationship.getAddReasonCd(), dto.getAddReasonCd());
        assertEquals(actRelationship.getAddTime(), dto.getAddTime());
        assertEquals(actRelationship.getAddUserId(), dto.getAddUserId());
        assertEquals(actRelationship.getDurationAmt(), dto.getDurationAmt());
        assertEquals(actRelationship.getDurationUnitCd(), dto.getDurationUnitCd());
        assertEquals(actRelationship.getFromTime(), dto.getFromTime());
        assertEquals(actRelationship.getLastChgReasonCd(), dto.getLastChgReasonCd());
        assertEquals(actRelationship.getLastChgTime(), dto.getLastChgTime());
        assertEquals(actRelationship.getLastChgUserId(), dto.getLastChgUserId());
        assertEquals(actRelationship.getRecordStatusCd(), dto.getRecordStatusCd());
        assertEquals(actRelationship.getRecordStatusTime(), dto.getRecordStatusTime());
        assertEquals(actRelationship.getSequenceNbr(), dto.getSequenceNbr());
        assertEquals(actRelationship.getStatusCd(), dto.getStatusCd());
        assertEquals(actRelationship.getStatusTime(), dto.getStatusTime());
        assertEquals(actRelationship.getToTime(), dto.getToTime());
        assertEquals(actRelationship.getUserAffiliationTxt(), dto.getUserAffiliationTxt());
        assertEquals(actRelationship.getSourceActUid(), dto.getSourceActUid());
        assertEquals(actRelationship.getTypeDescTxt(), dto.getTypeDescTxt());
        assertEquals(actRelationship.getTargetActUid(), dto.getTargetActUid());
        assertEquals(actRelationship.getSourceClassCd(), dto.getSourceClassCd());
        assertEquals(actRelationship.getTargetClassCd(), dto.getTargetClassCd());
        assertEquals(actRelationship.getTypeCd(), dto.getTypeCd());
    }
}
