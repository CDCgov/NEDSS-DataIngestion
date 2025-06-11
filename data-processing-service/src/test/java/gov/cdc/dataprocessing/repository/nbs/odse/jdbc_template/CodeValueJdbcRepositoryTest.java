package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
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

import static gov.cdc.dataprocessing.constant.query.CodeValueQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CodeValueJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private CodeValueJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindCodeDescriptionsByCodeSetNm_shouldReturnResults() {
        CodeValueGeneral record = new CodeValueGeneral();
        record.setCodeSetNm("example");

        when(jdbcTemplateOdse.query(eq(SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_NM), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(record));

        List<CodeValueGeneral> result = repository.findCodeDescriptionsByCodeSetNm("example");

        assertEquals(1, result.size());
        assertEquals("example", result.get(0).getCodeSetNm());
    }

    @Test
    void testFindCodeValuesByCodeSetNm_shouldReturnResults() {
        CodeValueGeneral record = new CodeValueGeneral();
        record.setCodeSetNm("another");

        when(jdbcTemplateOdse.query(eq(SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_NM_ORDERED), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(record));

        List<CodeValueGeneral> result = repository.findCodeValuesByCodeSetNm("another");

        assertEquals(1, result.size());
        assertEquals("another", result.get(0).getCodeSetNm());
    }

    @Test
    void testFindCodeValuesByCodeSetNmAndCode_shouldReturnResults() {
        CodeValueGeneral record = new CodeValueGeneral();
        record.setCode("TEST_CODE");

        when(jdbcTemplateOdse.query(eq(SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_AND_CODE), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(record));

        List<CodeValueGeneral> result = repository.findCodeValuesByCodeSetNmAndCode("testSet", "TEST_CODE");

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
    }

    @Test
    void testFindCodeValuesByCodeSetNmAndCode_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_AND_CODE), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<CodeValueGeneral> result = repository.findCodeValuesByCodeSetNmAndCode("none", "none");

        assertEquals(0, result.size());
    }
}