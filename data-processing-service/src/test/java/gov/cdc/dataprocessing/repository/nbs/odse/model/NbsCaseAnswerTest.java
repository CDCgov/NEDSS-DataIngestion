package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsCaseAnswerTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NbsCaseAnswer nbsCaseAnswer = new NbsCaseAnswer();

        // Assert
        assertNull(nbsCaseAnswer.getId());
        assertNull(nbsCaseAnswer.getActUid());
        assertNull(nbsCaseAnswer.getAddTime());
        assertNull(nbsCaseAnswer.getAddUserId());
        assertNull(nbsCaseAnswer.getAnswerTxt());
        assertNull(nbsCaseAnswer.getNbsQuestionUid());
        assertNull(nbsCaseAnswer.getNbsQuestionVersionCtrlNbr());
        assertNull(nbsCaseAnswer.getLastChgTime());
        assertNull(nbsCaseAnswer.getLastChgUserId());
        assertNull(nbsCaseAnswer.getRecordStatusCd());
        assertNull(nbsCaseAnswer.getRecordStatusTime());
        assertNull(nbsCaseAnswer.getSeqNbr());
        assertNull(nbsCaseAnswer.getAnswerLargeTxt());
        assertNull(nbsCaseAnswer.getNbsTableMetadataUid());
        assertNull(nbsCaseAnswer.getNbsUiMetadataVerCtrlNbr());
        assertNull(nbsCaseAnswer.getAnswerGroupSeqNbr());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long actUid = 1L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String answerTxt = "Sample Answer";
        Long nbsQuestionUid = 3L;
        Integer nbsQuestionVersionCtrlNbr = 4;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 5L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Integer seqNbr = 6;
        String answerLargeTxt = "Sample Large Answer";
        Long nbsTableMetadataUid = 7L;
        Integer nbsUiMetadataVerCtrlNbr = 8;
        Integer answerGroupSeqNbr = 9;

        NbsCaseAnswerDto dto = new NbsCaseAnswerDto();
        dto.setActUid(actUid);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setAnswerTxt(answerTxt);
        dto.setNbsQuestionUid(nbsQuestionUid);
        dto.setNbsQuestionVersionCtrlNbr(nbsQuestionVersionCtrlNbr);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setSeqNbr(seqNbr);
        dto.setNbsTableMetadataUid(nbsTableMetadataUid);
        dto.setAnswerGroupSeqNbr(answerGroupSeqNbr);

        // Act
        NbsCaseAnswer nbsCaseAnswer = new NbsCaseAnswer(dto);

        // Assert
        assertNull(nbsCaseAnswer.getId()); // ID should be null because it's not set in the DTO
        assertEquals(actUid, nbsCaseAnswer.getActUid());
        assertEquals(addTime, nbsCaseAnswer.getAddTime());
        assertEquals(addUserId, nbsCaseAnswer.getAddUserId());
        assertEquals(answerTxt, nbsCaseAnswer.getAnswerTxt());
        assertEquals(nbsQuestionUid, nbsCaseAnswer.getNbsQuestionUid());
        assertEquals(nbsQuestionVersionCtrlNbr, nbsCaseAnswer.getNbsQuestionVersionCtrlNbr());
        assertEquals(lastChgTime, nbsCaseAnswer.getLastChgTime());
        assertEquals(lastChgUserId, nbsCaseAnswer.getLastChgUserId());
        assertEquals(recordStatusCd, nbsCaseAnswer.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsCaseAnswer.getRecordStatusTime());
        assertEquals(seqNbr, nbsCaseAnswer.getSeqNbr());
        assertNull(nbsCaseAnswer.getAnswerLargeTxt());
        assertEquals(nbsTableMetadataUid, nbsCaseAnswer.getNbsTableMetadataUid());
        assertNotNull(nbsCaseAnswer.getNbsUiMetadataVerCtrlNbr());
        assertEquals(answerGroupSeqNbr, nbsCaseAnswer.getAnswerGroupSeqNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NbsCaseAnswer nbsCaseAnswer = new NbsCaseAnswer();

        Long id = 1L;
        Long actUid = 2L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 3L;
        String answerTxt = "Sample Answer";
        Long nbsQuestionUid = 4L;
        Integer nbsQuestionVersionCtrlNbr = 5;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 6L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Integer seqNbr = 7;
        String answerLargeTxt = "Sample Large Answer";
        Long nbsTableMetadataUid = 8L;
        Integer nbsUiMetadataVerCtrlNbr = 9;
        Integer answerGroupSeqNbr = 10;

        // Act
        nbsCaseAnswer.setId(id);
        nbsCaseAnswer.setActUid(actUid);
        nbsCaseAnswer.setAddTime(addTime);
        nbsCaseAnswer.setAddUserId(addUserId);
        nbsCaseAnswer.setAnswerTxt(answerTxt);
        nbsCaseAnswer.setNbsQuestionUid(nbsQuestionUid);
        nbsCaseAnswer.setNbsQuestionVersionCtrlNbr(nbsQuestionVersionCtrlNbr);
        nbsCaseAnswer.setLastChgTime(lastChgTime);
        nbsCaseAnswer.setLastChgUserId(lastChgUserId);
        nbsCaseAnswer.setRecordStatusCd(recordStatusCd);
        nbsCaseAnswer.setRecordStatusTime(recordStatusTime);
        nbsCaseAnswer.setSeqNbr(seqNbr);
        nbsCaseAnswer.setAnswerLargeTxt(answerLargeTxt);
        nbsCaseAnswer.setNbsTableMetadataUid(nbsTableMetadataUid);
        nbsCaseAnswer.setNbsUiMetadataVerCtrlNbr(nbsUiMetadataVerCtrlNbr);
        nbsCaseAnswer.setAnswerGroupSeqNbr(answerGroupSeqNbr);

        // Assert
        assertEquals(id, nbsCaseAnswer.getId());
        assertEquals(actUid, nbsCaseAnswer.getActUid());
        assertEquals(addTime, nbsCaseAnswer.getAddTime());
        assertEquals(addUserId, nbsCaseAnswer.getAddUserId());
        assertEquals(answerTxt, nbsCaseAnswer.getAnswerTxt());
        assertEquals(nbsQuestionUid, nbsCaseAnswer.getNbsQuestionUid());
        assertEquals(nbsQuestionVersionCtrlNbr, nbsCaseAnswer.getNbsQuestionVersionCtrlNbr());
        assertEquals(lastChgTime, nbsCaseAnswer.getLastChgTime());
        assertEquals(lastChgUserId, nbsCaseAnswer.getLastChgUserId());
        assertEquals(recordStatusCd, nbsCaseAnswer.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsCaseAnswer.getRecordStatusTime());
        assertEquals(seqNbr, nbsCaseAnswer.getSeqNbr());
        assertEquals(answerLargeTxt, nbsCaseAnswer.getAnswerLargeTxt());
        assertEquals(nbsTableMetadataUid, nbsCaseAnswer.getNbsTableMetadataUid());
        assertEquals(nbsUiMetadataVerCtrlNbr, nbsCaseAnswer.getNbsUiMetadataVerCtrlNbr());
        assertEquals(answerGroupSeqNbr, nbsCaseAnswer.getAnswerGroupSeqNbr());
    }
}
