package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LookupAnswerTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        LookupAnswer lookupAnswer = new LookupAnswer();

        // Assert
        assertNull(lookupAnswer.getId());
        assertNull(lookupAnswer.getLookupQuestion());
        assertNull(lookupAnswer.getFromAnswerCode());
        assertNull(lookupAnswer.getFromAnsDisplayNm());
        assertNull(lookupAnswer.getFromCodeSystemCd());
        assertNull(lookupAnswer.getFromCodeSystemDescTxt());
        assertNull(lookupAnswer.getToAnswerCode());
        assertNull(lookupAnswer.getToAnsDisplayNm());
        assertNull(lookupAnswer.getToCodeSystemCd());
        assertNull(lookupAnswer.getToCodeSystemDescTxt());
        assertNull(lookupAnswer.getAddTime());
        assertNull(lookupAnswer.getAddUserId());
        assertNull(lookupAnswer.getLastChgTime());
        assertNull(lookupAnswer.getLastChgUserId());
        assertNull(lookupAnswer.getStatusCd());
        assertNull(lookupAnswer.getStatusTime());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LookupAnswer lookupAnswer = new LookupAnswer();
        LookupQuestion lookupQuestion = new LookupQuestion();
        lookupQuestion.setId(1L);

        Long id = 1L;
        String fromAnswerCode = "fromAnswerCode";
        String fromAnsDisplayNm = "fromAnsDisplayNm";
        String fromCodeSystemCd = "fromCodeSystemCd";
        String fromCodeSystemDescTxt = "fromCodeSystemDescTxt";
        String toAnswerCode = "toAnswerCode";
        String toAnsDisplayNm = "toAnsDisplayNm";
        String toCodeSystemCd = "toCodeSystemCd";
        String toCodeSystemDescTxt = "toCodeSystemDescTxt";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 1L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 2L;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());

        // Act
        lookupAnswer.setId(id);
        lookupAnswer.setLookupQuestion(lookupQuestion);
        lookupAnswer.setFromAnswerCode(fromAnswerCode);
        lookupAnswer.setFromAnsDisplayNm(fromAnsDisplayNm);
        lookupAnswer.setFromCodeSystemCd(fromCodeSystemCd);
        lookupAnswer.setFromCodeSystemDescTxt(fromCodeSystemDescTxt);
        lookupAnswer.setToAnswerCode(toAnswerCode);
        lookupAnswer.setToAnsDisplayNm(toAnsDisplayNm);
        lookupAnswer.setToCodeSystemCd(toCodeSystemCd);
        lookupAnswer.setToCodeSystemDescTxt(toCodeSystemDescTxt);
        lookupAnswer.setAddTime(addTime);
        lookupAnswer.setAddUserId(addUserId);
        lookupAnswer.setLastChgTime(lastChgTime);
        lookupAnswer.setLastChgUserId(lastChgUserId);
        lookupAnswer.setStatusCd(statusCd);
        lookupAnswer.setStatusTime(statusTime);

        // Assert
        assertEquals(id, lookupAnswer.getId());
        assertEquals(lookupQuestion, lookupAnswer.getLookupQuestion());
        assertEquals(fromAnswerCode, lookupAnswer.getFromAnswerCode());
        assertEquals(fromAnsDisplayNm, lookupAnswer.getFromAnsDisplayNm());
        assertEquals(fromCodeSystemCd, lookupAnswer.getFromCodeSystemCd());
        assertEquals(fromCodeSystemDescTxt, lookupAnswer.getFromCodeSystemDescTxt());
        assertEquals(toAnswerCode, lookupAnswer.getToAnswerCode());
        assertEquals(toAnsDisplayNm, lookupAnswer.getToAnsDisplayNm());
        assertEquals(toCodeSystemCd, lookupAnswer.getToCodeSystemCd());
        assertEquals(toCodeSystemDescTxt, lookupAnswer.getToCodeSystemDescTxt());
        assertEquals(addTime, lookupAnswer.getAddTime());
        assertEquals(addUserId, lookupAnswer.getAddUserId());
        assertEquals(lastChgTime, lookupAnswer.getLastChgTime());
        assertEquals(lastChgUserId, lookupAnswer.getLastChgUserId());
        assertEquals(statusCd, lookupAnswer.getStatusCd());
        assertEquals(statusTime, lookupAnswer.getStatusTime());
    }
}
