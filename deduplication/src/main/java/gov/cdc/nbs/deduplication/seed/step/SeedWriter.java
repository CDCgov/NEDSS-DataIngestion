package gov.cdc.nbs.deduplication.seed.step;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

  private static final String CLUSTER_QUERY = """
    SELECT
          p.person_uid AS external_id,
          p.person_parent_uid,
          CAST(p.birth_time AS DATE) AS birth_date,
          p.curr_sex_cd AS sex,
          p.additional_gender_cd AS gender,
          nested.address,
          nested.phone,
          nested.name,
          nested.drivers_license,
          nested.ssn,
          nested.race
      FROM NBS_ODSE.dbo.person p WITH (NOLOCK)
      LEFT JOIN deduplication.dbo.nbs_mpi_mapping m ON p.person_uid = m.person_uid
      OUTER APPLY (
          SELECT
              ISNULL((SELECT STRING_ESCAPE(pl.street_addr1, 'json') AS street FROM NBS_ODSE.dbo.Postal_locator pl WHERE pl.postal_locator_uid = elp.locator_uid FOR JSON PATH), '[]') AS address,
              ISNULL((SELECT TOP 1 eid.root_extension_txt FROM NBS_ODSE.dbo.entity_id eid WHERE eid.entity_uid = p.person_uid AND eid.type_cd = 'SS'), '') AS ssn,
              ISNULL((SELECT TOP 1 pr.race_category_cd FROM NBS_ODSE.dbo.Person_race pr WHERE pr.person_uid = p.person_uid), '') AS race,
              ISNULL((SELECT REPLACE(REPLACE(tl.phone_nbr_txt,'-',''),' ','') FROM NBS_ODSE.dbo.Tele_locator tl JOIN NBS_ODSE.dbo.Entity_locator_participation elp ON elp.locator_uid = tl.tele_locator_uid WHERE elp.entity_uid = p.person_uid AND elp.class_cd = 'TELE' AND elp.status_cd = 'A' FOR JSON PATH), '[]') AS phone,
              ISNULL((SELECT STRING_ESCAPE(pn.first_nm, 'json') FROM NBS_ODSE.dbo.person_name pn WHERE pn.person_uid = p.person_uid FOR JSON PATH), '[]') AS name,
              ISNULL((SELECT ei.assigning_authority_cd FROM NBS_ODSE.dbo.entity_id ei WHERE ei.entity_uid = p.person_uid AND ei.type_cd = 'DL' FOR JSON PATH), '[]') AS drivers_license
      ) AS nested
      WHERE p.person_parent_uid IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      AND p.person_uid > ?
      AND (m.status != 'P' OR m.status IS NULL);
        """;

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final MpiPersonMapper mapper = new MpiPersonMapper();
  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;
  private final Long lastProcessedId;

  public SeedWriter(
          @Qualifier("nbsTemplate") JdbcTemplate template,
          ObjectMapper objectMapper,
          @Qualifier("recordLinkageRestClient") RestClient recordLinkageClient,
          @Value("${lastProcessedId:0}") Long lastProcessedId) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(template);
    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
    this.lastProcessedId = lastProcessedId;
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
            CLUSTER_QUERY,
            new MapSqlParameterSource()
                    .addValue("ids", personParentUids)
                    .addValue("lastProcessedId", lastProcessedId),
            mapper);

    Map<String, List<MpiPerson>> clusterDataMap = clusterEntries.stream()
            .collect(Collectors.groupingBy(MpiPerson::parent_id));

    return personParentUids.stream()
            .map(personParentUid -> new Cluster(
                    clusterDataMap.get(personParentUid),
                    personParentUid
            ))
            .toList();
  }
}