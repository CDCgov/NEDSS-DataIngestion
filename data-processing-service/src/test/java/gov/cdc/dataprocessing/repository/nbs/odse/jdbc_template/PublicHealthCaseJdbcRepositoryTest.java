package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PublicHealthCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PublicHealthCaseJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private PublicHealthCaseJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new PublicHealthCaseJdbcRepository(jdbcTemplate);
    }

    @Test
    void testFindById() {
        PublicHealthCase mockCase = new PublicHealthCase();
        mockCase.setPublicHealthCaseUid(123L);

        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(mockCase);

        PublicHealthCase result = repository.findById(123L);

        assertNotNull(result);
        assertEquals(123L, result.getPublicHealthCaseUid());
    }

    @Test
    void testInsertPublicHealthCase() {
        PublicHealthCase phc = new PublicHealthCase();
        phc.setPublicHealthCaseUid(1L);
        phc.setAddTime(new java.sql.Timestamp(System.currentTimeMillis()));
        phc.setAddUserId(100L);
        phc.setCd("TEST_CD");
        phc.setProgAreaCd("PROG1");
        phc.setRecordStatusCd("ACTIVE");
        phc.setStatusCd("OPEN");
        phc.setVersionCtrlNbr(1);

        when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

        assertDoesNotThrow(() -> repository.insertPublicHealthCase(phc));
    }

    @Test
    void testUpdatePublicHealthCase() {
        PublicHealthCase phc = new PublicHealthCase();
        phc.setPublicHealthCaseUid(1L);
        phc.setAddTime(new java.sql.Timestamp(System.currentTimeMillis()));
        phc.setAddUserId(100L);
        phc.setCd("TEST_CD");
        phc.setProgAreaCd("PROG1");
        phc.setRecordStatusCd("ACTIVE");
        phc.setStatusCd("OPEN");
        phc.setVersionCtrlNbr(1);

        when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

        int result = repository.updatePublicHealthCase(phc);
        assertEquals(1, result);
    }
}
