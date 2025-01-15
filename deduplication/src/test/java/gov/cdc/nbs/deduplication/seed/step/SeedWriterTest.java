package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
class SeedWriterTest {

  @Mock
  private RestClient restClient;

  @Mock
  private RequestBodyUriSpec uriSpec;

  @Mock
  private RequestBodySpec bodySpec;

  @Mock
  private ResponseSpec response;

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void initializes() {
    SeedWriter newWriter = new SeedWriter(mapper, restClient);
    assertThat(newWriter).isNotNull();
  }

  @Test
  void writesChunk() throws Exception {
    // Create a mock NbsPerson object
    NbsPerson nbsPerson1 = new NbsPerson(
        "1",
        "2",
        "1990-01-01",
        "M",
        "123456",
        List.of(new SeedRequest.Address(List.of("123 Main St."), "Atlanta", "Georgia", "30024", "Gwinnett County")),
        List.of(new SeedRequest.Name(List.of("John"), "Doe", List.of())),
        List.of(new Telecom("555-1234")),
        "123-45-6789",
        "Asian",
        "Male",
        new DriversLicense("D1234567", "GA")
    );

    NbsPerson nbsPerson2 = new NbsPerson(
        "3",
        "4",
        "1992-01-01",
        "F",
        "654321",
        List.of(new Address(List.of("456 Elm St."), "Atlanta", "Georgia", "30024", "Gwinnett County")),
        List.of(new Name(List.of("Jane"), "Smith", List.of())),
        List.of(new Telecom("555-5678")),
        "987-65-4321",
        "Caucasian",
        "Female",
        new DriversLicense("D7654321", "GA")
    );

    // Create a chunk of NbsPerson
    var chunk = new Chunk<NbsPerson>(List.of(nbsPerson1, nbsPerson2));

    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/seed")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);

    final SeedWriter writer = new SeedWriter(mapper, restClient);

    writer.write(chunk);

    verify(restClient, times(1)).post();
  }


}
