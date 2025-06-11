package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private NotificationJdbcRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        repository = new NotificationJdbcRepository(jdbcTemplate);
    }

    @Test
    void testInsertNotification() {
        Notification notification = mockNotification();

        repository.insertNotification(notification);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateNotification() {
        Notification notification = mockNotification();

        repository.updateNotification(notification);
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindById_found() {
        Notification expected = mockNotification();
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), ArgumentMatchers.<BeanPropertyRowMapper<Notification>>any()))
                .thenReturn(List.of(expected));

        Notification result = repository.findById(100L);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testFindById_notFound() {
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), ArgumentMatchers.<BeanPropertyRowMapper<Notification>>any()))
                .thenReturn(Collections.emptyList());

        Notification result = repository.findById(999L);
        assertNull(result);
    }

    private Notification mockNotification() {
        Notification n = new Notification();
        n.setNotificationUid(1L);
        n.setLocalId("LOCAL-123");
        n.setAddTime(new Timestamp(System.currentTimeMillis()));
        n.setRecordStatusCd("ACTIVE");
        n.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        n.setAddUserId(10L);
        return n;
    }
}
