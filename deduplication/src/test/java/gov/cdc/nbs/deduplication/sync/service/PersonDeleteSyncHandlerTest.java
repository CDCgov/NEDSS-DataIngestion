package gov.cdc.nbs.deduplication.sync.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ExtendWith(MockitoExtension.class)
class PersonDeleteSyncHandlerTest {

  @Mock
  private RestClient recordLinkageClient;

  @Mock
  private JdbcClient deduplicationClient;

  @InjectMocks
  private PersonDeleteSyncHandler deleteHandler;

  @Test
  void should_handle_delete() {
    // Mock
    mockMpiLookup("1234", "ABCD-EFGH");
    mockRecordLinkerDelete("ABCD-EFGH");
    mockDeleteMapping("1234");
    mockCleanPotentialMerges("1234");

    // Act
    String personUid = "1234";
    JsonNode payloadNode = createPayloadNode(personUid, personUid);
    deleteHandler.handleDelete(payloadNode);

    // Verify
    // Should check if patient exists in MPI (Yes in this test)
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.LOOKUP_MPI_PATIENT);

    // Should send delete request to MPI
    verify(recordLinkageClient, times(1)).delete();

    // Should delete the nbs_mpi_mapping entry
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.DELETE_MPI_MAPPING);

    // Should remove patient from any potential merges
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.REMOVE_FROM_POTENTIAL_MERGES);

    // Should clean up the potential merges to clear any left with a single entry
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.CLEAN_UP_POTENTIAL_MERGES);
  }

  @Test
  void should_not_handle_delete() {
    // Mock
    mockMpiLookup("1234", null);

    mockCleanPotentialMerges("1234");

    // Act
    String personUid = "1234";
    JsonNode payloadNode = createPayloadNode(personUid, personUid);
    deleteHandler.handleDelete(payloadNode);

    // Verify
    // Should check if patient exists in MPI (No in this test)
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.LOOKUP_MPI_PATIENT);

    // Should not send delete request to MPI
    verify(recordLinkageClient, never()).delete();

    // Should not delete the nbs_mpi_mapping entry
    verify(deduplicationClient, never()).sql(PersonDeleteSyncHandler.DELETE_MPI_MAPPING);

    // Should remove patient from any potential merges
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.REMOVE_FROM_POTENTIAL_MERGES);

    // Should clean up the potential merges to clear any left with a single entry
    verify(deduplicationClient, times(1)).sql(PersonDeleteSyncHandler.CLEAN_UP_POTENTIAL_MERGES);
  }

  private void mockMpiLookup(String id, String response) {
    Optional<String> lookup = response == null ? Optional.empty() : Optional.of(response);
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(deduplicationClient.sql(PersonDeleteSyncHandler.LOOKUP_MPI_PATIENT)).thenReturn(spec);
    when(spec.param("id", id)).thenReturn(spec);

    MappedQuerySpec<String> mapSpec = Mockito.mock(MappedQuerySpec.class);
    when(spec.query(String.class)).thenReturn(mapSpec);
    when(mapSpec.optional()).thenReturn(lookup);
  }

  private void mockRecordLinkerDelete(String uuid) {
    RequestHeadersUriSpec spec = Mockito.mock(RequestHeadersUriSpec.class);
    ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);

    when(recordLinkageClient.delete()).thenReturn(spec);
    when(spec.uri("/patient/" + uuid)).thenReturn(spec);
    when(spec.retrieve()).thenReturn(responseSpec);
  }

  private void mockDeleteMapping(String id) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(deduplicationClient.sql(PersonDeleteSyncHandler.DELETE_MPI_MAPPING)).thenReturn(spec);
    when(spec.param("id", id)).thenReturn(spec);
  }

  private void mockCleanPotentialMerges(String id) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(deduplicationClient.sql(PersonDeleteSyncHandler.REMOVE_FROM_POTENTIAL_MERGES)).thenReturn(spec);
    when(spec.param("id", id)).thenReturn(spec);

    StatementSpec spec2 = Mockito.mock(StatementSpec.class);
    when(deduplicationClient.sql(PersonDeleteSyncHandler.CLEAN_UP_POTENTIAL_MERGES)).thenReturn(spec2);
  }

  private JsonNode createPayloadNode(String personUid, String personParentUid) {
    JsonNodeFactory factory = JsonNodeFactory.instance;
    return factory.objectNode()
        .set("after", factory.objectNode()
            .put("person_uid", personUid)
            .put("person_parent_uid", personParentUid));
  }
}
