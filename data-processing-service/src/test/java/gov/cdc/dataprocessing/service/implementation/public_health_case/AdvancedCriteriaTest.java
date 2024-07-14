package gov.cdc.dataprocessing.service.implementation.public_health_case;


import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dsma_algorithm.*;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.utilities.component.public_health_case.AdvancedCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdvancedCriteriaTest {

    private AdvancedCriteria advancedCriteria;
    private Algorithm mockAlgorithm;

    @BeforeEach
    void setUp() {
        advancedCriteria = new AdvancedCriteria();
        mockAlgorithm = mock(Algorithm.class);
    }

    @Test
    void testGetAdvancedInvCriteriaMapWithValidData() throws DataProcessingException {
        InvCriteriaType invCriteriaType = mock(InvCriteriaType.class);
        InvValueType invValueType = mock(InvValueType.class);
        CodedType questionType = mock(CodedType.class);
        CodedType logicType = mock(CodedType.class);

        when(mockAlgorithm.getElrAdvancedCriteria()).thenReturn(mock(ElrAdvancedCriteriaType.class));
        when(mockAlgorithm.getElrAdvancedCriteria().getInvCriteria()).thenReturn(invCriteriaType);
        when(invCriteriaType.getInvValue()).thenReturn(Collections.singletonList(invValueType));
        when(invValueType.getInvQuestion()).thenReturn(questionType);
        when(invValueType.getInvQuestionLogic()).thenReturn(logicType);
        when(questionType.getCode()).thenReturn("Q1");
        when(logicType.getCode()).thenReturn("EQ");
        when(invValueType.getInvStringValue()).thenReturn("value1");
        when(invValueType.getInvCodedValue()).thenReturn(Collections.emptyList());

        Map<String, Object> result = advancedCriteria.getAdvancedInvCriteriaMap(mockAlgorithm);

        assertNotNull(result);
        assertTrue(result.containsKey("Q1"));
        EdxRuleManageDto dto = (EdxRuleManageDto) result.get("Q1");
        assertEquals("Q1", dto.getQuestionId());
        assertEquals("EQ", dto.getLogic());
        assertEquals("value1", dto.getValue());
    }

    @Test
    void testGetAdvancedInvCriteriaMapWithCodedValues() throws DataProcessingException {
        InvCriteriaType invCriteriaType = mock(InvCriteriaType.class);
        InvValueType invValueType = mock(InvValueType.class);
        CodedType questionType = mock(CodedType.class);
        CodedType logicType = mock(CodedType.class);
        CodedType codedValue = mock(CodedType.class);

        when(mockAlgorithm.getElrAdvancedCriteria()).thenReturn(mock(ElrAdvancedCriteriaType.class));
        when(mockAlgorithm.getElrAdvancedCriteria().getInvCriteria()).thenReturn(invCriteriaType);
        when(invCriteriaType.getInvValue()).thenReturn(Collections.singletonList(invValueType));
        when(invValueType.getInvQuestion()).thenReturn(questionType);
        when(invValueType.getInvQuestionLogic()).thenReturn(logicType);
        when(questionType.getCode()).thenReturn("Q2");
        when(logicType.getCode()).thenReturn("NE");
        when(invValueType.getInvStringValue()).thenReturn(null);
        when(invValueType.getInvCodedValue()).thenReturn(Arrays.asList(codedValue, codedValue));
        when(codedValue.getCode()).thenReturn("code1", "code2");

        Map<String, Object> result = advancedCriteria.getAdvancedInvCriteriaMap(mockAlgorithm);

        assertNotNull(result);
        assertTrue(result.containsKey("Q2"));
        EdxRuleManageDto dto = (EdxRuleManageDto) result.get("Q2");
        assertEquals("Q2", dto.getQuestionId());
        assertEquals("NE", dto.getLogic());
        assertEquals("code1,code2", dto.getValue());
    }

    @Test
    void testGetAdvancedInvCriteriaMapWithException() {
        when(mockAlgorithm.getElrAdvancedCriteria()).thenThrow(new RuntimeException("Test Exception"));

        Executable executable = () -> advancedCriteria.getAdvancedInvCriteriaMap(mockAlgorithm);

        DataProcessingException exception = assertThrows(DataProcessingException.class, executable);
        assertEquals("Exception while creating advanced Investigation Criteria Map: ", exception.getMessage());
    }
}
