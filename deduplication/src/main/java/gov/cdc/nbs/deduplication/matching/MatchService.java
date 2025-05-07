package gov.cdc.nbs.deduplication.matching;

import gov.cdc.nbs.deduplication.matching.model.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import gov.cdc.nbs.deduplication.matching.exception.MatchException;
import gov.cdc.nbs.deduplication.matching.mapper.LinkRequestMapper;
import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;

@Service
public class MatchService {
  private static final String FIND_NBS_PERSON_QUERY = """
      SELECT TOP 1
        person_parent_uid
      FROM
        nbs_mpi_mapping
      WHERE
        mpi_person = :mpi_person;
      """;

  private static final String LINK_NBS_MPI_QUERY = """
      INSERT INTO nbs_mpi_mapping
            (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
          VALUES
            (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
          """;

  private static final String INSERT_POSSIBLE_MATCH = """
      INSERT INTO match_candidates
        (person_uid, mpi_person_id)
      VALUES
        (:person_uid, :mpi_person_id)
      """;

  private final RestClient recordLinkageClient;
  private final NamedParameterJdbcTemplate template;
  private final LinkRequestMapper linkRequestMapper = new LinkRequestMapper();

  public MatchService(
      @Qualifier("recordLinkerRestClient") final RestClient recordLinkageClient,
      @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
    this.recordLinkageClient = recordLinkageClient;
    this.template = template;
  }

  public MatchResponse match(PersonMatchRequest request) {
    // Convert request into MpiPerson acceptable by Record Linkage service
    LinkRequest linkRequest = linkRequestMapper.map(request);

    // Send to RL
    LinkResponse linkResponse = sendMatchRequest(linkRequest);

    if (linkResponse == null) {
      throw new MatchException("Match response from Record Linkage is null");
    }

    // Handle response from RL and send response back to caller
    if ("certain".equals(linkResponse.match_grade())) {
      return handleExactMatch(linkResponse);
    } else if ("possible".equals(linkResponse.match_grade())) {
      return handlePossibleMatch(linkResponse);
    } else {
      return new MatchResponse(null, MatchType.NONE, linkResponse);
    }

  }

  private MatchResponse handleExactMatch(LinkResponse linkResponse) {
    // throws error if not able to find result
    Long matchingPerson = findNbsPersonParentId(linkResponse.person_reference_id());
    return new MatchResponse(matchingPerson, MatchType.EXACT, linkResponse);
  }

  private MatchResponse handlePossibleMatch(LinkResponse linkResponse) {
    // In the case of a possible match, a new `patient` record is created within the
    // MPI but not linked to any `person`. We tell the MPI to create a new person to
    // match what we will do in NBS
    CreatePersonResponse response = sendCreatePersonRequest(linkResponse.patient_reference_id());

    if (response == null) {
      throw new MatchException(
          "Record Linkage failed to create new entry for patient: " + linkResponse.patient_reference_id());
    }

    // Add newly created person identifier to response
    LinkResponse newLinkReponse = new LinkResponse(
        linkResponse.patient_reference_id(),
        response.person_reference_id(),
        linkResponse.match_grade(),
        linkResponse.results());
    return new MatchResponse(null, MatchType.POSSIBLE, newLinkReponse);
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

  public LinkResponse sendLinkRequest(LinkRequest linkRequest) {
    return recordLinkageClient.post()
        .uri("/link")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(linkRequest)
        .retrieve()
        .body(LinkResponse.class);
  }

  private CreatePersonResponse sendCreatePersonRequest(String mpiPatientId) {
    return recordLinkageClient.post()
        .uri("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(new CreatePersonRequest(List.of(mpiPatientId)))
        .retrieve()
        .body(CreatePersonResponse.class);
  }

  private Long findNbsPersonParentId(String mpiPerson) {
    SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("mpi_person", mpiPerson);
    return template.queryForObject(FIND_NBS_PERSON_QUERY, parameters, Long.class);
  }

  // Adds an entry to the deduplication database to relate the NBS person Ids to
  // the MPI person Ids
  public void relateNbsIdToMpiId(RelateRequest request) {
    try {
      // Step 1: Build LinkRequest from mpiPerson (original match input)
      LinkRequest linkRequest = new LinkRequest(request.mpiPerson());

      // Step 2: Call /link to insert into MPI
      LinkResponse linkResponse = sendLinkRequest(linkRequest);

      if (linkResponse == null) {
        throw new MatchException("Linking failed when inserting patient into MPI");
      }

      // Step 3: Insert into nbs_mpi_mapping
      boolean isPossibleMatch = request.matchType() == MatchType.POSSIBLE;
      String status = isPossibleMatch ? "R" : "P";

      SqlParameterSource parameters = new MapSqlParameterSource()
              .addValue("person_uid", request.nbsPerson())
              .addValue("person_parent_uid", request.nbsPersonParent())
              .addValue("mpi_patient", linkResponse.patient_reference_id())
              .addValue("mpi_person", linkResponse.person_reference_id())
              .addValue("status", status);
      template.update(LINK_NBS_MPI_QUERY, parameters);

      // Step 4: Store possible matches
      if (isPossibleMatch && linkResponse.results() != null) {
        linkResponse.results().forEach(result -> {
          SqlParameterSource matchParams = new MapSqlParameterSource()
                  .addValue("person_uid", request.nbsPerson())
                  .addValue("mpi_person_id", result.person_reference_id());
          template.update(INSERT_POSSIBLE_MATCH, matchParams);
        });
      }
    } catch (NullPointerException e) {
      throw new MatchException("Linking failed when inserting patient into MPI");
    }
  }

}