package gov.cdc.nbs.deduplication.merge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;

import gov.cdc.nbs.deduplication.SecurityTestUtil;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class MergeGroupServiceTest {

  @Mock
  private JdbcClient jdbcClient;

  @Mock
  private PatientRecordService patientRecordService;

  @InjectMocks
  private MergeGroupService mergeGroupService;

  @Test
  void testNoMerge() {
    // mock
    SecurityTestUtil.mockSecurityContext(220L);

    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MergeGroupService.SET_ENTRY_NO_MERGE)).thenReturn(spec);
    when(spec.param("userId", 220L)).thenReturn(spec);
    when(spec.param("mergeGroup", 1l)).thenReturn(spec);
    when(spec.param("personUid", 788L)).thenReturn(spec);

    // act
    mergeGroupService.markNoMerge(1l, 788L);

    // verify
    verify(jdbcClient, times(1)).sql(MergeGroupService.SET_ENTRY_NO_MERGE);
  }

  @Test
  void testMarkAllNoMerge() {
    // mock
    SecurityTestUtil.mockSecurityContext(220L);

    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MergeGroupService.SET_GROUP_TO_NO_MERGE)).thenReturn(spec);
    when(spec.param("userId", 220L)).thenReturn(spec);
    when(spec.param("mergeGroup", 1l)).thenReturn(spec);

    // act
    mergeGroupService.markAllNoMerge(1l);

    // verify
    verify(jdbcClient, times(1)).sql(MergeGroupService.SET_GROUP_TO_NO_MERGE);
  }

  @Test
  void testGetMergeGroup() {
    // mock
    List<String> personIds = new ArrayList<>();
    personIds.add("1");

    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MergeGroupService.SELECT_PERSON_UIDS_FROM_MERGE_GROUP)).thenReturn(spec);
    when(spec.param("mergeGroup", 1l)).thenReturn(spec);
    MappedQuerySpec<String> mqs = Mockito.mock(MappedQuerySpec.class);
    when(spec.query(String.class)).thenReturn(mqs);
    when(mqs.list()).thenReturn(personIds);

    List<PersonMergeData> pmd = new ArrayList<>();
    when(patientRecordService.fetchPersonsMergeData(personIds)).thenReturn(pmd);

    // act
    List<PersonMergeData> response = mergeGroupService.getMergeGroup(1l);

    // verify
    verify(patientRecordService, times(1)).fetchPersonsMergeData(personIds);
    assertThat(response).isEqualTo(pmd);
  }

}
