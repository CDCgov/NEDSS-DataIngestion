package gov.cdc.nbs.deduplication.merge;

import gov.cdc.nbs.deduplication.SecurityTestUtil;
import gov.cdc.nbs.deduplication.merge.handler.AdminCommentMergeHandler;
import gov.cdc.nbs.deduplication.merge.handler.PersonTableMergeHandler;
import gov.cdc.nbs.deduplication.merge.handler.SectionMergeHandler;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MergeServiceTest {

  @Mock
  private PersonTableMergeHandler personTableMergeHandler;

  @Mock
  private AdminCommentMergeHandler adminCommentMergeHandler;

  @Mock
  private JdbcClient deduplicationClient;

  @Mock
  private ObjectMapper objectMapper;

  private MergeService mergeService;

  @BeforeEach
  void setUp() {
    List<SectionMergeHandler> handlers = Arrays.asList(personTableMergeHandler, adminCommentMergeHandler);
    StatementSpec mockStatementSpec = Mockito.mock(StatementSpec.class);

    when(deduplicationClient.sql(MergeService.MARK_PATIENTS_AS_MERGED)).thenReturn(mockStatementSpec);
    when(mockStatementSpec.param("mergeGroup", 123L)).thenReturn(mockStatementSpec);
    when(mockStatementSpec.param("userId", 220L)).thenReturn(mockStatementSpec);
    when(mockStatementSpec.update()).thenReturn(1); // simulate update success

    StatementSpec auditStatementSpec = Mockito.mock(StatementSpec.class);
    when(deduplicationClient.sql(MergeService.SAVE_PATIENT_MERGE_AUDIT)).thenReturn(auditStatementSpec);
    when(auditStatementSpec.param(anyString(), any())).thenReturn(auditStatementSpec);
    when(auditStatementSpec.update()).thenReturn(1); // simulate audit insert success

    mergeService = new MergeService(handlers, deduplicationClient, objectMapper);
  }

  @Test
  void testPerformMerge_CallsAllHandlersInOrder_AndSavesAudit() throws Exception {
    // Arrange
    long matchId = 123L;
    String matchIdStr = "123";

    PatientMergeRequest request = mock(PatientMergeRequest.class);
    when(request.survivingRecord()).thenReturn("999");

    String relatedAuditsJson = "[{\"table\":\"person\",\"action\":\"update\"}]";
    String mergeRequestJson = "{\"survivingRecord\":\"999\"}";

    // Mock objectMapper serialization
    when(objectMapper.writeValueAsString(any())).thenReturn(relatedAuditsJson, mergeRequestJson);

    SecurityTestUtil.mockSecurityContext(220L);

    // Act
    mergeService.performMerge(matchId, request);

    // Assert: Verify handlers are called in order
    InOrder inOrder = inOrder(personTableMergeHandler, adminCommentMergeHandler, deduplicationClient);

    inOrder.verify(personTableMergeHandler).handleMerge(eq(matchIdStr), eq(request), any(PatientMergeAudit.class));
    inOrder.verify(adminCommentMergeHandler).handleMerge(eq(matchIdStr), eq(request), any(PatientMergeAudit.class));

    // Assert: Verify markPatientsMerged is called
    inOrder.verify(deduplicationClient).sql(MergeService.MARK_PATIENTS_AS_MERGED);

    // Assert: Verify audit is saved
    inOrder.verify(deduplicationClient).sql(MergeService.SAVE_PATIENT_MERGE_AUDIT);

    // Assert: Verify ObjectMapper was used to serialize
    verify(objectMapper, times(2)).writeValueAsString(any());
  }
}
