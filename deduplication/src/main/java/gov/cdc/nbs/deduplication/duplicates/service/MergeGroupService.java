package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidateData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MergeGroupService {

    private final NamedParameterJdbcTemplate deduplicationTemplate;


    public MergeGroupService(
            @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate
    ) {
        this.deduplicationTemplate = deduplicationTemplate;
    }


    @Transactional
    public List<MatchCandidateData> fetchAllMatchesRequiringReview() {
        return deduplicationTemplate.query(
                QueryConstants.FETCH_ALL_MATCH_CANDIDATES_REQUIRING_REVIEW,
                new MapSqlParameterSource(), // no params needed
                (rs, rowNum) -> new MatchCandidateData(
                        rs.getString("person_uid"),
                        rs.getLong("num_of_matching"),
                        rs.getString("date_identified")
                )
        );
    }
}
