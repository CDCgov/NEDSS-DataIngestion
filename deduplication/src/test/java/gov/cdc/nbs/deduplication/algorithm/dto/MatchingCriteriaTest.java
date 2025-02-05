package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchingCriteriaTest {

    @Test
    void testSetAndGetField() {
        MatchingCriteria matchingCriteria = new MatchingCriteria();
        Field field = new Field();
        field.setName("BIRTHDATE");

        matchingCriteria.setField(field);

        assertNotNull(matchingCriteria.getField());
        assertEquals("BIRTHDATE", matchingCriteria.getField().getName());
    }

    @Test
    void testSetAndGetMethod() {
        MatchingCriteria matchingCriteria = new MatchingCriteria();
        Method method = new Method();
        method.setValue("exact");

        matchingCriteria.setMethod(method);

        assertNotNull(matchingCriteria.getMethod());
        assertEquals("exact", matchingCriteria.getMethod().getValue());
    }

    @Test
    void testFullObject() {
        MatchingCriteria matchingCriteria = new MatchingCriteria();

        // Create and set Field
        Field field = new Field();
        field.setName("ADDRESS");
        matchingCriteria.setField(field);

        // Create and set Method
        Method method = new Method();
        method.setValue("compare_match_all");
        matchingCriteria.setMethod(method);

        // Validate that the field and method are correctly set and retrieved
        assertNotNull(matchingCriteria.getField());
        assertEquals("ADDRESS", matchingCriteria.getField().getName());

        assertNotNull(matchingCriteria.getMethod());
        assertEquals("compare_match_all", matchingCriteria.getMethod().getValue());
    }
}

