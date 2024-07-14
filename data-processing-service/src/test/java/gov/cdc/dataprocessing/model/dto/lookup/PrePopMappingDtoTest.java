package gov.cdc.dataprocessing.model.dto.lookup;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class PrePopMappingDtoTest {

    @Test
    public void testConstructor() {
        // Create a LookupMappingDto instance with sample data
        LookupMappingDto lookupMappingDto = new LookupMappingDto();
        lookupMappingDto.setLookupQuestionUid(1L);
        lookupMappingDto.setLookupAnswerUid(2L);
        lookupMappingDto.setFromQuestionIdentifier("fromQuestion");
        lookupMappingDto.setFromCodeSystemCd("fromCodeSystem");
        lookupMappingDto.setFromDataType("fromDataType");
        lookupMappingDto.setFromFormCd("fromForm");
        lookupMappingDto.setToFormCd("toForm");
        lookupMappingDto.setToQuestionIdentifier("toQuestion");
        lookupMappingDto.setToCodeSystemCd("toCodeSystem");
        lookupMappingDto.setToDataType("toDataType");
        lookupMappingDto.setFromAnswerCode("fromAnswer");
        lookupMappingDto.setFromAnsCodeSystemCd("fromAnsCodeSystem");
        lookupMappingDto.setToAnswerCode("toAnswer");
        lookupMappingDto.setToAnsCodeSystemCd("toAnsCodeSystem");

        // Create a PrePopMappingDto instance using the constructor with LookupMappingDto parameter
        PrePopMappingDto prePopMappingDto = new PrePopMappingDto(lookupMappingDto);

        // Verify that attributes are correctly initialized
        assertEquals(1L, prePopMappingDto.getLookupQuestionUid());
        assertEquals(2L, prePopMappingDto.getLookupAnswerUid());
        assertEquals("fromQuestion", prePopMappingDto.getFromQuestionIdentifier());
        assertEquals("fromCodeSystem", prePopMappingDto.getFromCodeSystemCode());
        assertEquals("fromDataType", prePopMappingDto.getFromDataType());
        assertEquals("fromForm", prePopMappingDto.getFromFormCd());
        assertEquals("toForm", prePopMappingDto.getToFormCd());
        assertEquals("toQuestion", prePopMappingDto.getToQuestionIdentifier());
        assertEquals("toCodeSystem", prePopMappingDto.getToCodeSystemCd());
        assertEquals("toDataType", prePopMappingDto.getToDataType());
        assertEquals("fromAnswer", prePopMappingDto.getFromAnswerCode());
        assertEquals("fromAnsCodeSystem", prePopMappingDto.getFromAnsCodeSystemCd());
        assertEquals("toAnswer", prePopMappingDto.getToAnswerCode());
        assertEquals("toAnsCodeSystem", prePopMappingDto.getToAnsCodeSystemCd());
    }

    @Test
    public void testDeepCopy() throws CloneNotSupportedException, IOException, ClassNotFoundException {
        // Create a PrePopMappingDto instance with sample data
        PrePopMappingDto original = new PrePopMappingDto();
        original.setLookupQuestionUid(1L);
        original.setLookupAnswerUid(2L);
        original.setFromQuestionIdentifier("fromQuestion");
        original.setFromCodeSystemCode("fromCodeSystem");
        original.setFromDataType("fromDataType");
        original.setFromFormCd("fromForm");
        original.setToFormCd("toForm");
        original.setToQuestionIdentifier("toQuestion");
        original.setToCodeSystemCd("toCodeSystem");
        original.setToDataType("toDataType");
        original.setFromAnswerCode("fromAnswer");
        original.setFromAnsCodeSystemCd("fromAnsCodeSystem");
        original.setToAnswerCode("toAnswer");
        original.setToAnsCodeSystemCd("toAnsCodeSystem");

        // Perform deep copy
        PrePopMappingDto deepCopy = (PrePopMappingDto) original.deepCopy();

        // Verify that the deep copy is not the same object reference
        assertNotSame(original, deepCopy);

        // Verify that the deep copy has the same attribute values as the original
        assertEquals(original.getLookupQuestionUid(), deepCopy.getLookupQuestionUid());
        assertEquals(original.getLookupAnswerUid(), deepCopy.getLookupAnswerUid());
        assertEquals(original.getFromQuestionIdentifier(), deepCopy.getFromQuestionIdentifier());
        assertEquals(original.getFromCodeSystemCode(), deepCopy.getFromCodeSystemCode());
        assertEquals(original.getFromDataType(), deepCopy.getFromDataType());
        assertEquals(original.getFromFormCd(), deepCopy.getFromFormCd());
        assertEquals(original.getToFormCd(), deepCopy.getToFormCd());
        assertEquals(original.getToQuestionIdentifier(), deepCopy.getToQuestionIdentifier());
        assertEquals(original.getToCodeSystemCd(), deepCopy.getToCodeSystemCd());
        assertEquals(original.getToDataType(), deepCopy.getToDataType());
        assertEquals(original.getFromAnswerCode(), deepCopy.getFromAnswerCode());
        assertEquals(original.getFromAnsCodeSystemCd(), deepCopy.getFromAnsCodeSystemCd());
        assertEquals(original.getToAnswerCode(), deepCopy.getToAnswerCode());
        assertEquals(original.getToAnsCodeSystemCd(), deepCopy.getToAnsCodeSystemCd());
    }
}
