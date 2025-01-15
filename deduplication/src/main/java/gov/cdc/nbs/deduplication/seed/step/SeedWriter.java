package gov.cdc.nbs.deduplication.seed.step;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SeedWriter implements ItemWriter<NbsPerson> {

  private final ObjectMapper mapper;
  private final RestClient recordLinkageClient;

  public SeedWriter(
      final ObjectMapper mapper,
      @Qualifier("recordLinkageRestClient") final RestClient recordLinkageClient) {
    this.mapper = mapper;
    this.recordLinkageClient = recordLinkageClient;
  }

  @Override
  public void write(Chunk<? extends NbsPerson> chunk) throws Exception {
    List<SeedRequest.Cluster> clusters = mapToClusters(new ArrayList<>(chunk.getItems()));
    SeedRequest request = new SeedRequest(clusters);
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

  private List<SeedRequest.Cluster> mapToClusters(List<NbsPerson> nbsPersons) {
    // Group nbsPersons by parentId and create Cluster objects
    Map<String, List<SeedRequest.MpiPerson>> groupedByParentId = nbsPersons.stream()
        .collect(Collectors.groupingBy(
            NbsPerson::personParentId,
            Collectors.mapping(nbsPerson -> new SeedRequest.MpiPerson(
                nbsPerson.personId(),
                nbsPerson.birth_date(),
                nbsPerson.sex(),
                nbsPerson.mrn(),
                nbsPerson.address(),
                nbsPerson.name(),
                nbsPerson.telecom(),
                nbsPerson.ssn(),
                nbsPerson.race(),
                nbsPerson.gender(),
                nbsPerson.drivers_license()
            ), Collectors.toList())
        ));
    return groupedByParentId.entrySet().stream()
        .map(entry -> new SeedRequest.Cluster(entry.getValue(), entry.getKey()))
        .toList();
  }
}
