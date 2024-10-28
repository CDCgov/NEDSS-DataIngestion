package gov.cdc.dataprocessing.test_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.cdc.dataprocessing.model.phdc.Container;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class TestDataReader {
    private static final String DATE_FORMAT = "MMM d, yyyy, h:mm:ss a"; // Match the date format in JSON

    public static Gson gsonForTest() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();

    }

    public <T> T readDataFromJsonPath(String path, Class<T> type) {
        String resourcePath = "/test_data" + (path.startsWith("/") ? path : "/" + path);
        T data;

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found at path: " + resourcePath);
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Gson gson =  new GsonBuilder()
                        .setDateFormat(DATE_FORMAT)
                        .create();


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
    public String readDataFromXmlPath(String path) {
        String resourcePath = "/test_data" + (path.startsWith("/") ? path : "/" + path);
        StringBuilder data = new StringBuilder();

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found at path: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line).append("\n");
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to load resource: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error processing XML data: " + e.getMessage());
            throw new RuntimeException("Error processing XML data", e);
        }

        return data.toString();
    }

    public Container convertXmlStrToContainer(String payload) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Container.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(payload);
        return (Container) unmarshaller.unmarshal(reader);
    }








}
