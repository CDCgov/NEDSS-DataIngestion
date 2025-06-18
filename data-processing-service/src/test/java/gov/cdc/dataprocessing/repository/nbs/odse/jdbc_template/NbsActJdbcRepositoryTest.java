package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
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

import static gov.cdc.dataprocessing.constant.query.NbsActQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NbsActJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private NbsActJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMergeNbsActEntity_shouldCallUpdate() {
        NbsActEntity entity = new NbsActEntity();
        entity.setActUid(1L);
        entity.setAddTime(new Timestamp(System.currentTimeMillis()));
        entity.setAddUserId(10L);
        entity.setEntityUid(2L);
        entity.setEntityVersionCtrlNbr(1);
        entity.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        entity.setLastChgUserId(11L);
        entity.setRecordStatusCd("ACTIVE");
        entity.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        entity.setTypeCd("TYPE1");

        repository.mergeNbsActEntity(entity);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_NBS_ACT_ENTITY), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeNbsActEntityHist_shouldCallUpdate() {
        NbsActEntityHist hist = new NbsActEntityHist();
        hist.setNbsActEntityUid(100L);
        hist.setActUid(1L);
        hist.setAddTime(new Timestamp(System.currentTimeMillis()));
        hist.setAddUserId(10L);
        hist.setEntityUid(2L);
        hist.setEntityVersionCtrlNbr(1);
        hist.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        hist.setLastChgUserId(11L);
        hist.setRecordStatusCd("ACTIVE");
        hist.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        hist.setTypeCd("TYPE1");

        repository.mergeNbsActEntityHist(hist);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_NBS_ACT_ENTITY_HIST), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteNbsEntityAct_shouldCallUpdate() {
        repository.deleteNbsEntityAct(123L);

        verify(jdbcTemplateOdse, times(1)).update(eq(DELETE_NBS_ACT_ENTITY_BY_UID), any(MapSqlParameterSource.class));
    }

    @Test
    void testGetNbsActEntitiesByActUid_shouldReturnList() {
        NbsActEntity entity = new NbsActEntity();
        entity.setActUid(1L);

        when(jdbcTemplateOdse.query(eq(SELECT_NBS_ACT_ENTITIES_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(entity));

        List<NbsActEntity> result = repository.getNbsActEntitiesByActUid(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getActUid());
    }

    @Test
    void testGetNbsActEntitiesByActUid_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_NBS_ACT_ENTITIES_BY_ACT_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<NbsActEntity> result = repository.getNbsActEntitiesByActUid(999L);

        assertEquals(0, result.size());
    }
}
