package gov.cdc.dataingestion.nbs.ecr.service.util;

public class StringHelper {
    public static String convertToSnakeCase(String input) {
        // Replace each upper case letter with an underscore followed by the lower case version of the letter
        String result = input.replaceAll("([a-z])([A-Z]+)", "$1_$2");
        // Convert the entire string to upper case
        result = result.toUpperCase();
        return result;
    }
}
