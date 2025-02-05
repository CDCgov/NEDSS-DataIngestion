package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockingCriteriaTest {

    @Test
    void testSetAndGetField() {
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("BIRTHDATE");

        blockingCriteria.setField(field);

        assertNotNull(blockingCriteria.getField());
        assertEquals("BIRTHDATE", blockingCriteria.getField().getName());
    }

    @Test
    void testSetAndGetMethod() {
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Method method = new Method();
        method.setValue("exact");

        blockingCriteria.setMethod(method);

        assertNotNull(blockingCriteria.getMethod());
        assertEquals("exact", blockingCriteria.getMethod().getValue());
    }

    @Test
    void testFullObject() {
        BlockingCriteria blockingCriteria = new BlockingCriteria();

        Field field = new Field();
        field.setName("ADDRESS");
        blockingCriteria.setField(field);

        Method method = new Method();
        method.setValue("compare_match_any");
        blockingCriteria.setMethod(method);

        assertNotNull(blockingCriteria.getField());
        assertEquals("ADDRESS", blockingCriteria.getField().getName());

        assertNotNull(blockingCriteria.getMethod());
        assertEquals("compare_match_any", blockingCriteria.getMethod().getValue());
    }
}

