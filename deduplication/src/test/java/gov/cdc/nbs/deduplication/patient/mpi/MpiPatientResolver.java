package gov.cdc.nbs.deduplication.patient.mpi;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

@Component
public class MpiPatientResolver {

  private final JdbcClient client;
  private final ObjectMapper mapper = new ObjectMapper();

  public MpiPatientResolver(@Qualifier("mpiJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  private static final String SELECT_PATIENT_QUERY = """
      SELECT TOP 1
        data
      FROM
        mpi_patient
      WHERE
        external_person_id = :id;
      """;

  public MpiPerson resolve(long patientId) {
    Optional<String> patientJson = client.sql(SELECT_PATIENT_QUERY)
        .param("id", patientId)
        .query(String.class)
        .optional();

    if (patientJson.isPresent()) {
      try {
        return mapper.readValue(patientJson.get(), MpiPerson.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to parse MPI patient data");
      }
    } else {
      return null;
    }
  }

}
