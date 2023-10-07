package gov.cdc.dataingestion.nbs.ecr.service.helper;

import java.util.ArrayList;
import java.util.List;

public class CdaMapStringHelper {
    public static List<String> GetStringsBeforePipe(String input) {
        List<String> result = new ArrayList<>();
        String[] parts = input.split("\\|");

        // The split function will naturally split the String before every "|", so we just need to return the parts.
        for (String part : parts) {
            result.add(part.trim());  // Using trim to remove any leading or trailing whitespaces.
        }

        return result;
    }

    public static List<String> GetStringsBeforeCaret(String input) {
        List<String> result = new ArrayList<>();
        String[] parts = input.split("\\^");

        // The split function will naturally split the String before every "|", so we just need to return the parts.
        for (String part : parts) {
            result.add(part.trim());  // Using trim to remove any leading or trailing whitespaces.
        }

        return result;
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
