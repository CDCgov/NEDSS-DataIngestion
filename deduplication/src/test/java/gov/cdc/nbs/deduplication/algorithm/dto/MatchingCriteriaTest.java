package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchingCriteriaTest {

    @Test
    void testConstructorAndGetters() {
        // Create Field and Method instances
        Field field = new Field("BIRTHDATE", "STRING");
        Method method = new Method("exact", "matcher");

        // Create MatchingCriteria using the constructor
        MatchingCriteria matchingCriteria = new MatchingCriteria(field, method);

        // Assert that the field and method are correctly set
        assertNotNull(matchingCriteria.field());
        assertEquals("BIRTHDATE", matchingCriteria.field().value());
        assertEquals("STRING", matchingCriteria.field().name());

        assertNotNull(matchingCriteria.method());
        assertEquals("exact", matchingCriteria.method().value());
        assertEquals("matcher", matchingCriteria.method().name());
    }

    @Test
    void testFullObject() {
        // Create and set Field
        Field field = new Field("ADDRESS", "STRING");

        // Create and set Method
        Method method = new Method("compare_match_all", "matcher");

        // Create MatchingCriteria using the constructor
        MatchingCriteria matchingCriteria = new MatchingCriteria(field, method);

        // Validate that the field and method are correctly set and retrieved
        assertNotNull(matchingCriteria.field());
        assertEquals("ADDRESS", matchingCriteria.field().value());
        assertEquals("STRING", matchingCriteria.field().name());

        assertNotNull(matchingCriteria.method());
        assertEquals("compare_match_all", matchingCriteria.method().value());
        assertEquals("matcher", matchingCriteria.method().name());
    }
}
