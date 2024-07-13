package gov.cdc.dataprocessing.model.dto.lookup;

import gov.cdc.dataprocessing.model.dto.lookup.LookupMappingDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestionExtended;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LookupMappingDtoTest {

    @Test
    public void testGetterSetter() {
        // Create an instance of LookupQuestionExtended to use in the constructor
        LookupQuestionExtended data = new LookupQuestionExtended();
        data.setId(1L);
        data.setFromQuestionIdentifier("fromQuestion");
        data.setFromCodeSystemCd("fromCodeSystem");
        data.setFromDataType("fromDataType");
        data.setFromFormCd("fromForm");
        data.setToFormCd("toForm");
        data.setToQuestionIdentifier("toQuestion");
        data.setToCodeSystemCd("toCodeSystem");
        data.setToDataType("toDataType");
        data.setLookupAnswerUid(2L);
        data.setFromAnswerCode("fromAnswer");
        data.setFromAnsCodeSystemCd("fromAnsCodeSystem");
        data.setToAnswerCode("toAnswer");
        data.setToAnsCodeSystemCd("toAnsCodeSystem");

        // Create an instance of LookupMappingDto using the constructor with LookupQuestionExtended parameter
        LookupMappingDto dto = new LookupMappingDto(data);

        // Test getter methods
        assertEquals(1L, dto.getLookupQuestionUid());
        assertEquals("fromQuestion", dto.getFromQuestionIdentifier());
        assertEquals("fromCodeSystem", dto.getFromCodeSystemCd());
        assertEquals("fromDataType", dto.getFromDataType());
        assertEquals("fromForm", dto.getFromFormCd());
        assertEquals("toForm", dto.getToFormCd());
        assertEquals("toQuestion", dto.getToQuestionIdentifier());
        assertEquals("toCodeSystem", dto.getToCodeSystemCd());
        assertEquals("toDataType", dto.getToDataType());
        assertEquals(2L, dto.getLookupAnswerUid());
        assertEquals("fromAnswer", dto.getFromAnswerCode());
        assertEquals("fromAnsCodeSystem", dto.getFromAnsCodeSystemCd());
        assertEquals("toAnswer", dto.getToAnswerCode());
        assertEquals("toAnsCodeSystem", dto.getToAnsCodeSystemCd());

        // Test default values
        assertNotNull(dto.getLookupAnswerUid()); // assuming the default value for Long is null if not set
    }
}
