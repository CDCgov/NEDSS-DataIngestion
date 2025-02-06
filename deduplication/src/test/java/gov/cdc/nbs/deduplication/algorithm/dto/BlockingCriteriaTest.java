package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlockingCriteriaTest {

    @Test
    void testBlockingCriteria() {
        // Use the correct constructor for Field and Method
        Field field = new Field("FIRST_NAME", "STRING");  // Field expects both name and type
        Method method = new Method("exact", "matcher");  // Method expects both value and name

        // Create the BlockingCriteria instance
        BlockingCriteria blockingCriteria = new BlockingCriteria(field, method);

        // Assert that the Field and Method values are correctly set
        assertEquals("FIRST_NAME", blockingCriteria.field().value());
        assertEquals("STRING", blockingCriteria.field().name());  // Check name as well
        assertEquals("exact", blockingCriteria.method().value());
        assertEquals("matcher", blockingCriteria.method().name());  // Check name as well
    }
}
