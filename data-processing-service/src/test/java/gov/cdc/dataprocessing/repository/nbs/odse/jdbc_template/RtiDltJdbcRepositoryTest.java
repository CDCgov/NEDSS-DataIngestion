package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.RtiDlt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RtiDltJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private RtiDltJdbcRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_WhenExists_ReturnsRtiDlt() {
        // Arrange
        String id = "test-uuid";
        RtiDlt expected = new RtiDlt();
        expected.setId(id);
        expected.setNbsInterfaceId(123L);
        expected.setStatus("SUCCESS");
        expected.setPayload("somePayload");
        expected.setStackTrace("none");
        expected.setCreatedOn(Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
        expected.setUpdatedOn(Timestamp.valueOf(LocalDateTime.now()));

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM rti_dlt WHERE id = :id"),
                eq(Map.of("id", id)),
                any(RowMapper.class)
        )).thenReturn(expected);

        // Act
        RtiDlt result = repository.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(123L, result.getNbsInterfaceId());
    }

    @Test
    void testFindById_WhenNotFound_ReturnsNull() {
        // Arrange
        String id = "missing-id";
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(Map.class),
                any(RowMapper.class)
        )).thenThrow(new EmptyResultDataAccessException(1));

        // Act
        RtiDlt result = repository.findById(id);

        // Assert
        assertNull(result);
    }

    @Test
    void testUpsert_PerformsUpdate() {
        // Arrange
        RtiDlt rti = new RtiDlt();
        rti.setId("id-123");
        rti.setNbsInterfaceId(99L);
        rti.setStatus("OK");
        rti.setStackTrace("trace");
        rti.setPayload("payload");
        rti.setCreatedOn(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        rti.setUpdatedOn(Timestamp.valueOf(LocalDateTime.now()));

        // Act
        repository.upsert(rti);

        // Assert
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testMapRow_ReturnsValidRtiDlt() throws Exception {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("uuid-1234");
        when(rs.getLong("nbs_interface_id")).thenReturn(101L);
        when(rs.getString("status")).thenReturn("FAILED");
        when(rs.getString("stack_trace")).thenReturn("stacktrace here");
        when(rs.getString("payload")).thenReturn("{json}");

        RtiDltJdbcRepository blah = new RtiDltJdbcRepository(null); // jdbcTemplate not needed here

        // Act
        RtiDlt result = blah.mapRow(rs, 0);

        // Assert
        assertNotNull(result);
        assertEquals("uuid-1234", result.getId());
        assertEquals(101L, result.getNbsInterfaceId());
        assertEquals("FAILED", result.getStatus());
        assertEquals("stacktrace here", result.getStackTrace());
        assertEquals("{json}", result.getPayload());
    }

    @Test
    void testFindByNbsInterfaceId_WhenFound_ReturnsList() {
        Long interfaceId = 123L;
        RtiDlt dlt1 = new RtiDlt();
        dlt1.setId("id1");
        RtiDlt dlt2 = new RtiDlt();
        dlt2.setId("id2");

        String expectedSql = "SELECT * FROM rti_dlt WHERE nbs_interface_id = :id ORDER BY created_on DESC";

        when(jdbcTemplate.query(eq(expectedSql), eq(Map.of("id", interfaceId)), any(RowMapper.class)))
                .thenReturn(List.of(dlt1, dlt2));

        List<RtiDlt> result = repository.findByNbsInterfaceId(interfaceId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("id1", result.get(0).getId());
        assertEquals("id2", result.get(1).getId());
    }

    @Test
    void testFindByNbsInterfaceId_WhenNotFound_ReturnsEmptyList() {
        Long interfaceId = 999L;

        when(jdbcTemplate.query(anyString(), any(Map.class), any(RowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        List<RtiDlt> result = repository.findByNbsInterfaceId(interfaceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUnSuccessStatus_WhenFound_ReturnsList() {
        RtiDlt dlt = new RtiDlt();
        dlt.setId("unsuccess-id");

        String expectedSql = "SELECT * FROM rti_dlt WHERE status != 'SUCCESS' ORDER BY created_on DESC";

        when(jdbcTemplate.query(eq(expectedSql), any(RowMapper.class)))
                .thenReturn(List.of(dlt));

        List<RtiDlt> result = repository.findByUnSuccessStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("unsuccess-id", result.get(0).getId());
    }

    @Test
    void testFindByUnSuccessStatus_WhenNone_ReturnsEmptyList() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        List<RtiDlt> result = repository.findByUnSuccessStatus();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}