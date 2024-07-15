package gov.cdc.dataprocessing.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RulesEngineUtilTest {

    @Test
    void testCalcMMWR_ValidDate() {
        String date = "01/01/2022";
        int[] expected = {52, 2021}; // MMWR week 52 of the previous year because the week starts on Sunday
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }

    @Test
    void testCalcMMWR_ValidDate2() {
        String date = "12/31/2022";
        int[] expected = {52, 2022}; // MMWR week 52 of the current year
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }

    @Test
    void testCalcMMWR_BeginningOfYear() {
        String date = "01/01/2021";
        int[] expected = {53, 2020}; // MMWR week 52 of the previous year because the week starts on Sunday
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }

    @Test
    void testCalcMMWR_EndOfYear() {
        String date = "12/31/2021";
        int[] expected = {52, 2021}; // MMWR week 52 of the current year
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }

    @Test
    void testCalcMMWR_NullDate() {
        String date = null;
        int[] expected = {0, 0};
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }

    @Test
    void testCalcMMWR_EmptyString() {
        String date = "";
        int[] expected = {0, 0};
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }

    @Test
    void testCalcMMWR_InvalidDate() {
        String date = "invalid date";
        int[] expected = {0, 0};
        int[] result = RulesEngineUtil.CalcMMWR(date);
        assertArrayEquals(expected, result);
    }
}
