package gov.cdc.nbs.deduplication.seed.listener;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.batch.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

class LastProcessedIdListenerTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    private NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate;

    @Mock
    private NamedParameterJdbcTemplate nbsNamedJdbcTemplate;

    @Mock
    private JobExecution jobExecution;

    @Mock
    private JobParameters jobParameters;

    @InjectMocks
    private LastProcessedIdListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAfterJob_Success() {
        Long lastProcessedId = 200L;

        // Mock jobExecution to return a completed status
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // Mock jobParameters to return lastProcessedId
        when(jobExecution.getJobParameters()).thenReturn(jobParameters);
        when(jobParameters.getLong("lastProcessedId")).thenReturn(lastProcessedId);

        // Mock the jdbcTemplate to return 1 to simulate a successful update
        when(deduplicationNamedJdbcTemplate.update(anyString(), anyMap())).thenReturn(1);

        // Call the afterJob method
        listener.afterJob(jobExecution);

        // Capture the parameters passed to the update method
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        // Now verify the update method call and capture arguments
        verify(deduplicationNamedJdbcTemplate).update(eq("UPDATE last_processed_id SET last_processed_id = :lastProcessedId WHERE id = 1"), paramsCaptor.capture());

        // Get the captured parameters
        Map<String, Object> capturedParams = paramsCaptor.getValue();

        // Verify the captured parameters contain the expected lastProcessedId
        assertThat(capturedParams)
                .isNotNull()
                .containsEntry("lastProcessedId", lastProcessedId);
    }

    @Test
    void testAfterJob_NoLastProcessedId() {
        // Mock jobExecution to return a completed status
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // Mock jobParameters to return null for lastProcessedId
        when(jobExecution.getJobParameters()).thenReturn(jobParameters);
        when(jobParameters.getLong("lastProcessedId")).thenReturn(null);

        listener.afterJob(jobExecution);

        // Verify that update method is not called since lastProcessedId is null
        verify(jdbcTemplate, never()).update(anyString(), any(Map.class));
    }

    @Test
    void testAfterJob_FailedJob() {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);

        listener.afterJob(jobExecution);

        // Verify that update method is not called for a failed job
        verify(jdbcTemplate, never()).update(anyString(), any(Map.class));
    }

    @Test
    void testGetSmallestPersonId_Success() {
        // Mock queryForObject to return the expected smallest person ID
        when(nbsNamedJdbcTemplate.queryForObject(
                eq("SELECT MIN(person_uid) FROM person"),
                anyMap(),
                eq(Long.class))
        ).thenReturn(null);

        // Call the method
        Long smallestPersonId = listener.getSmallestPersonId();

        // Verify that the returned ID is correct
        assertThat(smallestPersonId).isNull();
    }

    @Test
    void testGetLargestProcessedId_Success() {
        Long lastProcessedId = 123L;
        when(listener.getLastProcessedId()).thenReturn(lastProcessedId);

        // Prepare the expected parameters map
        Map<String, Object> params = new HashMap<>();
        params.put("lastProcessedId", lastProcessedId);

        // Mock the database query with the exact map and expected result
        Long expectedLargestProcessedId = 200L;
        when(nbsNamedJdbcTemplate.queryForObject(
                eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
                eq(params),
                eq(Long.class))
        ).thenReturn(expectedLargestProcessedId);

        // Call the method
        Long largestProcessedId = listener.getLargestProcessedId();

        // Assert the result
        assertThat(largestProcessedId).isEqualTo(expectedLargestProcessedId);

        // Verify the query was called with the correct parameters
        verify(nbsNamedJdbcTemplate).queryForObject(
                eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
                eq(params),
                eq(Long.class)
        );
    }

    @Test
    void testGetLargestProcessedId_Failure() {
        // Mock getLastProcessedId to return a specific last processed ID
        Long lastProcessedId = 100L;
        when(listener.getLastProcessedId()).thenReturn(lastProcessedId);

        // Prepare the expected parameters map
        Map<String, Object> params = new HashMap<>();
        params.put("lastProcessedId", lastProcessedId);

        // Mock queryForObject to throw an exception
        when(nbsNamedJdbcTemplate.queryForObject(
                eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
                eq(params),
                eq(Long.class))
        ).thenThrow(new RuntimeException("Database error"));

        // Call the method
        Long largestProcessedId = listener.getLargestProcessedId();

        // Assert that the returned ID is null in case of an exception
        assertThat(largestProcessedId).isNull();

        // Verify that the query was called with the correct parameters
        verify(nbsNamedJdbcTemplate).queryForObject(
                eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
                eq(params),
                eq(Long.class)
        );
    }

    @Test
    void testGetLargestProcessedId_WhenNoLastProcessedId() {
        // Mock getLastProcessedId to return null (i.e., no last processed ID)
        when(listener.getLastProcessedId()).thenReturn(null);

        // Prepare the expected parameters map
        Map<String, Object> params = new HashMap<>();
        params.put("lastProcessedId", null);

        // Mock queryForObject to return the expected largest person ID
        Long expectedLargestProcessedId = 200L;
        when(nbsNamedJdbcTemplate.queryForObject(
                eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
                eq(params),
                eq(Long.class))
        ).thenReturn(expectedLargestProcessedId);

        // Call the method
        Long largestProcessedId = listener.getLargestProcessedId();

        // Assert that the returned ID is correct
        assertThat(largestProcessedId).isEqualTo(expectedLargestProcessedId);

        // Verify that the query was called with the correct parameters
        verify(nbsNamedJdbcTemplate).queryForObject(
                eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
                eq(params),
                eq(Long.class)
        );
    }

    @Test
    void testUpdateLastProcessedId_Success() {
        // Mock getLastProcessedId to return a valid ID
        Long lastProcessedId = 123L;
        when(listener.getLastProcessedId()).thenReturn(lastProcessedId);

        // Mock jdbcTemplate.update to return the expected result (e.g., 1 row updated)
        when(jdbcTemplate.update(
                eq("UPDATE last_processed_id SET last_processed_id = :lastProcessedId WHERE id = 1"),
                anyMap())
        ).thenReturn(1); // Ensure the mock returns a positive result

        // Call the method to update last processed ID
        listener.updateLastProcessedId(lastProcessedId);

        // Verify the update method was invoked with the correct SQL and parameters
        verify(jdbcTemplate).update(
                eq("UPDATE last_processed_id SET last_processed_id = :lastProcessedId WHERE id = 1"),
                anyMap()
        );
    }


    @Test
    void testUpdateLastProcessedId_NoRowsUpdated() {
        when(deduplicationNamedJdbcTemplate.update(anyString(), anyMap())).thenReturn(0);

        assertThatThrownBy(() -> listener.updateLastProcessedId(250L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to update the last processed ID");
    }

    @Test
    void testUpdateLastProcessedId_ExceptionThrown() {
        when(deduplicationNamedJdbcTemplate.update(anyString(), anyMap()))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> listener.updateLastProcessedId(250L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to update the last processed ID");
    }

}