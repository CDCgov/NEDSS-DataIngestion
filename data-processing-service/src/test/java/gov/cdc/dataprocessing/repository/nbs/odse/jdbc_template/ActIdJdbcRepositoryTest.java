package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ActIdQuery.MERGE_SQL_ACT_ID;
import static gov.cdc.dataprocessing.constant.query.ActIdQuery.SELECT_BY_ACT_UID_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ActIdJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private ActIdJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMergeActId_shouldCallUpdateWithCorrectParameters() {
        ActId actId = new ActId();
        actId.setActUid(1L);
        actId.setActIdSeq(1);
        actId.setAddReasonCd("reason");
        actId.setAddTime(Timestamp.from(Instant.now()));
        actId.setAddUserId(100L);
        actId.setAssigningAuthorityCd("authCd");
        actId.setAssigningAuthorityDescTxt("desc");
        actId.setDurationAmt("10");
        actId.setDurationUnitCd("days");
        actId.setLastChgReasonCd("chgReason");
        actId.setLastChgTime(Timestamp.from(Instant.now()));
        actId.setLastChgUserId(101L);
        actId.setRecordStatusCd("active");
        actId.setRecordStatusTime(Timestamp.from(Instant.now()));
        actId.setRootExtensionTxt("root");
        actId.setStatusCd("status");
        actId.setStatusTime(Timestamp.from(Instant.now()));
        actId.setTypeCd("type");
        actId.setTypeDescTxt("typeDesc");
        actId.setUserAffiliationTxt("affiliation");
        actId.setValidFromTime(Timestamp.from(Instant.now()));
        actId.setValidToTime(Timestamp.from(Instant.now()));

        repository.mergeActId(actId);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_SQL_ACT_ID), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindRecordsByActUid_shouldReturnRecords() {
        Long actUid = 1L;
        ActId mockActId = new ActId();
        mockActId.setActUid(actUid);

        when(jdbcTemplateOdse.query(eq(SELECT_BY_ACT_UID_SQL), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(mockActId));

        List<ActId> result = repository.findRecordsByActUid(actUid);

        assertEquals(1, result.size());
        assertEquals(actUid, result.get(0).getActUid());
    }

    @Test
    void testFindRecordsByActUid_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_BY_ACT_UID_SQL), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<ActId> result = repository.findRecordsByActUid(999L);

        assertEquals(0, result.size());
    }
}
