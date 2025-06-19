package gov.cdc.nbs.deduplication.sync.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PersonDeleteSyncHandler {

  private final RestClient recordLinkageClient;
  private final JdbcClient deduplicationClient;

  public PersonDeleteSyncHandler(
      @Qualifier("recordLinkerRestClient") RestClient recordLinkageClient,
      @Qualifier("deduplicationJdbcClient") JdbcClient deduplicationClient) {
    this.recordLinkageClient = recordLinkageClient;
    this.deduplicationClient = deduplicationClient;
  }

  private static final String LOOKUP_MPI_PATIENT = """
      SELECT
        mpi_patient
      FROM
        nbs_mpi_mapping
      WHERE
        person_uid = :id;
      """;

  private static final String DELETE_MPI_MAPPING = """
      DELETE FROM
        nbs_mpi_mapping
      WHERE
        person_uid = :id;
      """;

  // Handles the case when a patient was deleted in NBS 6. This deletion needs to
  // propagate to the nbs_mpi_mapping table as well as the MPI. Removing the
  // patient data will prevent new data from matching to a deleted patient
  public void handleDelete(JsonNode payloadNode) {
    JsonNode afterNode = payloadNode.path("after");
    String personUid = afterNode.get("person_uid").asText();

    Optional<String> mpiUuid = deduplicationClient.sql(LOOKUP_MPI_PATIENT)
        .param("id", personUid)
        .query(String.class)
        .optional();

    // the entry is in the nbs_mpi_mapping table. Remove it from the MPI and then
    // remove it from the lookup table
    if (mpiUuid.isPresent()) {
      recordLinkageClient.delete()
          .uri("/patient/" + mpiUuid.get())
          .retrieve()
          .toBodilessEntity();

      deduplicationClient.sql(DELETE_MPI_MAPPING)
          .param("id", personUid)
          .update();
    }
  }

}
