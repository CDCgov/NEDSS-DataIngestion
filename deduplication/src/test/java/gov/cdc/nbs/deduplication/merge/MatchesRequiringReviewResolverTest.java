package gov.cdc.nbs.deduplication.merge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;
import gov.cdc.nbs.deduplication.merge.exception.MergeListException;

@ExtendWith(MockitoExtension.class)
class MatchesRequiringReviewResolverTest {

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @InjectMocks
  MatchesRequiringReviewResolver resolver;

  @Test
  void should_fetch() {
    // Mock
    // count
    JdbcTemplate template = Mockito.mock(JdbcTemplate.class);
    when(deduplicationTemplate.getJdbcTemplate()).thenReturn(template);
    when(template.queryForObject(MatchesRequiringReviewResolver.COUNT_QUERY, Integer.class)).thenReturn(14);
    // fetch
    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
    when(deduplicationTemplate.query(
        eq(MatchesRequiringReviewResolver.SELECT_QUERY.replace(":sort", "person_name ASC")),
        captor.capture(),
        ArgumentMatchers.<RowMapper<MatchRequiringReview>>any())).thenReturn(List.of(
            new MatchRequiringReview(
                "12345",
                "444",
                "John Doe",
                "create date as string",
                "identified date as string",
                2)));

    // Call resolve
    MatchesRequireReviewResponse response = resolver.resolve(1, 13, "name,asc");

    // Validate
    MapSqlParameterSource params = captor.getValue();
    assertThat(params.getValue("limit")).isEqualTo(13);
    assertThat(params.getValue("offset")).isEqualTo(13);
    assertThat(response.matches()).hasSize(1);
    assertThat(response.matches().get(0).patientId()).isEqualTo("12345");
    assertThat(response.matches().get(0).patientLocalId()).isEqualTo("444");
    assertThat(response.matches().get(0).patientName()).isEqualTo("John Doe");
    assertThat(response.matches().get(0).createdDate()).isEqualTo("create date as string");
    assertThat(response.matches().get(0).identifiedDate()).isEqualTo("identified date as string");
    assertThat(response.matches().get(0).numOfMatchingRecords()).isEqualTo(2);
    assertThat(response.page()).isEqualTo(1);
    assertThat(response.total()).isEqualTo(14);
  }

  @Test
  void should_fetch_all() {
    // Mock fetch
    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
    when(deduplicationTemplate.query(
        eq(MatchesRequiringReviewResolver.SELECT_QUERY.replace(":sort", "person_name ASC")),
        captor.capture(),
        ArgumentMatchers.<RowMapper<MatchRequiringReview>>any())).thenReturn(List.of(
            new MatchRequiringReview(
                "12345",
                "444",
                "John Doe",
                "create date as string",
                "identified date as string",
                2)));

    // Call resolve
    resolver.resolveAll("name,asc");

    // Validate
    MapSqlParameterSource params = captor.getValue();
    assertThat(params.getValue("limit")).isEqualTo(10_000);
    assertThat(params.getValue("offset")).isEqualTo(0);
  }

  @Test
  void should_return_count() {
    // Mock
    JdbcTemplate template = Mockito.mock(JdbcTemplate.class);
    when(deduplicationTemplate.getJdbcTemplate()).thenReturn(template);
    when(template.queryForObject(MatchesRequiringReviewResolver.COUNT_QUERY, Integer.class)).thenReturn(13);

    // Call count
    Integer totalCount = resolver.getMatchCandidateCount();

    // Validate
    assertThat(totalCount).isEqualTo(13);
  }

  @Test
  void should_map_row() throws SQLException {
    // Mock resultset
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    java.sql.Timestamp addTime = java.sql.Timestamp.from(Instant.now().minus(Duration.ofHours(3)));
    java.sql.Timestamp dateIdentified = java.sql.Timestamp.from(Instant.now());

    when(resultSet.getString("person_uid")).thenReturn("12345");
    when(resultSet.getString("person_local_id")).thenReturn("444");
    when(resultSet.getString("person_name")).thenReturn("John Doe");
    when(resultSet.getTimestamp("person_add_time")).thenReturn(addTime);
    when(resultSet.getTimestamp("date_identified")).thenReturn(dateIdentified);
    when(resultSet.getInt("match_count")).thenReturn(3);

    // Map to object
    MatchRequiringReview mappedRow = resolver.mapRowToMatchCandidateData(resultSet, 0);

    // validate mapping
    assertThat(mappedRow.patientId()).isEqualTo("12345");
    assertThat(mappedRow.patientLocalId()).isEqualTo("444");
    assertThat(mappedRow.patientName()).isEqualTo("John Doe");
    assertThat(mappedRow.createdDate()).isEqualTo(addTime.toLocalDateTime().toString());
    assertThat(mappedRow.identifiedDate()).isEqualTo(dateIdentified.toLocalDateTime().toString());
    assertThat(mappedRow.numOfMatchingRecords()).isEqualTo(3);
  }

  @ParameterizedTest
  @CsvSource(delimiter = '|', textBlock = """
      patient-id,asc | person_local_id | ASC
      patient-id,desc | person_local_id | DESC
      name,asc | person_name | ASC
      name,desc | person_name | DESC
      created,asc | person_add_time | ASC
      created,desc | person_add_time | DESC
      identified,asc | date_identified | ASC
      identified,desc | date_identified | DESC
      count,asc | match_count | ASC
      count,desc | match_count | DESC
      """)
  void should_resolve_order(String input, String expectedColumn, String expectedDirection) {
    Sort.Order order = resolver.toOrder(input);
    assertThat(order.getProperty()).isEqualTo(expectedColumn);
    assertThat(order.getDirection()).isEqualTo(Direction.fromString(expectedDirection));
  }

  @Test
  void should_throw_error_invalid_sort() {
    MergeListException ex = assertThrows(
        MergeListException.class,
        () -> resolver.toOrder("badSort"));
    assertThat(
        ex.getMessage())
        .isEqualTo("Invalid sort column specified. Valid options are [patient-id, name, created, identified, count]");
  }

}
