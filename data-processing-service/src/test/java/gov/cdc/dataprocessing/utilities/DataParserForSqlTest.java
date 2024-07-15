package gov.cdc.dataprocessing.utilities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataParserForSqlTest {

    @Test
    void testParseValue_Long() {
        Long expected = 123L;
        Long result = DataParserForSql.parseValue("123", Long.class);
        assertEquals(expected, result);
    }

    @Test
    void testParseValue_String() {
        String expected = "test";
        String result = DataParserForSql.parseValue("test", String.class);
        assertEquals(expected, result);
    }

    @Test
    void testParseValue_Timestamp() {
        Timestamp expected = Timestamp.valueOf("2023-07-14 12:00:00");
        Timestamp result = DataParserForSql.parseValue("2023-07-14 12:00:00", Timestamp.class);
        assertEquals(expected, result);
    }

    @Test
    void testParseValue_Integer() {
        Integer expected = 123;
        Integer result = DataParserForSql.parseValue("123", Integer.class);
        assertEquals(expected, result);
    }

    @Test
    void testParseValue_BigDecimal() {
        BigDecimal expected = new BigDecimal("123.45");
        BigDecimal result = DataParserForSql.parseValue("123.45", BigDecimal.class);
        assertEquals(expected, result);
    }

    @Test
    void testParseValue_Null() {
        assertNull(DataParserForSql.parseValue(null, String.class));
    }

    @Test
    void testDataNotNull_NonNull() {
        assertTrue(DataParserForSql.dataNotNull("test"));
    }

    @Test
    void testDataNotNull_Null() {
        assertFalse(DataParserForSql.dataNotNull(null));
    }

    @Test
    void testResultValidCheck_NonEmptyList() {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, "test"});
        assertTrue(DataParserForSql.resultValidCheck(results));
    }

    @Test
    void testResultValidCheck_EmptyList() {
        List<Object[]> results = Collections.emptyList();
        assertFalse(DataParserForSql.resultValidCheck(results));
    }

    @Test
    void testResultValidCheck_NullList() {
        assertFalse(DataParserForSql.resultValidCheck(null));
    }
}
