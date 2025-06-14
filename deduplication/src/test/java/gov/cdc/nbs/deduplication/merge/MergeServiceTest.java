package gov.cdc.nbs.deduplication.merge;

import gov.cdc.nbs.deduplication.merge.handler.AdminCommentMergeHandler;
import gov.cdc.nbs.deduplication.merge.handler.PersonTableMergeHandler;
import gov.cdc.nbs.deduplication.merge.handler.SectionMergeHandler;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MergeServiceTest {

  @Mock
  private PersonTableMergeHandler personTableMergeHandler;

  @Mock
  private AdminCommentMergeHandler adminCommentMergeHandler;

  private MergeService mergeService;


  @BeforeEach
  void setUp() {
    List<SectionMergeHandler> handlers = Arrays.asList(personTableMergeHandler, adminCommentMergeHandler);
    mergeService = new MergeService(handlers);
  }

  @Test
  void testPerformMerge_CallsAllHandlersInOrder() {
    long matchId = 123L;
    String matchIdStr = "123";
    PatientMergeRequest patientMergeRequest = mock(PatientMergeRequest.class);
    mergeService.performMerge(matchId, patientMergeRequest);

    InOrder inOrder = inOrder(personTableMergeHandler, adminCommentMergeHandler);
    inOrder.verify(personTableMergeHandler).handleMerge(matchIdStr, patientMergeRequest);
    inOrder.verify(adminCommentMergeHandler).handleMerge(matchIdStr, patientMergeRequest);
  }

}
