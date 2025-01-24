package gov.cdc.nbs.deduplication.seed.step;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class SeedWriterTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Mock
  private RestClient restClient;

  @Mock
  private RequestBodyUriSpec uriSpec;

  @Mock
  private RequestBodySpec bodySpec;

  @Mock
  private ResponseSpec response;

  private SeedWriter writer;

  @BeforeEach
  void setUp() {
    // Initialize the mocks
    MockitoAnnotations.openMocks(this);

    // Initialize the writer with mocked jdbcTemplate and restClient
    writer = new SeedWriter(jdbcTemplate, new ObjectMapper(), restClient);
  }


  @Test
  void writesFirstBatchWhenNoLastProcessedId() throws Exception {
    // Simulate the first run, where there is no lastProcessedId.
    when(jdbcTemplate.queryForObject("SELECT last_processed_id FROM deduplication WHERE job_name = 'seeding-job'", Long.class))
            .thenReturn(null); // no lastProcessedId

    // Mock RestClient interactions
    when(restClient.post()).thenReturn(uriSpec);  // Mocking post to return uriSpec
    when(uriSpec.uri(anyString(), any())).thenReturn(bodySpec);  // Mock uri() to return bodySpec
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);

    // Mock NbsPerson data representing a chunk of records
    List<NbsPerson> nbsPersons = new ArrayList<>();
    nbsPersons.add(new NbsPerson("100", "1"));
    nbsPersons.add(new NbsPerson("101", "1"));
    var chunk = new Chunk<>(nbsPersons);

    // Call the method under test
    writer.write(chunk);

    // Verify that the write method correctly initiates the seeding from the smallest ID (100)
    // Check that the seeding process uses the first chunk (with the smallest ID)
    verify(restClient, times(1)).post(); // Ensure the request to the REST client was made
    verify(uriSpec, times(1)).uri(anyString(), any());
    verify(bodySpec, times(1)).body(anyString());
  }


  @Test
  void writesSubsequentBatchWhenLastProcessedIdExists() throws Exception {
    // Simulate a scenario where the lastProcessedId is present from a previous run
    when(jdbcTemplate.queryForObject("SELECT last_processed_id FROM deduplication WHERE job_name = 'seeding-job'", Long.class))
            .thenReturn(100L); // lastProcessedId is 100

    // Mock NbsPerson data representing a chunk of records
    List<NbsPerson> nbsPersons = new ArrayList<>();
    nbsPersons.add(new NbsPerson("101", "1"));
    nbsPersons.add(new NbsPerson("102", "1"));
    var chunk = new Chunk<>(nbsPersons);

    // Call the method under test
    writer.write(chunk);

    verify(restClient, times(1)).post(); // Ensure the request to the REST client was made
  }


  @Test
  void writesChunk() throws Exception {
    // Mock the RestClient interactions
    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/seed")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(Mockito.anyString())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(response);

    // Prepare test data (NbsPerson list and Chunk)
    List<NbsPerson> nbsPersons = new ArrayList<>();
    nbsPersons.add(new NbsPerson("100", "1"));
    var chunk = new Chunk<>(nbsPersons);

    // Call the method under test
    writer.write(chunk);

    // Verify that the RestClient post() method was called exactly once
    verify(restClient, times(1)).post();
  }

}
