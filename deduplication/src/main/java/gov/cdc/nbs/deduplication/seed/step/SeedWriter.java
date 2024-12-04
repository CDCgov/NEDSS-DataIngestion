package gov.cdc.nbs.deduplication.seed.step;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Cluster;

/**
 * Submits Seed request to Record Linkage API
 */
@Component
public class SeedWriter implements ItemWriter<Cluster> {

  private final ObjectMapper mapper;
  private final RestClient recordLinkageClient;

  public SeedWriter(
      final ObjectMapper mapper,
      @Qualifier("recordLinkageRestClient") final RestClient recordLinkageClient) {
    this.mapper = mapper;
    this.recordLinkageClient = recordLinkageClient;
  }

  @Override
  public void write(@NonNull Chunk<? extends Cluster> chunk) throws Exception {
    SeedRequest request = new SeedRequest(List.copyOf(chunk.getItems()));
    String requestJson = mapper.writeValueAsString(request);

    // Send Clusters to MPI
    recordLinkageClient.post()
        .uri("/seed")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(requestJson)
        .retrieve()
        .body(MpiResponse.class);

  }

}
