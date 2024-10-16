package gov.cdc.dataprocessing.service.implementation.person;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@Getter
public class DibbsMatchService {
  private final ObjectMapper mapper = new ObjectMapper();
  private final EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();

  @Value("${converterApiUrl}")
  private String converterApiUrl;

  public boolean match(PersonContainer personContainer) throws InterruptedException {
    try {
      return callDIBBSConverterApi(personContainer);
    } catch (InterruptedException e) {
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
  }

  boolean callDIBBSConverterApi(PersonContainer personContainer) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
    HttpRequest request = createHttpRequest(personContainer);
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return handleResponse(response);
  }

  HttpRequest createHttpRequest(PersonContainer personContainer) throws IOException {
    String requestJson = mapper.writeValueAsString(personContainer);
    return HttpRequest.newBuilder()
        .uri(URI.create(converterApiUrl + "person/match"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
        .build();
  }

  boolean handleResponse(HttpResponse<String> response) throws IOException {
    boolean foundMatch = false;
    if (response.statusCode() == 200) {
      log.info(response.body());
      JsonNode jsonNode = mapper.readTree(response.body());
      String foundMatchStr = jsonNode.get("found_match").asText();
      foundMatch = Boolean.parseBoolean(foundMatchStr);
      setPersonId(jsonNode);
    }
    return foundMatch;
  }

  private void setPersonId(JsonNode jsonNode) {
    JsonNode updatedBundleNode = jsonNode.get("updated_bundle");
    if (updatedBundleNode != null) {
      JsonNode entryArray = updatedBundleNode.get("entry");
      if (entryArray != null && entryArray.isArray()) {
        for (JsonNode entryNode : entryArray) {
          JsonNode resourceNode = entryNode.get("resource");
          if (resourceNode != null && "Person".equals(resourceNode.get("resourceType").asText())) {
            edxPatientMatchFoundDT.setPatientUid(Long.valueOf(resourceNode.get("id").asText()));
          }
        }
      }
    }
  }
}
