package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxEntityMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static gov.cdc.dataprocessing.constant.query.EdxMatchQuery.MERGE_EDX_ENTITY_MATCH;
import static gov.cdc.dataprocessing.constant.query.EdxMatchQuery.MERGE_EDX_PATIENT_MATCH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EdxMatchJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private EdxMatchJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMergeEdxPatientMatch_shouldCallUpdate() {
        EdxPatientMatch match = new EdxPatientMatch();
        match.setPatientUid(101L);
        match.setMatchString("matchStr");
        match.setTypeCd("type1");
        match.setMatchStringHashcode(1L);

        repository.mergeEdxPatientMatch(match);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_EDX_PATIENT_MATCH), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeEdxEntityMatch_shouldCallUpdate() {
        EdxEntityMatch match = new EdxEntityMatch();
        match.setEntityUid(202L);
        match.setMatchString("entityMatch");
        match.setTypeCd("type2");
        match.setMatchStringHashcode(1L);

        repository.mergeEdxEntityMatch(match);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_EDX_ENTITY_MATCH), any(MapSqlParameterSource.class));
    }
}