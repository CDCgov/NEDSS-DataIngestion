package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.NbsCaseAnswerQuery.SELECT_NBS_CASE_ANSWER_BY_ACT_UID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NbsCaseAnswerJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private NbsCaseAnswerJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNbsCaseAnswerByActUid_shouldReturnList() {
        NbsCaseAnswer answer = new NbsCaseAnswer();
        answer.setActUid(10L);

        when(jdbcTemplateOdse.query(eq(SELECT_NBS_CASE_ANSWER_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(answer));

        List<NbsCaseAnswer> result = repository.getNbsCaseAnswerByActUid(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getActUid());
    }

    @Test
    void testGetNbsCaseAnswerByActUid_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_NBS_CASE_ANSWER_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<NbsCaseAnswer> result = repository.getNbsCaseAnswerByActUid(999L);

        assertEquals(0, result.size());
    }
}