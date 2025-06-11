package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
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

import static gov.cdc.dataprocessing.constant.query.ConfirmationMethodQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ConfirmationMethodJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private ConfirmationMethodJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpsertConfirmationMethod_shouldCallUpdate() {
        ConfirmationMethod method = new ConfirmationMethod();
        method.setPublicHealthCaseUid(123L);
        method.setConfirmationMethodCd("code");
        method.setConfirmationMethodDescTxt("description");
        method.setConfirmationMethodTime(new Timestamp(System.currentTimeMillis()));

        repository.upsertConfirmationMethod(method);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_CONFIRMATION_METHOD), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindByPublicHealthCaseUid_shouldReturnList() {
        ConfirmationMethod mockResult = new ConfirmationMethod();
        mockResult.setPublicHealthCaseUid(123L);

        when(jdbcTemplateOdse.query(eq(SELECT_CONFIRMATION_METHOD_BY_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(mockResult));

        List<ConfirmationMethod> result = repository.findByPublicHealthCaseUid(123L);

        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getPublicHealthCaseUid());
    }

    @Test
    void testFindByPublicHealthCaseUid_shouldReturnEmptyList() {
        when(jdbcTemplateOdse.query(eq(SELECT_CONFIRMATION_METHOD_BY_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<ConfirmationMethod> result = repository.findByPublicHealthCaseUid(999L);

        assertEquals(0, result.size());
    }
}