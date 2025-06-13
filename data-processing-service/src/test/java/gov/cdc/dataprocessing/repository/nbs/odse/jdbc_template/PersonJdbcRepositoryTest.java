package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;

import static org.mockito.Mockito.*;

class PersonJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private PersonJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePerson() {
        repository.createPerson(new Person());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindMprUid() {
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(123L);
        repository.findMprUid(100L);
        verify(jdbcTemplate).queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class));
    }

    @Test
    void testFindPersonsByParentUid() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        repository.findPersonsByParentUid(101L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testFindByPersonUid() {
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(new Person());
        repository.findByPersonUid(102L);
        verify(jdbcTemplate).queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testCreatePersonName() {
        repository.createPersonName(new PersonName());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindPersonNameByPersonUid() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        repository.findPersonNameByPersonUid(103L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testCreatePersonRace() {
        repository.createPersonRace(new PersonRace());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindPersonRaceByPersonUid() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        repository.findPersonRaceByPersonUid(104L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testCreatePersonEthnicGroup() {
        repository.createPersonEthnicGroup(new PersonEthnicGroup());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindPersonEthnicByPersonUid() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        repository.findPersonEthnicByPersonUid(105L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testUpdatePerson() {
        repository.updatePerson(new Person());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testSelectByPersonUid() {
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(new Person());
        repository.selectByPersonUid(106L);
        verify(jdbcTemplate).queryForObject(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testMergePersonName() {
        repository.mergePersonName(new PersonName());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindBySeqIdByParentUid() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        repository.findBySeqIdByParentUid(107L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testMergePersonRace() {
        repository.mergePersonRace(new PersonRace());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindByPersonRaceUid() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        repository.findByPersonRaceUid(108L);
        verify(jdbcTemplate).query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testMergePersonEthnicGroup() {
        repository.mergePersonEthnicGroup(new PersonEthnicGroup());
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }
}
