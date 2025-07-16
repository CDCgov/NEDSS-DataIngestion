package gov.cdc.nbs.deduplication.merge;


import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.merge.model.PatientFileMergeHistory;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MergeHistoryHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @InjectMocks
  private MergeHistoryHandler mergeHistoryHandler;

  @Test
  void testGetPatientMergeHistoryList() {
    long patientId = 123L;

    mockingForMergeHistory(String.valueOf(patientId));

    List<PatientFileMergeHistory> result = mergeHistoryHandler.getPatientMergeHistoryList(patientId);

    verifyMergeHistoryResult(result);
  }

  private void mockingForMergeHistory(String patientId) {
    Timestamp dateTime = Timestamp.valueOf(LocalDateTime.of(2024, 7, 15, 10, 30));
    Map<String, Object> mergeHistoryRow = Map.of(
        "supersededPersonId", patientId,
        "mergedBy", "Doe, John",
        "mergeTime", dateTime
    );

    when(jdbcTemplate.queryForList(eq(MergeHistoryHandler.QUERY), any(MapSqlParameterSource.class)))
        .thenReturn(List.of(mergeHistoryRow));

    when(patientRecordService.fetchPersonNameAndAddTime(patientId))
        .thenReturn(new PatientNameAndTime("LOC-789", "Smith, Jane", dateTime.toLocalDateTime()));
  }

  private void verifyMergeHistoryResult(List<PatientFileMergeHistory> result) {
    assertThat(result).hasSize(1);

    PatientFileMergeHistory history = result.getFirst();
    assertThat(history.supersededPersonLocalId()).isEqualTo("LOC-789");
    assertThat(history.supersededPersonLegalName()).isEqualTo("Smith, Jane");
    assertThat(history.mergedByUser()).isEqualTo("Doe, John");
  }


}
