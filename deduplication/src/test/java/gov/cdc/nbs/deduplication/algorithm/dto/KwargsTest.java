package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KwargsTest {

    @Test
    public void testKwargs() {
        Kwargs kwargs = new Kwargs("JaroWinkler", Map.of("LAST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );
        assertNotNull(kwargs);
        assertEquals("JaroWinkler",kwargs.similarityMeasure());
    }
}
