package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidateData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MergeGroupServiceTest {

    @Mock
    private NamedParameterJdbcTemplate deduplicationTemplate;

    @InjectMocks
    private MergeGroupService mergeGroupService;

    @Test
    @SuppressWarnings("unchecked")
    void fetchAllMatchesRequiringReview_ReturnsListOfMatchCandidateData() throws SQLException {
        // Given
        String personUid = "person-123";
        long numOfMatching = 3L;
        String dateIdentified = "2023-09-15";

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.getString("person_uid")).thenReturn(personUid);
        when(mockResultSet.getLong("num_of_matching")).thenReturn(numOfMatching);
        when(mockResultSet.getString("date_identified")).thenReturn(dateIdentified);

        when(deduplicationTemplate.query(
                eq(QueryConstants.FETCH_ALL_MATCH_CANDIDATES_REQUIRING_REVIEW),
                any(MapSqlParameterSource.class),
                any(RowMapper.class)
        )).thenAnswer(invocation -> {
            RowMapper<MatchCandidateData> rowMapper = invocation.getArgument(2);
            MatchCandidateData data = rowMapper.mapRow(mockResultSet, 0);
            return Collections.singletonList(data);
        });

        // When
        List<MatchCandidateData> result = mergeGroupService.fetchAllMatchesRequiringReview();

        // Then
        assertThat(result).hasSize(1);
        MatchCandidateData data = result.get(0);
        assertThat(data.personUid()).isEqualTo(personUid);
        assertThat(data.numOfMatches()).isEqualTo(numOfMatching);
        assertThat(data.dateIdentified()).isEqualTo(dateIdentified);
    }
}
