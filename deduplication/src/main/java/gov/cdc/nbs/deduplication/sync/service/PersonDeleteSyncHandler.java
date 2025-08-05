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

  static final String LOOKUP_MPI_PATIENT = """
      SELECT
        mpi_patient
      FROM
        nbs_mpi_mapping
      WHERE
        person_uid = :id;
      """;

  static final String DELETE_MPI_MAPPING = """
      DELETE FROM
        nbs_mpi_mapping
      WHERE
        person_uid = :id;
      """;

  static final String REMOVE_FROM_POTENTIAL_MERGES = """
      UPDATE merge_group_entries
      SET is_merge = 0
      WHERE person_uid = :id
      AND is_merge IS NULL;
      """;

  // clears any potential merge listings where there are only 1 entry
  static final String CLEAN_UP_POTENTIAL_MERGES = """
      UPDATE merge_group_entries
      SET
        is_merge = 0
      WHERE
        merge_group IN (
          SELECT
            merge_group
          FROM
            (
              SELECT
                merge_group,
                count(*) as null_count
              FROM
                merge_group_entries
              WHERE
                is_merge IS NULL
              GROUP BY
                merge_group
            ) AS counts
          WHERE
            null_count = 1
        );
          """;

  // Handles the case when a patient was deleted in NBS 6. This deletion needs to
  // propagate to the nbs_mpi_mapping table as well as the MPI. Removing the
  // patient data will prevent new data from matching to a deleted patient.
  // We also need to check if this patient was involved in a potential merge. If
  // so remove it
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

    // ensure the person is removed from any potential merges
    deduplicationClient.sql(REMOVE_FROM_POTENTIAL_MERGES)
        .param("id", personUid)
        .update();

    // do a clean up of potential merges so no single entries are left
    deduplicationClient.sql(CLEAN_UP_POTENTIAL_MERGES)
        .update();
  }

}
