package gov.cdc.dataingestion.nbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class TestHelper {

    public static String testFileReading() throws IOException {
        var file = new File("src/test/resources/xmlData.txt").getAbsolutePath();

        String result = "";
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            return result;

        } catch (IOException e) {
            throw new IOException("An error occurred while reading the file: " + e.getMessage());
        }
    }
}
