package gov.cdc.dataprocessing.model.dto.edx;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdxRuleManageDtoTest {

    @Test
    void testGettersAndSetters() {
        EdxRuleManageDto dto = new EdxRuleManageDto();

        String value = "value";
        Collection<Object> defaultCodedValueColl = new ArrayList<>();
        String defaultNumericValue = "defaultNumericValue";
        String defaultStringValue = "defaultStringValue";
        String behavior = "behavior";
        Long dsmAlgorithmUid = 1L;
        String defaultCommentValue = "defaultCommentValue";
        String logic = "logic";
        String questionId = "questionId";
        boolean isAdvanceCriteria = true;
        String type = "type";
        String participationTypeCode = "participationTypeCode";
        String participationClassCode = "participationClassCode";
        Long participationUid = 2L;

        dto.setValue(value);
        dto.setDefaultCodedValueColl(defaultCodedValueColl);
        dto.setDefaultNumericValue(defaultNumericValue);
        dto.setDefaultStringValue(defaultStringValue);
        dto.setBehavior(behavior);
        dto.setDsmAlgorithmUid(dsmAlgorithmUid);
        dto.setDefaultCommentValue(defaultCommentValue);
        dto.setLogic(logic);
        dto.setQuestionId(questionId);
        dto.setAdvanceCriteria(isAdvanceCriteria);
        dto.setType(type);
        dto.setParticipationTypeCode(participationTypeCode);
        dto.setParticipationClassCode(participationClassCode);
        dto.setParticipationUid(participationUid);

        assertEquals(value, dto.getValue());
        assertEquals(defaultCodedValueColl, dto.getDefaultCodedValueColl());
        assertEquals(defaultNumericValue, dto.getDefaultNumericValue());
        assertEquals(defaultStringValue, dto.getDefaultStringValue());
        assertEquals(behavior, dto.getBehavior());
        assertEquals(dsmAlgorithmUid, dto.getDsmAlgorithmUid());
        assertEquals(defaultCommentValue, dto.getDefaultCommentValue());
        assertEquals(logic, dto.getLogic());
        assertEquals(questionId, dto.getQuestionId());
        assertTrue(dto.isAdvanceCriteria());
        assertEquals(type, dto.getType());
        assertEquals(participationTypeCode, dto.getParticipationTypeCode());
        assertEquals(participationClassCode, dto.getParticipationClassCode());
        assertEquals(participationUid, dto.getParticipationUid());
    }
}
