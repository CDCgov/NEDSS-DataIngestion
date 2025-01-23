package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
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

  @Mock
  private JdbcTemplate jdbcTemplate;  // Mock JdbcTemplate

  @Mock
  private JdbcTemplate otherJdbcTemplate;  // Mock the second JdbcTemplate

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void initializes() {
    // Pass both JdbcTemplate mocks
    SeedWriter newWriter = new SeedWriter(jdbcTemplate, otherJdbcTemplate, mapper, restClient);
    assertThat(newWriter).isNotNull();
  }

  @Test
  void writesChunk() throws Exception {
    // Pass both JdbcTemplate mocks
    final SeedWriter writer = new SeedWriter(jdbcTemplate, otherJdbcTemplate, mapper, restClient);

    // Mock RestClient interactions
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/seed")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);

    // Mock JdbcTemplate query to simulate database interaction
    List<NbsPerson> nbsPersons = new ArrayList<>();
    nbsPersons.add(new NbsPerson("100", "1"));
    var chunk = new Chunk<>(nbsPersons);

    // Call the write method
    writer.write(chunk);

    // Verify RestClient interactions
    verify(restClient, times(1)).post();
    verify(uriSpec, times(1)).uri("/seed");
    verify(bodySpec, times(1)).accept(MediaType.APPLICATION_JSON);
    verify(bodySpec, times(1)).contentType(MediaType.APPLICATION_JSON);
    verify(bodySpec, times(1)).body(Mockito.anyString());
    verify(bodySpec, times(1)).retrieve();
  }
}
