package gov.cdc.nbs.deduplication.seed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import gov.cdc.nbs.deduplication.config.container.UseTestContainers;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.MpiPerson;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@UseTestContainers
class SeedingTest {

  private static final String NBS_QUERY = """
      SELECT
      (
      	SELECT
      		count(person_uid)
      	FROM
      		person
      	WHERE
      		person_uid = person_parent_uid
      		AND person.cd = 'PAT'
      		AND person.record_status_cd = 'ACTIVE'
      ) AS unique_persons,
      (
      	SELECT
      		count(person_uid)
      	FROM
      		person
      	WHERE
      		person.cd = 'PAT'
      		AND person.record_status_cd = 'ACTIVE'
      ) AS total_records;
           """;

  private static final String MPI_QUERY = """
      SELECT
      (
      	SELECT
      		count(id)
      	FROM
      		mpi_person
      ) AS unique_persons,
      (
      	SELECT
      		count(id)
      	FROM
      		mpi_patient
      ) AS total_records;
      """;

  private static final String MPI_DATA_SELECT = """
      SELECT
      	data
      FROM
      	mpi_patient
      WHERE
      	external_patient_id = '10000001';
            """;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  @Qualifier("nbsTemplate")
  private JdbcTemplate nbsTemplate;

  @Autowired
  @Qualifier("mpiTemplate")
  private JdbcTemplate mpiTemplate;

  @Autowired
  private ObjectMapper mapper;

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
    // Kick off seeding job
    jobLauncherTestUtils.setJob(seedJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    // Verify seeding was completed successfully
    assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

    // Verify nbs and mpi counts match
    validateCounts();

    // Verify a known patients content
    String rawData = mpiTemplate.queryForObject(MPI_DATA_SELECT, String.class);
    MpiPerson mpiData = mapper.readValue(rawData, MpiPerson.class);

    // Personal
    assertThat(mpiData.birth_date()).isEqualTo("1990-01-01");
    assertThat(mpiData.sex()).isEqualTo("M");
    assertThat(mpiData.race()).isEqualTo("ASIAN");

    // Address
    assertThat(mpiData.address()).hasSize(1);
    assertThat(mpiData.address().get(0).line()).hasSize(1);
    assertThat(mpiData.address().get(0).line().get(0)).isEqualTo("123 Main St.");
    assertThat(mpiData.address().get(0).city()).isEqualTo("Atlanta");
    assertThat(mpiData.address().get(0).state()).isEqualTo("Georgia");
    assertThat(mpiData.address().get(0).postal_code()).isEqualTo("30024");
    assertThat(mpiData.address().get(0).county()).isEqualTo("Gwinnett County");

    // Name
    assertThat(mpiData.name()).hasSize(1);
    assertThat(mpiData.name().get(0).given()).hasSize(2);
    assertThat(mpiData.name().get(0).given().get(0)).isEqualTo("Surma");
    assertThat(mpiData.name().get(0).given().get(1)).isEqualTo("J");
    assertThat(mpiData.name().get(0).family()).isEqualTo("Singh");
    assertThat(mpiData.name().get(0).suffix()).isEmpty();

    // Phone
    assertThat(mpiData.telecom()).hasSize(3);
    assertThat(mpiData.telecom().get(0).value()).isEqualTo("2323222222");
    assertThat(mpiData.telecom().get(1).value()).isEqualTo("4562323222");
    assertThat(mpiData.telecom().get(2).value()).isEqualTo("2323222222");
  }

  private void validateCounts() {
    // query to check NBS counts
    RowCount nbsCount = getNbsPersonCount();
    assertThat(nbsCount.unique()).isPositive();
    assertThat(nbsCount.total()).isPositive();

    // verify MPI counts match NBS
    RowCount mpiCount = getMpiPersonCount();
    assertThat(mpiCount.unique()).isEqualTo(nbsCount.unique());
    assertThat(mpiCount.total()).isEqualTo(nbsCount.total());
  }

