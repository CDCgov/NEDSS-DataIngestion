package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void testSetAndGetValue() {
        Field field = new Field();
        field.setValue("123456");

        assertEquals("123456", field.getValue());
    }

    @Test
    void testSetAndGetName() {
        Field field = new Field();
        field.setName("BIRTHDATE");

        assertEquals("BIRTHDATE", field.getName());
    }

    @Test
    void testFullObject() {
        Field field = new Field();
        field.setValue("123456");
        field.setName("BIRTHDATE");

        assertNotNull(field.getValue());
        assertEquals("123456", field.getValue());

        assertNotNull(field.getName());
        assertEquals("BIRTHDATE", field.getName());
    }
}

