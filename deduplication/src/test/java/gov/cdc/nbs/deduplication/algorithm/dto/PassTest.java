package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PassTest {

    @Test
    void testSetAndGetName() {
        Pass pass = new Pass();
        pass.setName("Pass 1");

        assertEquals("Pass 1", pass.getName());
    }

    @Test
    void testSetAndGetDescription() {
        Pass pass = new Pass();
        pass.setDescription("This is a description");

        assertEquals("This is a description", pass.getDescription());
    }

    @Test
    void testSetAndGetLowerBound() {
        Pass pass = new Pass();
        pass.setLowerBound("0.1");

        assertEquals("0.1", pass.getLowerBound());
    }

    @Test
    void testSetAndGetUpperBound() {
        Pass pass = new Pass();
        pass.setUpperBound("0.9");

        assertEquals("0.9", pass.getUpperBound());
    }

    @Test
    void testSetAndGetBlockingCriteria() {
        Pass pass = new Pass();
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        blockingCriteria.setField(new Field());
        blockingCriteria.getField().setName("BIRTHDATE");

        pass.setBlockingCriteria(List.of(blockingCriteria));

        assertNotNull(pass.getBlockingCriteria());
        assertEquals(1, pass.getBlockingCriteria().size());
        assertEquals("BIRTHDATE", pass.getBlockingCriteria().get(0).getField().getName());
    }

    @Test
    void testSetAndGetMatchingCriteria() {
        Pass pass = new Pass();
        MatchingCriteria matchingCriteria = new MatchingCriteria();
        matchingCriteria.setField(new Field());
        matchingCriteria.getField().setName("FIRST_NAME");
        matchingCriteria.setMethod(new Method());
        matchingCriteria.getMethod().setValue("exact");

        pass.setMatchingCriteria(List.of(matchingCriteria));

        assertNotNull(pass.getMatchingCriteria());
        assertEquals(1, pass.getMatchingCriteria().size());
        assertEquals("FIRST_NAME", pass.getMatchingCriteria().get(0).getField().getName());
        assertEquals("exact", pass.getMatchingCriteria().get(0).getMethod().getValue());
    }

    @Test
    void testFullObject() {
        Pass pass = new Pass();
        pass.setName("Test Pass");
        pass.setDescription("This is a full object test");
        pass.setLowerBound("0.2");
        pass.setUpperBound("0.8");

        // Create BlockingCriteria
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        blockingCriteria.setField(new Field());
        blockingCriteria.getField().setName("ADDRESS");

        // Create MatchingCriteria
        MatchingCriteria matchingCriteria = new MatchingCriteria();
        matchingCriteria.setField(new Field());
        matchingCriteria.getField().setName("LAST_NAME");
        matchingCriteria.setMethod(new Method());
        matchingCriteria.getMethod().setValue("jarowinkler");

        pass.setBlockingCriteria(List.of(blockingCriteria));
        pass.setMatchingCriteria(List.of(matchingCriteria));

        assertNotNull(pass.getName());
        assertEquals("Test Pass", pass.getName());

        assertNotNull(pass.getDescription());
        assertEquals("This is a full object test", pass.getDescription());

        assertNotNull(pass.getLowerBound());
        assertEquals("0.2", pass.getLowerBound());

        assertNotNull(pass.getUpperBound());
        assertEquals("0.8", pass.getUpperBound());

        assertNotNull(pass.getBlockingCriteria());
        assertEquals(1, pass.getBlockingCriteria().size());
        assertEquals("ADDRESS", pass.getBlockingCriteria().get(0).getField().getName());

        assertNotNull(pass.getMatchingCriteria());
        assertEquals(1, pass.getMatchingCriteria().size());
        assertEquals("LAST_NAME", pass.getMatchingCriteria().get(0).getField().getName());
        assertEquals("jarowinkler", pass.getMatchingCriteria().get(0).getMethod().getValue());
    }
}

