package gov.cdc.dataprocessing.test_data;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TestDataReader {

    public <T> T readDataFromJsonPath(String path, Class<T> type) {
        String resourcePath = "/test_data" + (path.startsWith("/") ? path : "/" + path);
        T data;

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found at path: " + resourcePath);
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                data = gson.fromJson(reader, type);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to load resource: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error processing JSON data: " + e.getMessage());
            throw new RuntimeException("Error processing JSON data", e);
        }

        return data;
    }


}
