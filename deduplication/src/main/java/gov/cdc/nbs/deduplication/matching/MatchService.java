package gov.cdc.nbs.deduplication.matching;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import gov.cdc.nbs.deduplication.matching.mapper.LinkRequestMapper;
import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.matching.model.LinkResponse;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;

@Service
public class MatchService {
  // Selects the most recent legal name
  static final String FIND_NBS_ADD_TIME_AND_NAME_QUERY = """
      SELECT
        TOP 1 CONCAT(pn.first_nm, ' ', pn.last_nm) AS name,
        p.add_time
      FROM
        person p
        LEFT JOIN person_name pn ON pn.person_uid = p.person_uid
      WHERE
        p.person_uid = :id
      ORDER BY
        CASE
          WHEN pn.nm_use_cd = 'L' THEN 1
          ELSE 2
        END,
        pn.as_of_date DESC
            """;

  static final String FIND_NBS_PERSON_QUERY = """
      SELECT TOP 1
        person_parent_uid
      FROM
        nbs_mpi_mapping
      WHERE
        mpi_person = :mpi_person;
      """;


  private final RestClient recordLinkageClient;
  private final NamedParameterJdbcTemplate template;
  private final NamedParameterJdbcTemplate nbsTemplate;
  private final LinkRequestMapper linkRequestMapper = new LinkRequestMapper();

  public MatchService(
      @Qualifier("recordLinkerRestClient") final RestClient recordLinkageClient,
      @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template,
      @Qualifier("nbsNamedTemplate") final NamedParameterJdbcTemplate nbsTemplate) {
    this.recordLinkageClient = recordLinkageClient;
    this.template = template;
    this.nbsTemplate = nbsTemplate;
  }

  public MatchResponse match(PersonMatchRequest request) {
    // Convert request into MpiPerson acceptable by Record Linkage service
    LinkRequest linkRequest = linkRequestMapper.map(request);

    // Send to RL
    LinkResponse linkResponse = sendMatchRequest(linkRequest);

    // Do not fail ELR ingestion if RL fails to return. Batch job can reprocess and flag
    if (linkResponse == null) {
      return new MatchResponse(null, MatchType.NONE);
    }

    // Handle response from RL and send response back to caller
    if ("certain".equals(linkResponse.match_grade())) {
      return handleExactMatch(linkResponse);
    } else if ("possible".equals(linkResponse.match_grade())) {
      return new MatchResponse(null, MatchType.POSSIBLE);
    } else {
      return new MatchResponse(null, MatchType.NONE);
    }

  }

  private MatchResponse handleExactMatch(LinkResponse linkResponse) {
    // throws error if not able to find result
    Long matchingPerson = findNbsPersonParentId(linkResponse.person_reference_id());
    return new MatchResponse(matchingPerson, MatchType.EXACT);
  }

  private LinkResponse sendMatchRequest(LinkRequest linkRequest) {
    return recordLinkageClient.post()
        .uri("/match")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(linkRequest)
        .retrieve()
        .body(LinkResponse.class);
  }

  private Long findNbsPersonParentId(String mpiPerson) {
    SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("mpi_person", mpiPerson);
    return template.queryForObject(FIND_NBS_PERSON_QUERY, parameters, Long.class);
  }


  PatientNameAndTime findNbsInfo(Long id) {
    return nbsTemplate.query(
        FIND_NBS_ADD_TIME_AND_NAME_QUERY,
        new MapSqlParameterSource()
            .addValue("id", id),
        (ResultSet rs, int rowNum) -> new PatientNameAndTime(
            rs.getString("name"),
            rs.getTimestamp("add_time").toLocalDateTime()))
        .getFirst();
  }

}