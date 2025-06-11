package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
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

import static gov.cdc.dataprocessing.constant.query.EntityQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EntityIdJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private EntityIdJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEntityId_shouldCallUpdate() {
        EntityId entity = createSample();
        repository.createEntityId(entity);
        verify(jdbcTemplateOdse, times(1)).update(eq(INSERT_SQL_ENTITY_ID), any(MapSqlParameterSource.class));
    }

    @Test
    void testBatchCreateEntityIds_shouldCallBatchUpdate() {
        List<EntityId> list = List.of(createSample(), createSample());
        repository.batchCreateEntityIds(list);
        verify(jdbcTemplateOdse, times(1)).batchUpdate(eq(INSERT_SQL_ENTITY_ID), any(MapSqlParameterSource[].class));
    }

    @Test
    void testMergeEntityId_shouldCallUpdate() {
        EntityId entity = createSample();
        repository.mergeEntityId(entity);
        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_ENTITY_ID), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindEntityIds_shouldReturnList() {
        EntityId mock = new EntityId();
        mock.setEntityUid(1L);

        when(jdbcTemplateOdse.query(eq(SELECT_ENTITY_ID_BY_ENTITY_ID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(mock));

        List<EntityId> result = repository.findEntityIds(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testFindEntityIdsActive_shouldReturnList() {
        EntityId mock = new EntityId();
        mock.setEntityUid(2L);

        when(jdbcTemplateOdse.query(eq(SELECT_ENTITY_ID_BY_ENTITY_ID_ACTIVE), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(mock));

        List<EntityId> result = repository.findEntityIdsActive(2L);
        assertEquals(1, result.size());
    }

    private EntityId createSample() {
        EntityId e = new EntityId();
        e.setEntityUid(1L);
        e.setEntityIdSeq(1);
        e.setAddReasonCode("reason");
        e.setAddTime(new Timestamp(System.currentTimeMillis()));
        e.setAddUserId(101L);
        e.setAssigningAuthorityCode("authCd");
        e.setAssigningAuthorityDescription("desc");
        e.setDurationAmount("5");
        e.setDurationUnitCode("days");
        e.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        e.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        e.setLastChangeReasonCode("chg");
        e.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
        e.setLastChangeUserId(102L);
        e.setRecordStatusCode("active");
        e.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        e.setRootExtensionText("root");
        e.setStatusCode("status");
        e.setStatusTime(new Timestamp(System.currentTimeMillis()));
        e.setTypeCode("type");
        e.setTypeDescriptionText("desc");
        e.setUserAffiliationText("affiliation");
        e.setValidFromTime(new Timestamp(System.currentTimeMillis()));
        e.setValidToTime(new Timestamp(System.currentTimeMillis()));
        e.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        e.setAssigningAuthorityIdType("typeX");
        return e;
    }
}
