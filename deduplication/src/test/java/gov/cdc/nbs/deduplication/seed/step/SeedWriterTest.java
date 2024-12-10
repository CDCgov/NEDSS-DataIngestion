package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Cluster;

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
    final SeedWriter writer = new SeedWriter(mapper, restClient);

    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/seed")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);

    List<Cluster> clusters = new ArrayList<>();
    clusters.add(new Cluster(new ArrayList<>(), "1234"));
    var chunk = new Chunk<Cluster>(clusters);

    writer.write(chunk);

    verify(restClient, times(1)).post();
  }

}
