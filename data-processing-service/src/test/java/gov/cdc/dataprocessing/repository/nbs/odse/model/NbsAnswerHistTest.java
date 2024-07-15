package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NbsAnswerHistTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NbsAnswerHist nbsAnswerHist = new NbsAnswerHist();

        // Assert
        assertNull(nbsAnswerHist.getNbsAnswerUid());
        assertNull(nbsAnswerHist.getActUid());
        assertNull(nbsAnswerHist.getAnswerTxt());
        assertNull(nbsAnswerHist.getNbsQuestionUid());
        assertNull(nbsAnswerHist.getNbsQuestionVersionCtrlNbr());
        assertNull(nbsAnswerHist.getSeqNbr());
        assertNull(nbsAnswerHist.getAnswerLargeTxt());
        assertNull(nbsAnswerHist.getAnswerGroupSeqNbr());
        assertNull(nbsAnswerHist.getRecordStatusCd());
        assertNull(nbsAnswerHist.getRecordStatusTime());
        assertNull(nbsAnswerHist.getLastChgTime());
        assertNull(nbsAnswerHist.getLastChgUserId());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long nbsAnswerUid = 1L;
        Long actUid = 2L;
        String answerTxt = "Sample Answer";
        Long nbsQuestionUid = 3L;
        Integer nbsQuestionVersionCtrlNbr = 4;
        Integer seqNbr = 5;
        String answerLargeTxt = "Sample Large Answer";
        Integer answerGroupSeqNbr = 6;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 7L;

        NbsAnswerDto dto = new NbsAnswerDto();
        dto.setNbsAnswerUid(nbsAnswerUid);
        dto.setActUid(actUid);
        dto.setAnswerTxt(answerTxt);
        dto.setNbsQuestionUid(nbsQuestionUid);
        dto.setNbsQuestionVersionCtrlNbr(nbsQuestionVersionCtrlNbr);
        dto.setSeqNbr(seqNbr);
//        dto.setAnswerLargeTxt(answerLargeTxt);
        dto.setAnswerGroupSeqNbr(answerGroupSeqNbr);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);

        // Act
        NbsAnswerHist nbsAnswerHist = new NbsAnswerHist(dto);

        // Assert
        assertEquals(nbsAnswerUid, nbsAnswerHist.getNbsAnswerUid());
        assertEquals(actUid, nbsAnswerHist.getActUid());
        assertEquals(answerTxt, nbsAnswerHist.getAnswerTxt());
        assertEquals(nbsQuestionUid, nbsAnswerHist.getNbsQuestionUid());
        assertEquals(nbsQuestionVersionCtrlNbr, nbsAnswerHist.getNbsQuestionVersionCtrlNbr());
        assertEquals(seqNbr, nbsAnswerHist.getSeqNbr());
        assertNull(nbsAnswerHist.getAnswerLargeTxt());
        assertEquals(answerGroupSeqNbr, nbsAnswerHist.getAnswerGroupSeqNbr());
        assertEquals(recordStatusCd, nbsAnswerHist.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsAnswerHist.getRecordStatusTime());
        assertEquals(lastChgTime, nbsAnswerHist.getLastChgTime());
        assertEquals(lastChgUserId, nbsAnswerHist.getLastChgUserId());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NbsAnswerHist nbsAnswerHist = new NbsAnswerHist();

        Long nbsAnswerUid = 1L;
        Long actUid = 2L;
        String answerTxt = "Sample Answer";
        Long nbsQuestionUid = 3L;
        Integer nbsQuestionVersionCtrlNbr = 4;
        Integer seqNbr = 5;
        String answerLargeTxt = "Sample Large Answer";
        Integer answerGroupSeqNbr = 6;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 7L;

        // Act
        nbsAnswerHist.setNbsAnswerUid(nbsAnswerUid);
        nbsAnswerHist.setActUid(actUid);
        nbsAnswerHist.setAnswerTxt(answerTxt);
        nbsAnswerHist.setNbsQuestionUid(nbsQuestionUid);
        nbsAnswerHist.setNbsQuestionVersionCtrlNbr(nbsQuestionVersionCtrlNbr);
        nbsAnswerHist.setSeqNbr(seqNbr);
        nbsAnswerHist.setAnswerLargeTxt(answerLargeTxt);
        nbsAnswerHist.setAnswerGroupSeqNbr(answerGroupSeqNbr);
        nbsAnswerHist.setRecordStatusCd(recordStatusCd);
        nbsAnswerHist.setRecordStatusTime(recordStatusTime);
        nbsAnswerHist.setLastChgTime(lastChgTime);
        nbsAnswerHist.setLastChgUserId(lastChgUserId);

        // Assert
        assertEquals(nbsAnswerUid, nbsAnswerHist.getNbsAnswerUid());
        assertEquals(actUid, nbsAnswerHist.getActUid());
        assertEquals(answerTxt, nbsAnswerHist.getAnswerTxt());
        assertEquals(nbsQuestionUid, nbsAnswerHist.getNbsQuestionUid());
        assertEquals(nbsQuestionVersionCtrlNbr, nbsAnswerHist.getNbsQuestionVersionCtrlNbr());
        assertEquals(seqNbr, nbsAnswerHist.getSeqNbr());
        assertEquals(answerLargeTxt, nbsAnswerHist.getAnswerLargeTxt());
        assertEquals(answerGroupSeqNbr, nbsAnswerHist.getAnswerGroupSeqNbr());
        assertEquals(recordStatusCd, nbsAnswerHist.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsAnswerHist.getRecordStatusTime());
        assertEquals(lastChgTime, nbsAnswerHist.getLastChgTime());
        assertEquals(lastChgUserId, nbsAnswerHist.getLastChgUserId());
    }
}
