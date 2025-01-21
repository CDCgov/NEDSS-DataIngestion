package gov.cdc.nbs.deduplication.seed.step;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.cdc.nbs.deduplication.seed.logger.LoggingService;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.seed.model.MpiResponse;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.*;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Cluster;

/**
 * Submits Seed request to Record Linkage API
 */

@Component
public class SeedWriter implements ItemWriter<NbsPerson> {

  private static final String CLUSTER_QUERY = """
      SELECT
          p.person_uid external_id,
          p.person_parent_uid,
          cast(p.birth_time as Date) birth_date,
          p.curr_sex_cd sex,
          p.additional_gender_cd gender,
          nested.address,
          nested.phone,
          nested.name,
          nested.drivers_license,
          nested.ssn,
          nested.race
      FROM
          person p WITH (NOLOCK)
          OUTER apply (
              SELECT
                  *
              FROM
                  -- address
                  (
                      SELECT
                          (
                              SELECT
                                  STRING_ESCAPE(pl.street_addr1, 'json') street,
                                  STRING_ESCAPE(pl.street_addr2, 'json') street2,
                                  city_desc_txt city,
                                  sc.code_desc_txt state,
                                  zip_cd zip,
                                  scc.code_desc_txt county
                              FROM
                                  Entity_locator_participation elp WITH (NOLOCK)
                                  JOIN Postal_locator pl WITH (NOLOCK) ON elp.locator_uid = pl.postal_locator_uid
                                  LEFT JOIN NBS_SRTE.dbo.state_code sc ON sc.state_cd = pl.state_cd
                                  LEFT JOIN NBS_SRTE.dbo.state_county_code_value scc ON scc.code = pl.cnty_cd
                              WHERE
                                  elp.entity_uid = p.person_uid
                                  AND elp.class_cd = 'PST'
                                  AND elp.status_cd = 'A'
                                  AND pl.street_addr1 IS NOT NULL FOR json path
                          ) AS address
                  ) AS address,
                  --ssn
                  (
                      SELECT
                          (
                              SELECT
                                  TOP 1 eid.root_extension_txt
                              FROM
                                  entity_id eid WITH (NOLOCK)
                              WHERE
                                  eid.entity_uid = p.person_uid
                              AND eid.type_cd = 'SS') as ssn) as ssn,
                  -- person races
                  (
                      SELECT
                          (
                              SELECT TOP 1
                                  pr.race_category_cd value
                              FROM
                                  Person_race pr WITH (NOLOCK)
                              WHERE
                                  person_uid = p.person_uid
                          ) AS race
                  ) AS race,
                  -- person phone
                  (
                      SELECT
                          (
                              SELECT
                                  REPLACE(REPLACE(tl.phone_nbr_txt,'-',''),' ','') value
                              FROM
                                  Entity_locator_participation elp WITH (NOLOCK)
                                  JOIN Tele_locator tl WITH (NOLOCK) ON elp.locator_uid = tl.tele_locator_uid
                              WHERE
                                  elp.entity_uid = p.person_uid
                                  AND elp.class_cd = 'TELE'
                                  AND elp.status_cd = 'A'
                                  AND tl.phone_nbr_txt IS NOT NULL FOR json path
                          ) AS phone
                  ) AS phone,
                  -- person_names
                  (
                      SELECT
                          (
                              SELECT
                                  STRING_ESCAPE(REPLACE(pn.last_nm,'-',' '), 'json') lastNm,
                                  STRING_ESCAPE(pn.middle_nm, 'json') middleNm,
                                  STRING_ESCAPE(pn.first_nm, 'json') firstNm,
                                  pn.nm_suffix nmSuffix
                              FROM
                                  person_name pn WITH (NOLOCK)
                              WHERE
                                  person_uid = p.person_uid FOR json path
                          ) AS name
                  ) AS name,
                  -- Drivers license
                  (
                      SELECT
                          (
                              SELECT
                                  ei.assigning_authority_cd authority,
                                  STRING_ESCAPE(REPLACE(REPLACE(ei.root_extension_txt,'-',''),' ',''), 'json') value
                              FROM
                                  entity_id ei WITH (NOLOCK)
                              WHERE
                                  ei.entity_uid = p.person_uid
                                  AND ei.type_cd = 'DL' FOR json path
                          ) AS drivers_license
                  ) AS drivers_license
              ) AS nested
      WHERE
          p.person_parent_uid IN (:ids);
      """;

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final MpiPersonMapper mapper = new MpiPersonMapper();
  private final ObjectMapper objectMapper;
  private final RestClient recordLinkageClient;
  private final LoggingService loggingService;

  public SeedWriter(
      @Qualifier("nbsTemplate") JdbcTemplate template,
      ObjectMapper objectMapper,
      @Qualifier("recordLinkageRestClient") RestClient recordLinkageClient,
      final LoggingService loggingService) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(template);
    this.objectMapper = objectMapper;
    this.recordLinkageClient = recordLinkageClient;
    this.loggingService = loggingService;
  }

  @Override
  public void write(@NonNull Chunk<? extends NbsPerson> chunk) throws Exception {
    try {
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
    } catch (Exception e) {
      loggingService.logError("SeedWriter", "Error during MPI persons batch seeding.", e);
      throw e;
    }
  }

  private List<Cluster> fetchClusters(List<String> personParentUids) {
    // fetch all cluster data for the current batch of person_parent_uids
    List<MpiPerson> clusterEntries = namedParameterJdbcTemplate.query(
        CLUSTER_QUERY,
        new MapSqlParameterSource("ids", personParentUids),
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
