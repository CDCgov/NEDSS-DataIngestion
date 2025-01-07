package gov.cdc.dataingestion.nbs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestHelper {

    public static String testFileReading() throws IOException {
        String path = new File("src/test/resources/xmlData.txt").getAbsolutePath();
        return Files.readString(Paths.get(path));
    }
}
