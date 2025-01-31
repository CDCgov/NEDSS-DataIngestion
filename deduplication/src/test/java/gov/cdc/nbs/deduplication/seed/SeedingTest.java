package gov.cdc.nbs.deduplication.seed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class SeedingTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Mock
  private JobLauncher launcher;

  @Mock
  private Job seedJob;

  @Mock
  private NamedParameterJdbcTemplate deduplicationNamedJdbcTemplate;

  @Mock
  private NamedParameterJdbcTemplate nbsNamedJdbcTemplate;

  @InjectMocks
  private SeedController seedController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void seedMpiTest(@Autowired Job seedJob) throws Exception {
    jobLauncherTestUtils.setJob(seedJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
  }

  @Test
  void startSeed_firstRun() throws Exception {
    when(deduplicationNamedJdbcTemplate.queryForObject(
            eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"),
            anyMap(),
            eq(Long.class))
    ).thenReturn(null);

    when(nbsNamedJdbcTemplate.queryForObject(
            eq("SELECT MIN(person_uid) FROM person"),
            anyMap(),
            eq(Long.class))
    ).thenReturn(5L);

    doNothing().when(launcher).run(eq(seedJob), any(JobParameters.class));

    seedController.startSeed();

    verify(nbsNamedJdbcTemplate).queryForObject(eq("SELECT MIN(person_uid) FROM person"), anyMap(), eq(Long.class));
    verify(launcher).run(eq(seedJob), any(JobParameters.class));
    verify(deduplicationNamedJdbcTemplate).update(eq("UPDATE last_processed_id SET last_processed_id = :largestProcessedId WHERE id = 1"), anyMap());
  }

  @Test
  void startSeed_subsequentRun() throws Exception {
    when(deduplicationNamedJdbcTemplate.queryForObject(eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"), any(HashMap.class), eq(Long.class)))
            .thenReturn(100L);
    when(nbsNamedJdbcTemplate.queryForObject(eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"), any(HashMap.class), eq(Long.class)))
            .thenReturn(200L);

    doNothing().when(launcher).run(eq(seedJob), any(JobParameters.class));
    seedController.startSeed();

    verify(deduplicationNamedJdbcTemplate).queryForObject(eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"), any(HashMap.class), eq(Long.class));
    verify(launcher).run(eq(seedJob), any(JobParameters.class));
    verify(deduplicationNamedJdbcTemplate).update(eq("UPDATE last_processed_id SET last_processed_id = :largestProcessedId WHERE id = 1"), any(HashMap.class));
  }

  @Test
  void startSeed_invalidSqlQueryForLastProcessedId() throws Exception {
    when(deduplicationNamedJdbcTemplate.queryForObject(
            eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"),
            anyMap(),
            eq(Long.class))
    ).thenThrow(new SQLException("Database error"));

    assertThrows(SQLException.class, () -> seedController.startSeed());
  }
}
