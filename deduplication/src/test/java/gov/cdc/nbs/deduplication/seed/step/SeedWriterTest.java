package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClientException;


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
  JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);


  @Test
  void initializes() {
    SeedWriter newWriter = new SeedWriter(jdbcTemplate, mapper, restClient);
    assertThat(newWriter).isNotNull();
  }

  @Test
  void writesChunk() throws Exception {
    final SeedWriter writer = new SeedWriter(jdbcTemplate, mapper, restClient);

    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/seed")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);


    List<NbsPerson> nbsPersons = new ArrayList<>();
    nbsPersons.add(new NbsPerson("100", "1"));
    var chunk = new Chunk<NbsPerson>(nbsPersons);

    writer.write(chunk);

    verify(restClient, times(1)).post();
  }

  @Test
  void writesChunk_shouldThrowExceptionWhenSeedingFails() {
    SeedWriter writer = new SeedWriter(jdbcTemplate, mapper, restClient);
    Chunk<NbsPerson> chunk = new Chunk<>(List.of(new NbsPerson("100", "1")));

    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/seed")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenThrow(new RestClientException("MPI Service Unavailable"));

    Exception exception = assertThrows(RuntimeException.class, () -> writer.write(chunk));
    assertEquals("Failed to seed cluster to MPI", exception.getMessage());
  }

}
