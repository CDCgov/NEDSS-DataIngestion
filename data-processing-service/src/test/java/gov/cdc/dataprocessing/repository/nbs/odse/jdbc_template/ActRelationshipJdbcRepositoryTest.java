package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.config.ServicePropertiesProvider;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ActRelationshipQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActRelationshipJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;
    @Mock
    private ServicePropertiesProvider servicePropertiesProvider;
    @InjectMocks
    private ActRelationshipJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(servicePropertiesProvider.getTz()).thenReturn("UTC");
    }

    @Test
    void testInsertActRelationship() {
        repository.insertActRelationship(createActRelationship());
        verify(jdbcTemplateOdse).update(eq(INSERT_SQL_ACT_RELATIONSHIP), any(MapSqlParameterSource.class));
    }
    @Test
    void testInsertActRelationship_null_statusTime() {
        ActRelationship actRelationship= createActRelationship();
        actRelationship.setStatusTime(null);
        repository.insertActRelationship(actRelationship);
        verify(jdbcTemplateOdse).update(eq(INSERT_SQL_ACT_RELATIONSHIP), any(MapSqlParameterSource.class));
    }
    @Test
    void testUpdateActRelationship() {
        repository.updateActRelationship(createActRelationship());
        verify(jdbcTemplateOdse).update(eq(UPDATE_SQL_ACT_RELATIONSHIP), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeActRelationship() {
        repository.mergeActRelationship(createActRelationship());
        verify(jdbcTemplateOdse).update(eq(MERGE_ACT_RELATIONSHIP), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertActRelationshipHistory() {
        repository.insertActRelationshipHistory(createActRelationshipHistory());
        verify(jdbcTemplateOdse).update(eq(CREATE_ACT_RELATIONSHIP_HISTORY), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteActRelationship() {
        ActRelationship act = new ActRelationship();
        act.setSourceActUid(1L);
        act.setTargetActUid(2L);
        act.setTypeCd("type");

        repository.deleteActRelationship(act);
        verify(jdbcTemplateOdse).update(eq(DELETE_SQL_ACT_RELATIONSHIP), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindBySourceActUid() {
        ActRelationship act = new ActRelationship();
        act.setSourceActUid(1L);

        when(jdbcTemplateOdse.query(eq(SELECT_BY_SOURCE), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(act));

        List<ActRelationship> result = repository.findBySourceActUid(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSourceActUid());
    }

    @Test
    void testFindBySourceActUidAndTypeCode() {
        ActRelationship act = new ActRelationship();
        act.setSourceActUid(1L);
        act.setTypeCd("type");

        when(jdbcTemplateOdse.query(eq(SELECT_BY_SOURCE_AND_TYPE_CODE), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(act));

        List<ActRelationship> result = repository.findBySourceActUidAndTypeCode(1L, "type");
        assertEquals(1, result.size());
        assertEquals("type", result.get(0).getTypeCd());
    }

    @Test
    void testFindByTargetActUid() {
        ActRelationship act = new ActRelationship();
        act.setTargetActUid(2L);

        when(jdbcTemplateOdse.query(eq(SELECT_BY_TARGET), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(act));

        List<ActRelationship> result = repository.findByTargetActUid(2L);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getTargetActUid());
    }

    private ActRelationship createActRelationship() {
        ActRelationship a = new ActRelationship();
        a.setSourceActUid(1L);
        a.setTargetActUid(2L);
        a.setTypeCd("type");
        a.setAddReasonCd("reason");
        a.setAddTime(new Timestamp(System.currentTimeMillis()));
        a.setAddUserId(1L);
        a.setDurationAmt("5");
        a.setDurationUnitCd("days");
        a.setFromTime(new Timestamp(System.currentTimeMillis()));
        a.setLastChgReasonCd("reason");
        a.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        a.setLastChgUserId(2L);
        a.setRecordStatusCd("active");
        a.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        a.setSequenceNbr(1);
        a.setSourceClassCd("sourceClass");
        a.setStatusCd("status");
        a.setStatusTime(new Timestamp(System.currentTimeMillis()));
        a.setTargetClassCd("targetClass");
        a.setToTime(new Timestamp(System.currentTimeMillis()));
        a.setTypeDescTxt("desc");
        a.setUserAffiliationTxt("affiliation");
        return a;
    }

    private ActRelationshipHistory createActRelationshipHistory() {
        ActRelationshipHistory h = new ActRelationshipHistory();
        h.setSourceActUid(1L);
        h.setTargetActUid(2L);
        h.setTypeCd("type");
        h.setVersionCrl(1);
        h.setAddReasonCd("reason");
        h.setAddTime(new Timestamp(System.currentTimeMillis()));
        h.setAddUserId(1L);
        h.setDurationAmt("5");
        h.setDurationUnitCd("days");
        h.setFromTime(new Timestamp(System.currentTimeMillis()));
        h.setLastChgReasonCd("reason");
        h.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        h.setLastChgUserId(2L);
        h.setRecordStatusCd("active");
        h.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        h.setSequenceNbr(1);
        h.setStatusCd("status");
        h.setStatusTime(new Timestamp(System.currentTimeMillis()));
        h.setSourceClassCd("sourceClass");
        h.setTargetClassCd("targetClass");
        h.setToTime(new Timestamp(System.currentTimeMillis()));
        h.setTypeDescTxt("desc");
        h.setUserAffiliationTxt("affiliation");
        return h;
    }
}