package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.constant.query.CaseManagementQuery.MERGE_CASE_MANAGEMENT;
import static gov.cdc.dataprocessing.constant.query.CaseManagementQuery.SELECT_CASE_MANAGEMENT_BY_PH_CASE_UID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CaseManagementJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private CaseManagementJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByPublicHealthCaseUid_shouldReturnCaseManagement() {
        CaseManagement expected = new CaseManagement();
        expected.setPublicHealthCaseUid(123L);

        when(jdbcTemplateOdse.queryForObject(
                eq(SELECT_CASE_MANAGEMENT_BY_PH_CASE_UID),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)))
                .thenReturn(expected);

        CaseManagement result = repository.findByPublicHealthCaseUid(123L);
        assertNotNull(result);
        assertEquals(123L, result.getPublicHealthCaseUid());
    }

    @Test
    void testFindByPublicHealthCaseUid_shouldReturnNullIfNotFound() {
        when(jdbcTemplateOdse.queryForObject(
                eq(SELECT_CASE_MANAGEMENT_BY_PH_CASE_UID),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        CaseManagement result = repository.findByPublicHealthCaseUid(999L);
        assertNull(result);
    }

    @Test
    void testMergeCaseManagement_shouldCallUpdate() {
        CaseManagement caseManagement = new CaseManagement();
        caseManagement.setCaseManagementUid(1L);
        caseManagement.setPublicHealthCaseUid(2L);
        caseManagement.setStatus900("active");
        caseManagement.setFldFollUpDispoDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setFldFollUpExamDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setFldFollUpExpectedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setInitFollUpClosedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setOojDueDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setOojInitgAgncyOutcDueDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setOojInitgAgncyOutcSntDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setOojInitgAgncyRecdDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setSurvAssignedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setFollUpAssignedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setInitFollUpAssignedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setInterviewAssignedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setInitInterviewAssignedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setCaseClosedDate(new Timestamp(System.currentTimeMillis()));
        caseManagement.setCaseReviewStatusDate(new Timestamp(System.currentTimeMillis()));

        repository.mergeCaseManagement(caseManagement);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_CASE_MANAGEMENT), any(MapSqlParameterSource.class));
    }
}