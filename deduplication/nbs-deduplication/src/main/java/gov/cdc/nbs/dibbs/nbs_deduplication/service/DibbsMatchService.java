package gov.cdc.nbs.dibbs.nbs_deduplication.service;

import gov.cdc.nbs.dibbs.nbs_deduplication.model.MatchPersonRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


@Component
@Slf4j
@RequiredArgsConstructor
public class DibbsMatchService {

  @Value("${linkageServiceUrl}")
  String linkageServiceUrl;

  private final FhirConverter fhirConverter;

  public ResponseEntity<String> match(MatchPersonRequest requestBodyDto)
      throws InterruptedException {
    try {
      return callRecordLinkageApi(requestBodyDto);
    } catch (InterruptedException e) {
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  ResponseEntity<String> callRecordLinkageApi(MatchPersonRequest requestBodyDto)
      throws IOException, InterruptedException {
    String requestJson = fhirConverter.convertPersonToFhirFormat(requestBodyDto);
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
    HttpRequest request = createHttpRequest(requestJson);
    HttpResponse<String> response = sendRequest(client, request);
    return handleResponse(response);
  }

  public HttpResponse<String> sendRequest(HttpClient client, HttpRequest request)
      throws IOException, InterruptedException {
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  HttpRequest createHttpRequest(String requestJson) {
    return HttpRequest.newBuilder()
        .uri(URI.create(linkageServiceUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
        .build();
  }

  ResponseEntity<String> handleResponse(HttpResponse<String> response)  {
    if (response.statusCode() == 200) {
      return ResponseEntity.ok(response.body());
    }
    return ResponseEntity.status(response.statusCode()).build();
  }



}
