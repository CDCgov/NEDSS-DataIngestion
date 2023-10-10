package gov.cdc.dataingestion.nbs.ecr.service.helper;

import java.util.ArrayList;
import java.util.List;

public class CdaMapStringHelper {
    private CdaMapStringHelper() {
        // String helper class
    }
    public static List<String> getStringsBeforePipe(String input) {
        List<String> result = new ArrayList<>();
        String[] parts = input.split("\\|");

        // The split function will naturally split the String before every "|", so we just need to return the parts.
        for (String part : parts) {
            result.add(part.trim());  // Using trim to remove any leading or trailing whitespaces.
        }

        return result;
    }

    public static List<String> getStringsBeforeCaret(String input) {
        List<String> result = new ArrayList<>();
        String[] parts = input.split("\\^");

        // The split function will naturally split the String before every "|", so we just need to return the parts.
        for (String part : parts) {
            result.add(part.trim());  // Using trim to remove any leading or trailing whitespaces.
        }

        return result;
    }


}
