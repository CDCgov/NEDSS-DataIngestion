package gov.cdc.nbs.deduplication.seed;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.config.container.UseTestContainers;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

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
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier("nbsTemplate")
  private JdbcTemplate nbsTemplate;

  @Autowired
  @Qualifier("mpiTemplate")
  private JdbcTemplate mpiTemplate;

  @Autowired
  private ObjectMapper mapper;

  @Test
  void seedMpiTest(@Autowired @Qualifier("seedJob") Job seedJob) throws Exception {
    // Kick off seeding job
    jobLauncherTestUtils.setJob(seedJob);
    jobLauncherTestUtils.setJobLauncher(jobLauncher);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    // Verify seeding was completed successfully
    assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

    // Verify nbs and mpi counts match
    validateCounts();

    // Verify a known patients content
    String rawData = mpiTemplate.queryForObject(MPI_DATA_SELECT, String.class);

    // Preprocess the JSON payload to ensure the 'race' field is an array
    rawData = transformRaceField(rawData);

    MpiPerson mpiData = mapper.readValue(rawData, MpiPerson.class);

    validatePatientData(mpiData);

  }

  private String transformRaceField(String jsonData) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(jsonData);

    JsonNode raceNode = jsonNode.get("race");
    if (raceNode != null && raceNode.isTextual()) {
      // Convert the single string into an array
      ArrayNode raceArray = objectMapper.createArrayNode();
      raceArray.add(raceNode.asText());

      ((ObjectNode) jsonNode).set("race", raceArray);
    }

    return objectMapper.writeValueAsString(jsonNode);
  }

  private void validatePatientData(MpiPerson mpiData) {
    // Personal
    assertThat(mpiData.birth_date()).isEqualTo("1990-01-01");
    assertThat(mpiData.sex()).isEqualTo("M");
    assertThat(mpiData.race().getFirst()).isEqualTo("ASIAN");

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

    // Identifiers
    assertThat(mpiData.identifiers()).hasSize(1);
    assertThat(mpiData.identifiers().get(0).value()).isEqualTo("3453453533");
    assertThat(mpiData.identifiers().get(0).type()).isEqualTo("AN");
    assertThat(mpiData.identifiers().get(0).authority()).isEqualTo("GA");
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

}
