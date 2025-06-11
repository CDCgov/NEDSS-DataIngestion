package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.NbsInterfaceQuery.SELECT_NBS_INTERFACE_BY_UID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NbsInterfaceJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private NbsInterfaceJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNbsInterfaceByUid_shouldReturnModel() {
        NbsInterfaceModel mockModel = new NbsInterfaceModel();
        mockModel.setNbsInterfaceUid(100);

        when(jdbcTemplateOdse.queryForObject(eq(SELECT_NBS_INTERFACE_BY_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(mockModel);

        NbsInterfaceModel result = repository.getNbsInterfaceByUid(100);
        assertEquals(100, result.getNbsInterfaceUid());
    }

    @Test
    void testUpdateRecordStatusToRtiProcess_shouldHandleEmptyList() {
        int result = repository.updateRecordStatusToRtiProcess(Collections.emptyList());
        assertEquals(0, result);
        verify(jdbcTemplateOdse, never()).update(any(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateRecordStatusToRtiProcess_shouldUpdateInChunks() {
        List<Integer> ids = Arrays.asList(1, 2, 3);

        when(jdbcTemplateOdse.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(3);

        int result = repository.updateRecordStatusToRtiProcess(ids);

        assertEquals(3, result);
        verify(jdbcTemplateOdse, times(1)).update(anyString(), any(MapSqlParameterSource.class));
    }
}