  private RowCount getNbsPersonCount() {
    return nbsTemplate.queryForObject(NBS_QUERY, new RowCountMapper());
  }

  private RowCount getMpiPersonCount() {
    return mpiTemplate.queryForObject(MPI_QUERY, new RowCountMapper());
  }

  private record RowCount(Integer unique, Integer total) {
  }

  private class RowCountMapper implements RowMapper<RowCount> {
    @Override
    public RowCount mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
      return new RowCount(rs.getInt("unique_persons"), rs.getInt("total_records"));
    }
  }


  @Test
  void startSeed_firstRun() throws Exception {
    // Mock behavior for the first run (lastProcessedId is null)
    when(deduplicationNamedJdbcTemplate.queryForObject(
            eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"),
            anyMap(),
            eq(Long.class))
    ).thenReturn(null);

    // Mock the minimum person_uid
    when(nbsNamedJdbcTemplate.queryForObject(
            eq("SELECT MIN(person_uid) FROM person"),
            anyMap(),
            eq(Long.class))
    ).thenReturn(5L);

    // Mock the maximum person_uid
    when(nbsNamedJdbcTemplate.queryForObject(
            eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"),
            anyMap(),
            eq(Long.class))
    ).thenReturn(100L);

    // Verify the parameters passed to the job launcher
    doAnswer(invocation -> {
      JobParameters parameters = invocation.getArgument(1);
      Long lastProcessedIdParam = parameters.getLong("lastProcessedId");

      // Assert lastProcessedId is correctly set and is not null
      Assertions.assertNotNull(lastProcessedIdParam, "lastProcessedId should not be null");
      Assertions.assertEquals(Long.valueOf(5L), lastProcessedIdParam, "lastProcessedId should be 5");

      return null;
    }).when(launcher).run(eq(seedJob), any(JobParameters.class));

    // Call the method
    seedController.startSeed();

    // Verify the minimum person_uid query was called
    verify(nbsNamedJdbcTemplate).queryForObject(
            eq("SELECT MIN(person_uid) FROM person"),
            anyMap(),
            eq(Long.class)
    );

    // Verify the job launcher was called with the correct parameters
    verify(launcher).run(eq(seedJob), any(JobParameters.class));

    // Verify the last_processed_id was updated
    verify(deduplicationNamedJdbcTemplate).update(
            eq("UPDATE last_processed_id SET last_processed_id = :largestProcessedId WHERE id = 1"),
            anyMap()
    );
  }















  @Test
  void startSeed_subsequentRun() throws Exception {
    // Mock behavior for a subsequent run (lastProcessedId exists)
    when(deduplicationNamedJdbcTemplate.queryForObject(eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"), any(HashMap.class), eq(Long.class)))
            .thenReturn(100L); // Existing lastProcessedId
    when(nbsNamedJdbcTemplate.queryForObject(eq("SELECT MAX(person_uid) FROM person WHERE person_uid > :lastProcessedId"), any(HashMap.class), eq(Long.class)))
            .thenReturn(200L); // Largest processed ID

    // Mock the job launcher
    doNothing().when(launcher).run(eq(seedJob), any(JobParameters.class));

    // Call the method
    seedController.startSeed();

    // Verify the lastProcessedId is fetched
    verify(deduplicationNamedJdbcTemplate).queryForObject(eq("SELECT last_processed_id FROM last_processed_id WHERE id = 1"), any(HashMap.class), eq(Long.class));

    // Verify the job launcher is invoked with the correct parameters
    verify(launcher).run(eq(seedJob), any(JobParameters.class));

    // Verify the largestProcessedId is updated
    verify(deduplicationNamedJdbcTemplate).update(eq("UPDATE last_processed_id SET last_processed_id = :largestProcessedId WHERE id = 1"), any(HashMap.class));
  }

}
