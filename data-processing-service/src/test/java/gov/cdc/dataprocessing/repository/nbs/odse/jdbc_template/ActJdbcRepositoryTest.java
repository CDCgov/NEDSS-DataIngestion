package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static gov.cdc.dataprocessing.constant.query.ActQuery.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ActJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private ActJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertAct_shouldCallUpdateWithCorrectSQLAndParameters() {
        Act act = new Act();
        act.setActUid(1L);
        act.setClassCode("classCd");
        act.setMoodCode("moodCd");

        repository.insertAct(act);

        verify(jdbcTemplateOdse, times(1)).update(eq(INSERT_SQL_ACT), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateAct_shouldCallUpdateWithCorrectSQLAndParameters() {
        Act act = new Act();
        act.setActUid(2L);
        act.setClassCode("classCd2");
        act.setMoodCode("moodCd2");

        repository.updateAct(act);

        verify(jdbcTemplateOdse, times(1)).update(eq(UPDATE_SQL_ACT), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeAct_shouldCallUpdateWithCorrectSQLAndParameters() {
        Act act = new Act();
        act.setActUid(3L);
        act.setClassCode("classCd3");
        act.setMoodCode("moodCd3");

        repository.mergeAct(act);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_SQL_ACT), any(MapSqlParameterSource.class));
    }
}
