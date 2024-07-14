package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ParticipationHistTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        ParticipationHist participationHist = new ParticipationHist();

        // Assert
        assertNull(participationHist.getSubjectEntityUid());
        assertNull(participationHist.getActUid());
        assertNull(participationHist.getTypeCd());
        assertNull(participationHist.getVersionCtrlNbr());
        assertNull(participationHist.getActClassCd());
        assertNull(participationHist.getAddReasonCd());
        assertNull(participationHist.getAddTime());
        assertNull(participationHist.getAddUserId());
        assertNull(participationHist.getAwarenessCd());
        assertNull(participationHist.getAwarenessDescTxt());
        assertNull(participationHist.getCd());
        assertNull(participationHist.getDurationAmt());
        assertNull(participationHist.getDurationUnitCd());
        assertNull(participationHist.getFromTime());
        assertNull(participationHist.getLastChgReasonCd());
        assertNull(participationHist.getLastChgTime());
        assertNull(participationHist.getLastChgUserId());
        assertNull(participationHist.getRecordStatusCd());
        assertNull(participationHist.getRecordStatusTime());
        assertNull(participationHist.getRoleSeq());
        assertNull(participationHist.getStatusCd());
        assertNull(participationHist.getStatusTime());
        assertNull(participationHist.getSubjectClassCd());
        assertNull(participationHist.getToTime());
        assertNull(participationHist.getTypeDescTxt());
        assertNull(participationHist.getUserAffiliationTxt());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long subjectEntityUid = 1L;
        Long actUid = 2L;
        String typeCd = "TYPE_CD";
        Integer versionCtrlNbr = 1;
        String actClassCd = "ACT_CLASS_CD";
        String addReasonCd = "ADD_REASON_CD";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 3L;
        String awarenessCd = "AWARENESS_CD";
        String awarenessDescTxt = "AWARENESS_DESC_TXT";
        String cd = "CD";
        String durationAmt = "DURATION_AMT";
        String durationUnitCd = "DURATION_UNIT_CD";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "LAST_CHG_REASON_CD";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String recordStatusCd = "RECORD_STATUS_CD";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Long roleSeq = 5L;
        String statusCd = "STATUS_CD";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String subjectClassCd = "SUBJECT_CLASS_CD";
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String typeDescTxt = "TYPE_DESC_TXT";
        String userAffiliationTxt = "USER_AFFILIATION_TXT";

        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(subjectEntityUid);
        dto.setActUid(actUid);
        dto.setTypeCd(typeCd);
        dto.setActClassCd(actClassCd);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setAwarenessCd(awarenessCd);
        dto.setAwarenessDescTxt(awarenessDescTxt);
        dto.setCd(cd);
        dto.setDurationAmt(durationAmt);
        dto.setDurationUnitCd(durationUnitCd);
        dto.setFromTime(fromTime);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setRoleSeq(roleSeq);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setSubjectClassCd(subjectClassCd);
        dto.setToTime(toTime);
        dto.setTypeDescTxt(typeDescTxt);
        dto.setUserAffiliationTxt(userAffiliationTxt);

        // Act
        ParticipationHist participationHist = new ParticipationHist(dto);

        // Assert
        assertEquals(subjectEntityUid, participationHist.getSubjectEntityUid());
        assertEquals(actUid, participationHist.getActUid());
        assertEquals(typeCd, participationHist.getTypeCd());
        assertNull(participationHist.getVersionCtrlNbr());
        assertEquals(actClassCd, participationHist.getActClassCd());
        assertEquals(addReasonCd, participationHist.getAddReasonCd());
        assertEquals(addTime, participationHist.getAddTime());
        assertEquals(addUserId, participationHist.getAddUserId());
        assertEquals(awarenessCd, participationHist.getAwarenessCd());
        assertEquals(awarenessDescTxt, participationHist.getAwarenessDescTxt());
        assertEquals(cd, participationHist.getCd());
        assertEquals(durationAmt, participationHist.getDurationAmt());
        assertEquals(durationUnitCd, participationHist.getDurationUnitCd());
        assertEquals(fromTime, participationHist.getFromTime());
        assertEquals(lastChgReasonCd, participationHist.getLastChgReasonCd());
        assertEquals(lastChgTime, participationHist.getLastChgTime());
        assertEquals(lastChgUserId, participationHist.getLastChgUserId());
        assertEquals(recordStatusCd, participationHist.getRecordStatusCd());
        assertEquals(recordStatusTime, participationHist.getRecordStatusTime());
        assertEquals(roleSeq, participationHist.getRoleSeq());
        assertEquals(statusCd, participationHist.getStatusCd());
        assertEquals(statusTime, participationHist.getStatusTime());
        assertEquals(subjectClassCd, participationHist.getSubjectClassCd());
        assertEquals(toTime, participationHist.getToTime());
        assertEquals(typeDescTxt, participationHist.getTypeDescTxt());
        assertEquals(userAffiliationTxt, participationHist.getUserAffiliationTxt());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        ParticipationHist participationHist = new ParticipationHist();

        Long subjectEntityUid = 1L;
        Long actUid = 2L;
        String typeCd = "TYPE_CD";
        Integer versionCtrlNbr = 1;
        String actClassCd = "ACT_CLASS_CD";
        String addReasonCd = "ADD_REASON_CD";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 3L;
        String awarenessCd = "AWARENESS_CD";
        String awarenessDescTxt = "AWARENESS_DESC_TXT";
        String cd = "CD";
        String durationAmt = "DURATION_AMT";
        String durationUnitCd = "DURATION_UNIT_CD";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "LAST_CHG_REASON_CD";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String recordStatusCd = "RECORD_STATUS_CD";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Long roleSeq = 5L;
        String statusCd = "STATUS_CD";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String subjectClassCd = "SUBJECT_CLASS_CD";
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String typeDescTxt = "TYPE_DESC_TXT";
        String userAffiliationTxt = "USER_AFFILIATION_TXT";

        // Act
        participationHist.setSubjectEntityUid(subjectEntityUid);
        participationHist.setActUid(actUid);
        participationHist.setTypeCd(typeCd);
        participationHist.setVersionCtrlNbr(versionCtrlNbr);
        participationHist.setActClassCd(actClassCd);
        participationHist.setAddReasonCd(addReasonCd);
        participationHist.setAddTime(addTime);
        participationHist.setAddUserId(addUserId);
        participationHist.setAwarenessCd(awarenessCd);
        participationHist.setAwarenessDescTxt(awarenessDescTxt);
        participationHist.setCd(cd);
        participationHist.setDurationAmt(durationAmt);
        participationHist.setDurationUnitCd(durationUnitCd);
        participationHist.setFromTime(fromTime);
        participationHist.setLastChgReasonCd(lastChgReasonCd);
        participationHist.setLastChgTime(lastChgTime);
        participationHist.setLastChgUserId(lastChgUserId);
        participationHist.setRecordStatusCd(recordStatusCd);
        participationHist.setRecordStatusTime(recordStatusTime);
        participationHist.setRoleSeq(roleSeq);
        participationHist.setStatusCd(statusCd);
        participationHist.setStatusTime(statusTime);
        participationHist.setSubjectClassCd(subjectClassCd);
        participationHist.setToTime(toTime);
        participationHist.setTypeDescTxt(typeDescTxt);
        participationHist.setUserAffiliationTxt(userAffiliationTxt);

        // Assert
        assertEquals(subjectEntityUid, participationHist.getSubjectEntityUid());
        assertEquals(actUid, participationHist.getActUid());
        assertEquals(typeCd, participationHist.getTypeCd());
        assertEquals(versionCtrlNbr, participationHist.getVersionCtrlNbr());
        assertEquals(actClassCd, participationHist.getActClassCd());
        assertEquals(addReasonCd, participationHist.getAddReasonCd());
        assertEquals(addTime, participationHist.getAddTime());
        assertEquals(addUserId, participationHist.getAddUserId());
        assertEquals(awarenessCd, participationHist.getAwarenessCd());
        assertEquals(awarenessDescTxt, participationHist.getAwarenessDescTxt());
        assertEquals(cd, participationHist.getCd());
        assertEquals(durationAmt, participationHist.getDurationAmt());
        assertEquals(durationUnitCd, participationHist.getDurationUnitCd());
        assertEquals(fromTime, participationHist.getFromTime());
        assertEquals(lastChgReasonCd, participationHist.getLastChgReasonCd());
        assertEquals(lastChgTime, participationHist.getLastChgTime());
        assertEquals(lastChgUserId, participationHist.getLastChgUserId());
        assertEquals(recordStatusCd, participationHist.getRecordStatusCd());
        assertEquals(recordStatusTime, participationHist.getRecordStatusTime());
        assertEquals(roleSeq, participationHist.getRoleSeq());
        assertEquals(statusCd, participationHist.getStatusCd());
        assertEquals(statusTime, participationHist.getStatusTime());
        assertEquals(subjectClassCd, participationHist.getSubjectClassCd());
        assertEquals(toTime, participationHist.getToTime());
        assertEquals(typeDescTxt, participationHist.getTypeDescTxt());
        assertEquals(userAffiliationTxt, participationHist.getUserAffiliationTxt());
    }
}
