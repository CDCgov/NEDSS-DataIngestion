package gov.cdc.nbs.mpidatasyncer.service.linker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.mpidatasyncer.model.LinkerSeedRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
@RequiredArgsConstructor
public class LinkerApiService {

  @Value("${linker.seed.url}")
  String linkerSeedUrl;

  ObjectMapper objectMapper = new ObjectMapper();

  public ResponseEntity<String> seed(LinkerSeedRequest linkerSeedRequest)
      throws InterruptedException {
    try {
      return callLinkerSeedApi(linkerSeedRequest);
    } catch (InterruptedException e) {
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage());
     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  ResponseEntity<String> callLinkerSeedApi(LinkerSeedRequest linkerSeedRequest)
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
    HttpRequest request = createHttpRequest(linkerSeedRequest);
    HttpResponse<String> response= sendRequest(client,request);
    return handleResponse(response);
  }

  public HttpResponse<String> sendRequest(HttpClient client,HttpRequest request)
      throws IOException, InterruptedException {
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

   HttpRequest createHttpRequest(LinkerSeedRequest linkerSeedRequest) throws JsonProcessingException {
     String jsonRequest = objectMapper.writeValueAsString(linkerSeedRequest);
    return HttpRequest.newBuilder()
        .uri(URI.create(linkerSeedUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
        .build();
  }

   ResponseEntity<String> handleResponse(HttpResponse<String> response)   {
    if (response.statusCode() == 200) {
      return ResponseEntity.ok(response.body());
    }
    return ResponseEntity.status(response.statusCode()).build();
  }
}
