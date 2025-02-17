package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PassTest {

    @Test
    void testPass() {
        Field field = new Field("FIRST_NAME", "STRING");  // Pass both parameters
        Method method = new Method("exact", "matcher");  // Pass both parameters
        BlockingCriteria blockingCriteria = new BlockingCriteria(field, method);
        MatchingCriteria matchingCriteria = new MatchingCriteria(new Field("LAST_NAME", "STRING"), new Method("exact", "matcher"));

        Pass pass = new Pass("Test Name", "Test Description", "0.1", "0.9",
                List.of(blockingCriteria), List.of(matchingCriteria));

        assertEquals("Test Name", pass.name());
        assertEquals("Test Description", pass.description());
        assertEquals("0.1", pass.lowerBound());
        assertEquals("0.9", pass.upperBound());
        assertEquals(1, pass.blockingCriteria().size());
        assertEquals("STRING", pass.blockingCriteria().get(0).field().name());
        assertEquals("exact", pass.blockingCriteria().get(0).method().value());
        assertEquals("matcher", pass.blockingCriteria().get(0).method().name());  // Validate name argument
        assertEquals(1, pass.matchingCriteria().size());
        assertEquals("STRING", pass.matchingCriteria().get(0).field().name());
        assertEquals("exact", pass.matchingCriteria().get(0).method().value());
    }
}
