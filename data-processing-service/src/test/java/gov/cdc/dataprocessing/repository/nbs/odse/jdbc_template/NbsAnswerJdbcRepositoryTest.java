package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.NbsAnswerQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NbsAnswerJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private NbsAnswerJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMergeNbsAnswer_shouldCallUpdate() {
        NbsAnswer answer = new NbsAnswer();
        answer.setNbsAnswerUid(1L);
        answer.setActUid(2L);
        answer.setAnswerTxt("Yes");
        answer.setNbsQuestionUid(3L);
        answer.setNbsQuestionVersionCtrlNbr(1);
        answer.setSeqNbr(1);
        answer.setAnswerLargeTxt("LargeText");
        answer.setAnswerGroupSeqNbr(1);
        answer.setRecordStatusCd("A");
        answer.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        answer.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        answer.setLastChgUserId(10L);

        repository.mergeNbsAnswer(answer);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_NBS_ANSWER), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeNbsAnswerHist_shouldCallUpdate() {
        NbsAnswerHist hist = new NbsAnswerHist();
        hist.setNbsAnswerUid(1L);
        hist.setActUid(2L);
        hist.setAnswerTxt("No");
        hist.setNbsQuestionUid(3L);
        hist.setNbsQuestionVersionCtrlNbr(1);
        hist.setSeqNbr(1);
        hist.setAnswerLargeTxt("LargeTextHist");
        hist.setAnswerGroupSeqNbr(1);
        hist.setRecordStatusCd("I");
        hist.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        hist.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        hist.setLastChgUserId(11L);

        repository.mergeNbsAnswerHist(hist);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_NBS_ANSWER_HIST), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteByNbsAnswerUid_shouldCallUpdate() {
        repository.deleteByNbsAnswerUid(1L);

        verify(jdbcTemplateOdse, times(1)).update(eq(DELETE_NBS_ANSWER_BY_UID), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindByActUid_shouldReturnList() {
        NbsAnswer mockAnswer = new NbsAnswer();
        mockAnswer.setActUid(2L);

        when(jdbcTemplateOdse.query(eq(SELECT_NBS_ANSWER_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(mockAnswer));

        List<NbsAnswer> result = repository.findByActUid(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getActUid());
    }

    @Test
    void testFindByActUid_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_NBS_ANSWER_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<NbsAnswer> result = repository.findByActUid(999L);

        assertEquals(0, result.size());
    }
}
