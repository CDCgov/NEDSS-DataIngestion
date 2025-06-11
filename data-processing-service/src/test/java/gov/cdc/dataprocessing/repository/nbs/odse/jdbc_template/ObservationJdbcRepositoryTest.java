package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class ObservationJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private ObservationJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindObservationByUid() {
        Long id = 1L;
        Observation mockObs = new Observation();
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(mockObs);

        repository.findObservationByUid(id);

        verify(jdbcTemplate).queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testInsertObservation() {
        Observation observation = new Observation();
        repository.insertObservation(observation);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObservation() {
        Observation observation = new Observation();
        repository.updateObservation(observation);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertObservationReason() {
        ObservationReason reason = new ObservationReason();
        repository.insertObservationReason(reason);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObservationReason() {
        ObservationReason reason = new ObservationReason();
        repository.updateObservationReason(reason);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteObservationReason() {
        ObservationReason reason = new ObservationReason();
        repository.deleteObservationReason(reason);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertObservationInterp() {
        ObservationInterp interp = new ObservationInterp();
        repository.insertObservationInterp(interp);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObservationInterp() {
        ObservationInterp interp = new ObservationInterp();
        repository.updateObservationInterp(interp);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteObservationInterp() {
        ObservationInterp interp = new ObservationInterp();
        repository.deleteObservationInterp(interp);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertObsValueCoded() {
        ObsValueCoded coded = new ObsValueCoded();
        repository.insertObsValueCoded(coded);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObsValueCoded() {
        ObsValueCoded coded = new ObsValueCoded();
        repository.updateObsValueCoded(coded);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteObsValueCoded() {
        ObsValueCoded coded = new ObsValueCoded();
        repository.deleteObsValueCoded(coded);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertObsValueTxt() {
        ObsValueTxt txt = new ObsValueTxt();
        repository.insertObsValueTxt(txt);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObsValueTxt() {
        ObsValueTxt txt = new ObsValueTxt();
        repository.updateObsValueTxt(txt);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteObsValueTxt() {
        ObsValueTxt txt = new ObsValueTxt();
        repository.deleteObsValueTxt(txt);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertObsValueDate() {
        ObsValueDate date = new ObsValueDate();
        repository.insertObsValueDate(date);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObsValueDate() {
        ObsValueDate date = new ObsValueDate();
        repository.updateObsValueDate(date);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteObsValueDate() {
        ObsValueDate date = new ObsValueDate();
        repository.deleteObsValueDate(date);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testInsertObsValueNumeric() {
        ObsValueNumeric numeric = new ObsValueNumeric();
        repository.insertObsValueNumeric(numeric);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateObsValueNumeric() {
        ObsValueNumeric numeric = new ObsValueNumeric();
        repository.updateObsValueNumeric(numeric);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testDeleteObsValueNumeric() {
        ObsValueNumeric numeric = new ObsValueNumeric();
        repository.deleteObsValueNumeric(numeric);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testRetrieveObservationQuestion() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        repository.retrieveObservationQuestion(1L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testFindByObservationReasons() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());

        repository.findByObservationReasons(1L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }
}
