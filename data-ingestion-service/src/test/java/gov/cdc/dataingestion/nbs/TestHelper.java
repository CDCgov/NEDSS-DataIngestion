package gov.cdc.dataingestion.nbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
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
