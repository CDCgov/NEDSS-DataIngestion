package gov.cdc.nbs.deduplication.merge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;
import gov.cdc.nbs.deduplication.merge.exception.MergeListException;

@Component
public class MatchesRequiringReviewResolver {
  private final NamedParameterJdbcTemplate deduplicationTemplate;

  public MatchesRequiringReviewResolver(
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate) {
    this.deduplicationTemplate = deduplicationTemplate;
  }

  static final String SELECT_QUERY = """
      SELECT
          person_uid,
          person_name,
          person_add_time,
          COUNT(mpi_person_id) + 1 AS match_count,
          date_identified
      FROM
          match_candidates
      WHERE is_merge IS NULL
      GROUP BY
          person_uid,
          date_identified
      OFFSET :offset ROWS
      FETCH NEXT :limit ROWS ONLY
      ORDER BY :sortColumn :sortDirection;
      """;

  static final String COUNT_QUERY = """
      SELECT
        count(DISTINCT person_uid)
      FROM
        match_candidates
      WHERE
        is_merge IS NULL
      """;

  MatchesRequireReviewResponse resolve(int page, int size, String sort) {
    int offset = page * size;
    Integer total = getMatchCandidateCount();

    Sort.Order sortOrder = toOrder(sort);

    List<MatchRequiringReview> data = fetch(offset, size, sortOrder);
    return new MatchesRequireReviewResponse(data, page, total);
  }

  List<MatchRequiringReview> resolveAll(String sort) {
    return fetch(0, 10_000, toOrder(sort));
  }

  private Integer getMatchCandidateCount() {
    return deduplicationTemplate.getJdbcTemplate()
        .queryForObject(COUNT_QUERY, Integer.class);
  }

  private List<MatchRequiringReview> fetch(int offset, int limit, Sort.Order order) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", offset)
        .addValue("sortColumn", order.getProperty())
        .addValue("sortDirection", order.getDirection()); // Might not work due to enum to string
    return deduplicationTemplate.query(
        SELECT_QUERY,
        parameters,
        this::mapRowToMatchCandidateData);
  }

  private MatchRequiringReview mapRowToMatchCandidateData(ResultSet rs, int rowNum) throws SQLException {
    return new MatchRequiringReview(
        rs.getString("person_uid"),
        rs.getString("patient_name"),
        rs.getTimestamp("add_time").toLocalDateTime().toString(),
        rs.getTimestamp("identified_time").toLocalDateTime().toString(),
        rs.getInt("match_count"));
  }

  private Sort.Order toOrder(String sort) {
    String[] sortParams = sort.split(",");
    String column = switch (sortParams[0]) {
      case "patient-id" -> "person_uid";
      case "name" -> "person_name";
      case "created" -> "add_time";
      case "identified" -> "date_identified";
      case "count" -> "match_count";
      default -> throw new MergeListException(
          "Invalid sort column specified. Valid options are [patient-id, name, created, identified, count]");
    };
    Sort.Direction direction = "asc".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.Order.by(column).with(direction);
  }

}
