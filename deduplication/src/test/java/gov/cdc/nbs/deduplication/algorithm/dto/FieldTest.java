package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void testConstructorAndGetters() {
        // Create a Field instance using the constructor
        Field field = new Field("BIRTHDATE", "STRING");

        // Assert that the values are correctly set
        assertNotNull(field);
        assertEquals("BIRTHDATE", field.value());
        assertEquals("STRING", field.name());
    }
}
