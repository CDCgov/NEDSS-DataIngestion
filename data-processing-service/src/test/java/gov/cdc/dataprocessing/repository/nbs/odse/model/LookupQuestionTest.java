package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LookupQuestionTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        LookupQuestion lookupQuestion = new LookupQuestion();

        // Assert
        assertNull(lookupQuestion.getId());
        assertNull(lookupQuestion.getFromQuestionIdentifier());
        assertNull(lookupQuestion.getFromQuestionDisplayName());
        assertNull(lookupQuestion.getFromCodeSystemCd());
        assertNull(lookupQuestion.getFromCodeSystemDescTxt());
        assertNull(lookupQuestion.getFromDataType());
        assertNull(lookupQuestion.getFromCodeSet());
        assertNull(lookupQuestion.getFromFormCd());
        assertNull(lookupQuestion.getToQuestionIdentifier());
        assertNull(lookupQuestion.getToQuestionDisplayName());
        assertNull(lookupQuestion.getToCodeSystemCd());
        assertNull(lookupQuestion.getToCodeSystemDescTxt());
        assertNull(lookupQuestion.getToDataType());
        assertNull(lookupQuestion.getToCodeSet());
        assertNull(lookupQuestion.getToFormCd());
        assertNull(lookupQuestion.getRdbColumnNm());
        assertNull(lookupQuestion.getAddTime());
        assertNull(lookupQuestion.getAddUserId());
        assertNull(lookupQuestion.getLastChgTime());
        assertNull(lookupQuestion.getLastChgUserId());
        assertNull(lookupQuestion.getStatusCd());
        assertNull(lookupQuestion.getStatusTime());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LookupQuestion lookupQuestion = new LookupQuestion();

        Long id = 1L;
        String fromQuestionIdentifier = "fromQuestionIdentifier";
        String fromQuestionDisplayName = "fromQuestionDisplayName";
        String fromCodeSystemCd = "fromCodeSystemCd";
        String fromCodeSystemDescTxt = "fromCodeSystemDescTxt";
        String fromDataType = "fromDataType";
        String fromCodeSet = "fromCodeSet";
        String fromFormCd = "fromFormCd";
        String toQuestionIdentifier = "toQuestionIdentifier";
        String toQuestionDisplayName = "toQuestionDisplayName";
        String toCodeSystemCd = "toCodeSystemCd";
        String toCodeSystemDescTxt = "toCodeSystemDescTxt";
        String toDataType = "toDataType";
        String toCodeSet = "toCodeSet";
        String toFormCd = "toFormCd";
        String rdbColumnNm = "rdbColumnNm";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 1L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 2L;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());

        // Act
        lookupQuestion.setId(id);
        lookupQuestion.setFromQuestionIdentifier(fromQuestionIdentifier);
        lookupQuestion.setFromQuestionDisplayName(fromQuestionDisplayName);
        lookupQuestion.setFromCodeSystemCd(fromCodeSystemCd);
        lookupQuestion.setFromCodeSystemDescTxt(fromCodeSystemDescTxt);
        lookupQuestion.setFromDataType(fromDataType);
        lookupQuestion.setFromCodeSet(fromCodeSet);
        lookupQuestion.setFromFormCd(fromFormCd);
        lookupQuestion.setToQuestionIdentifier(toQuestionIdentifier);
        lookupQuestion.setToQuestionDisplayName(toQuestionDisplayName);
        lookupQuestion.setToCodeSystemCd(toCodeSystemCd);
        lookupQuestion.setToCodeSystemDescTxt(toCodeSystemDescTxt);
        lookupQuestion.setToDataType(toDataType);
        lookupQuestion.setToCodeSet(toCodeSet);
        lookupQuestion.setToFormCd(toFormCd);
        lookupQuestion.setRdbColumnNm(rdbColumnNm);
        lookupQuestion.setAddTime(addTime);
        lookupQuestion.setAddUserId(addUserId);
        lookupQuestion.setLastChgTime(lastChgTime);
        lookupQuestion.setLastChgUserId(lastChgUserId);
        lookupQuestion.setStatusCd(statusCd);
        lookupQuestion.setStatusTime(statusTime);

        // Assert
        assertEquals(id, lookupQuestion.getId());
        assertEquals(fromQuestionIdentifier, lookupQuestion.getFromQuestionIdentifier());
        assertEquals(fromQuestionDisplayName, lookupQuestion.getFromQuestionDisplayName());
        assertEquals(fromCodeSystemCd, lookupQuestion.getFromCodeSystemCd());
        assertEquals(fromCodeSystemDescTxt, lookupQuestion.getFromCodeSystemDescTxt());
        assertEquals(fromDataType, lookupQuestion.getFromDataType());
        assertEquals(fromCodeSet, lookupQuestion.getFromCodeSet());
        assertEquals(fromFormCd, lookupQuestion.getFromFormCd());
        assertEquals(toQuestionIdentifier, lookupQuestion.getToQuestionIdentifier());
        assertEquals(toQuestionDisplayName, lookupQuestion.getToQuestionDisplayName());
        assertEquals(toCodeSystemCd, lookupQuestion.getToCodeSystemCd());
        assertEquals(toCodeSystemDescTxt, lookupQuestion.getToCodeSystemDescTxt());
        assertEquals(toDataType, lookupQuestion.getToDataType());
        assertEquals(toCodeSet, lookupQuestion.getToCodeSet());
        assertEquals(toFormCd, lookupQuestion.getToFormCd());
        assertEquals(rdbColumnNm, lookupQuestion.getRdbColumnNm());
        assertEquals(addTime, lookupQuestion.getAddTime());
        assertEquals(addUserId, lookupQuestion.getAddUserId());
        assertEquals(lastChgTime, lookupQuestion.getLastChgTime());
        assertEquals(lastChgUserId, lookupQuestion.getLastChgUserId());
        assertEquals(statusCd, lookupQuestion.getStatusCd());
        assertEquals(statusTime, lookupQuestion.getStatusTime());
    }
}
