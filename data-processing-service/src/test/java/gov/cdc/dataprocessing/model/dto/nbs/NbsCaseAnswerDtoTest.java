package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NbsCaseAnswerDtoTest {

    @Test
    public void testEmptyConstructor() {
        // Create an instance of NbsCaseAnswerDto using empty constructor
        NbsCaseAnswerDto dto = new NbsCaseAnswerDto();

        // Assert default values
        assertNull(dto.getNbsCaseAnswerUid());
        assertNull(dto.getNbsTableMetadataUid());
        assertNull(dto.getCode());
        assertNull(dto.getValue());
        assertNull(dto.getType());
        assertNull(dto.getOtherType());
        assertEquals(false, dto.isUpdateNbsQuestionUid());
    }

    @Test
    public void testParameterizedConstructor() {
        // Prepare a NbsCaseAnswer object for testing
        NbsCaseAnswer nbsCaseAnswer = new NbsCaseAnswer();
        nbsCaseAnswer.setActUid(1L);
        nbsCaseAnswer.setAddTime(new Timestamp(System.currentTimeMillis()));
        nbsCaseAnswer.setAddUserId(2L);
        nbsCaseAnswer.setAnswerTxt("Answer");
        nbsCaseAnswer.setNbsQuestionUid(3L);
        nbsCaseAnswer.setNbsQuestionVersionCtrlNbr(1);
        nbsCaseAnswer.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        nbsCaseAnswer.setLastChgUserId(4L);
        nbsCaseAnswer.setRecordStatusCd("Active");
        nbsCaseAnswer.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        nbsCaseAnswer.setSeqNbr(1);
        nbsCaseAnswer.setNbsTableMetadataUid(5L);
        nbsCaseAnswer.setNbsUiMetadataVerCtrlNbr(2);
        nbsCaseAnswer.setAnswerGroupSeqNbr(1);

        // Create an instance of NbsCaseAnswerDto using parameterized constructor
        NbsCaseAnswerDto dto = new NbsCaseAnswerDto(nbsCaseAnswer);

        // Assert values copied from nbsCaseAnswer
        assertEquals(nbsCaseAnswer.getActUid(), dto.getActUid());
        assertEquals(nbsCaseAnswer.getAddTime(), dto.getAddTime());
        assertEquals(nbsCaseAnswer.getAddUserId(), dto.getAddUserId());
        assertEquals(nbsCaseAnswer.getAnswerTxt(), dto.getAnswerTxt());
        assertEquals(nbsCaseAnswer.getNbsQuestionUid(), dto.getNbsQuestionUid());
        assertEquals(2, dto.getNbsQuestionVersionCtrlNbr());
        assertEquals(nbsCaseAnswer.getLastChgTime(), dto.getLastChgTime());
        assertEquals(nbsCaseAnswer.getLastChgUserId(), dto.getLastChgUserId());
        assertEquals(nbsCaseAnswer.getRecordStatusCd(), dto.getRecordStatusCd());
        assertEquals(nbsCaseAnswer.getRecordStatusTime(), dto.getRecordStatusTime());
        assertEquals(nbsCaseAnswer.getSeqNbr(), dto.getSeqNbr());
        assertEquals(nbsCaseAnswer.getNbsTableMetadataUid(), dto.getNbsTableMetadataUid());
        assertEquals(nbsCaseAnswer.getNbsUiMetadataVerCtrlNbr(), dto.getNbsQuestionVersionCtrlNbr());
        assertEquals(nbsCaseAnswer.getAnswerGroupSeqNbr(), dto.getAnswerGroupSeqNbr());
    }
}
