package gov.cdc.nbs.deduplication.seed.step;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
  // Set to keep track of processed person_parent_uids to avoid reseeding
  private final Set<String> processedPersonParentUids = new HashSet<>();

  public SeedWriter(
      final ObjectMapper mapper,
      @Qualifier("recordLinkageRestClient") final RestClient recordLinkageClient) {
    this.mapper = mapper;
    this.recordLinkageClient = recordLinkageClient;
  }

  @Override
  public void write(@NonNull Chunk<? extends Cluster> chunk) throws Exception {
    System.out.println("Processing chunk of size: " + chunk.getItems().size());

    // Filter out clusters that have already been processed
    @SuppressWarnings("unchecked")
    List<Cluster> clustersToSend = (List<Cluster>) chunk.getItems().stream()
            .filter(cluster -> !processedPersonParentUids.contains(cluster.external_person_id()))
            .toList();

    // Mark processed clusters
    clustersToSend.forEach(cluster -> processedPersonParentUids.add(cluster.external_person_id()));


    if (!clustersToSend.isEmpty()) {
      // Add the processed person_parent_uids to the set to avoid reseeding in the future
      clustersToSend.forEach(cluster -> processedPersonParentUids.add(cluster.external_person_id()));

      // Create the SeedRequest
      SeedRequest request = new SeedRequest(clustersToSend);
      String requestJson = mapper.writeValueAsString(request);

      // Send the clusters to the MPI
      recordLinkageClient.post()
              .uri("/seed")
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .body(requestJson)
              .retrieve()
              .body(MpiResponse.class);

      System.out.println("Clusters successfully sent to MPI: " + clustersToSend.size());
    } else {
      System.out.println("No new clusters to send to MPI in this chunk.");
    }
  }
}
