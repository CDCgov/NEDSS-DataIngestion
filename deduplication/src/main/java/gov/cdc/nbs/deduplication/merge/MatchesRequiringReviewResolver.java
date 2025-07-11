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
        mrr.id,
        mrr.person_uid,
        mrr.person_local_id,
        mrr.person_name,
        mrr.person_add_time,
        mrr.date_identified,
        count(mc.person_uid) as match_count
      FROM
        matches_requiring_review mrr
        JOIN match_candidates mc ON mc.match_id = mrr.id AND mc.is_merge IS NULL
      GROUP BY
        mrr.id,
        mrr.person_uid,
        mrr.person_local_id,
        mrr.person_name,
        mrr.person_add_time,
        mrr.date_identified
            ORDER BY :sort
            OFFSET :offset ROWS
                FETCH NEXT :limit ROWS ONLY;
            """;

  static final String COUNT_QUERY = """
      SELECT
        count(*) AS COUNT
      FROM
        (
          SELECT DISTINCT
            mrr.person_uid,
            date_identified,
            person_name,
            person_add_time
          FROM
            matches_requiring_review mrr
            JOIN match_candidates mc ON mc.match_id = mrr.id AND mc.is_merge IS NULL
          WHERE
            is_merge IS NULL
        ) AS COUNT;
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

  Integer getMatchCandidateCount() {
    return deduplicationTemplate.getJdbcTemplate()
        .queryForObject(COUNT_QUERY, Integer.class);
  }

  private List<MatchRequiringReview> fetch(int offset, int limit, Sort.Order order) {
    String sortValue = order.getProperty() + " " + order.getDirection().toString();
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", offset);

    return deduplicationTemplate.query(
        SELECT_QUERY.replace(":sort", sortValue),
        parameters,
        this::mapRowToMatchCandidateData);
  }

  MatchRequiringReview mapRowToMatchCandidateData(ResultSet rs, int rowNum) throws SQLException {
    return new MatchRequiringReview(
        rs.getLong("id"),
        rs.getString("person_uid"),
        rs.getString("person_local_id"),
        rs.getString("person_name"),
        rs.getTimestamp("person_add_time").toLocalDateTime().toString(),
        rs.getTimestamp("date_identified").toLocalDateTime().toString(),
        rs.getInt("match_count"));
  }

  Sort.Order toOrder(String sort) {
    String[] sortParams = sort.split(",");
    String column = switch (sortParams[0]) {
      case "patient-id" -> "person_local_id";
      case "name" -> "person_name";
      case "created" -> "person_add_time";
      case "identified" -> "date_identified";
      case "count" -> "match_count";
      default -> throw new MergeListException(
          "Invalid sort column specified. Valid options are [patient-id, name, created, identified, count]");
    };
    Sort.Direction direction = "asc".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.Order.by(column).with(direction);
  }

}
