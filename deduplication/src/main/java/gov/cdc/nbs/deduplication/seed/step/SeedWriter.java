package gov.cdc.nbs.deduplication.seed.step;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.*;


/**
 * Submits Seed request to Record Linkage API
 */

@Component
public class SeedWriter implements ItemWriter<NbsPerson> {


  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final MpiPersonMapper mapper = new MpiPersonMapper();
  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;

  public SeedWriter(
      @Qualifier("nbsTemplate") JdbcTemplate template,
      ObjectMapper objectMapper,
      @Qualifier("recordLinkageRestClient") RestClient recordLinkageClient) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(template);
    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
  }

  @Override
  public void write(Chunk<? extends NbsPerson> chunk) throws Exception {
    // Extract person_parent_uids from the chunk
    List<String> personParentUids = chunk.getItems().stream()
        .map(NbsPerson::personParentUid)
        .toList();

    List<Cluster> clusters = fetchClusters(personParentUids);

    // Send Clusters to MPI
    SeedRequest request = new SeedRequest(clusters);
    String requestJson = objectMapper.writeValueAsString(request);

    recordLinkageClient.post()
        .uri("/seed")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(requestJson)
        .retrieve()
        .body(MpiResponse.class);
  }

  private List<Cluster> fetchClusters(List<String> personParentUids) {
    // fetch all cluster data for the current batch of person_parent_uids
    List<MpiPerson> clusterEntries = namedParameterJdbcTemplate.query(
        QueryConstants.PERSON_RECORDS_BY_PARENT_IDS,
        new MapSqlParameterSource("ids", personParentUids),
        mapper);

    Map<String, List<MpiPerson>> clusterDataMap = clusterEntries.stream()
        .collect(Collectors.groupingBy(MpiPerson::parent_id));

    return personParentUids.stream()
        .map(personParentUid -> new Cluster(
            clusterDataMap.get(personParentUid),
            personParentUid))
        .toList();
  }
}
