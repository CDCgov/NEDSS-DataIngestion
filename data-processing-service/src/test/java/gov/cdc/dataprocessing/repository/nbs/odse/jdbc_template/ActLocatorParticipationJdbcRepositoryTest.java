package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
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

import static gov.cdc.dataprocessing.constant.query.ActLocatorParticipationQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ActLocatorParticipationJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private ActLocatorParticipationJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertActLocatorParticipation_shouldCallUpdate() {
        ActLocatorParticipation a = createSample();
        repository.insertActLocatorParticipation(a);
        verify(jdbcTemplateOdse).update(eq(INSERT_SQL_ACT_LOCATOR_PAT), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateActLocatorParticipation_shouldCallUpdate() {
        ActLocatorParticipation a = createSample();
        repository.updateActLocatorParticipation(a);
        verify(jdbcTemplateOdse).update(eq(UPDATE_SQL_ACT_LOCATOR_PAT), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeActLocatorParticipation_shouldCallUpdate() {
        ActLocatorParticipation a = createSample();
        repository.mergeActLocatorParticipation(a);
        verify(jdbcTemplateOdse).update(eq(MERGE_ACT_LOCATOR), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindByActUid_shouldReturnList() {
        ActLocatorParticipation mockRecord = new ActLocatorParticipation();
        mockRecord.setActUid(1L);

        when(jdbcTemplateOdse.query(eq(SELECT_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(mockRecord));

        List<ActLocatorParticipation> result = repository.findByActUid(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getActUid());
    }

    @Test
    void testFindByActUid_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<ActLocatorParticipation> result = repository.findByActUid(999L);

        assertEquals(0, result.size());
    }

    private ActLocatorParticipation createSample() {
        ActLocatorParticipation a = new ActLocatorParticipation();
        a.setEntityUid(1L);
        a.setActUid(2L);
        a.setLocatorUid(3L);
        a.setAddReasonCd("reason");
        a.setAddTime(new Timestamp(System.currentTimeMillis()));
        a.setAddUserId(100L);
        a.setDurationAmount("5");
        a.setDurationUnitCd("days");
        a.setFromTime(new Timestamp(System.currentTimeMillis()));
        a.setLastChangeReasonCd("update");
        a.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
        a.setLastChangeUserId(101L);
        a.setRecordStatusCd("active");
        a.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        a.setToTime(new Timestamp(System.currentTimeMillis()));
        a.setStatusCd("complete");
        a.setStatusTime(new Timestamp(System.currentTimeMillis()));
        a.setTypeCd("type");
        a.setTypeDescTxt("description");
        a.setUserAffiliationTxt("affiliation");
        return a;
    }
}
