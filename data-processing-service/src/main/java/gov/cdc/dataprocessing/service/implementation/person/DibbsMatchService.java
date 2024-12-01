package gov.cdc.dataprocessing.service.implementation.person;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.container.model.dibbs.DibbsRequestBodyDto;
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
  private  EdxPatientMatchDto edxPatientMatchFoundDT ;

  @Value("${dedupMatchUrl}")
  private String dedupMatchUrl;

  public boolean match(PersonContainer personContainer) throws InterruptedException {
    try {
      DibbsRequestBodyDto  dibbsRequestBodyDto= new DibbsRequestBodyDto(personContainer);
      return callDIBBSConverterApi(dibbsRequestBodyDto);
    } catch (InterruptedException e) {
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
  }


  boolean callDIBBSConverterApi(DibbsRequestBodyDto dibbsRequestBodyDto) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
    HttpRequest request = createHttpRequest(dibbsRequestBodyDto);
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return handleResponse(response);
  }

  HttpRequest createHttpRequest(DibbsRequestBodyDto dibbsRequestBodyDto) throws IOException {
    String requestJson = mapper.writeValueAsString(dibbsRequestBodyDto);
    return HttpRequest.newBuilder()
        .uri(URI.create(dedupMatchUrl))
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
    edxPatientMatchFoundDT = new EdxPatientMatchDto();
    JsonNode personReferenceIdNode = jsonNode.get("person_reference_id");
    if (personReferenceIdNode != null && !personReferenceIdNode.asText().isEmpty()) {
      String personId = personReferenceIdNode.asText();
      if (!personId.matches("^[0-9]*$")) { // If it contains non-numeric characters
        personId = "10116362"; // Fixed value just for demo purposes
      }
      edxPatientMatchFoundDT.setPatientUid(Long.valueOf(personId));
    }
  }

}
