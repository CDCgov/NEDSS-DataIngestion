package gov.cdc.nbs.deduplication.duplicates.service;



import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.model.PossibleMatchGroup;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MergeGroupHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @InjectMocks
  private MergeGroupHandler mergeGroupHandler;


  @Test
  void testGetMergeGroups() throws SQLException {
    // Arrange
    List<String> personUids = Arrays.asList("personUid1", "personUid2");
    List<MpiPerson> patientRecords = getPatientRecords();

    //Mocking
    ResultSet rsPossibleMatchGroup = mockResultSetForPossibleMatchGroup();
    ResultSet rsPersonUids = mockResultSetForPersonUids();
    mockQueryForPossibleMatchGroups(rsPossibleMatchGroup);
    mockQueryForPersonIdsByMpiIds(rsPersonUids);
    when(patientRecordService.fetchPersonRecords(personUids)).thenReturn(patientRecords);

    // Act
    List<MergeGroupResponse> result = mergeGroupHandler.getMergeGroups(0, 10);

    // Verify
    verifyMergeGroupResponse(result);
  }

  @Test
  void testUpdateMergeStatus() {
    MergeStatusRequest request = new MergeStatusRequest(100L, false);

    mergeGroupHandler.updateMergeStatus(request);

    verify(deduplicationTemplate, times(1)).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP),
        any(MapSqlParameterSource.class)
    );
  }

  private List<MpiPerson> getPatientRecords() {
    return Arrays.asList(
        new MpiPerson("personUid1", null, null, null, null,
            getMockedName(), null, null, null),
        new MpiPerson("personUid1", null, null, null, null,
            getMockedName(), null, null, null)
    );
  }

  private List<MpiPerson.Name> getMockedName() {
    return List.of(new MpiPerson.Name(
        List.of("John", "Jane"),    // Given names
        "Doe",                      // Family name
        null
    ));
  }

  private ResultSet mockResultSetForPossibleMatchGroup() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("person_uid")).thenReturn("personUid");
    when(rs.getString("mpi_person_ids")).thenReturn("mpiId1, mpiId2");
    when(rs.getString("date_identified")).thenReturn("2023-10-01");
    return rs;
  }

  private ResultSet mockResultSetForPersonUids() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("person_uid")).thenReturn("personUid1", "personUid2");
    return rs;
  }

  private void mockQueryForPossibleMatchGroups(ResultSet rs) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.POSSIBLE_MATCH_GROUP),
        argThat((MapSqlParameterSource parameters) -> {
          Integer limit = (Integer) parameters.getValue("limit");
          Integer offset = (Integer) parameters.getValue("offset");
          return limit != null && offset != null;
        }),
        ArgumentMatchers.<RowMapper<PossibleMatchGroup>>any()))
        .thenAnswer(invocation -> {
          RowMapper<PossibleMatchGroup> rowMapper = invocation.getArgument(2);
          return Collections.singletonList(rowMapper.mapRow(rs, 1));
        });
  }

  @SuppressWarnings("unchecked")
  private void mockQueryForPersonIdsByMpiIds(ResultSet rs) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS),
        argThat((MapSqlParameterSource parameters) -> {
          List<String> ids = (List<String>) parameters.getValue("mpiIds");
          return ids != null;
        }),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenAnswer(invocation -> {
          RowMapper<String> rowMapper = invocation.getArgument(2);
          List<String> result = new ArrayList<>();
          result.add(rowMapper.mapRow(rs, 1));
          result.add(rowMapper.mapRow(rs, 2));
          return result;
        });
  }

  private void verifyMergeGroupResponse(List<MergeGroupResponse> result) {
    assertNotNull(result);
    assertEquals(1, result.size());
    MergeGroupResponse response = result.getFirst();
    assertEquals("2023-10-01", response.dateIdentified());
    assertEquals("John Doe", response.mostRecentPersonName());
    assertEquals(2, response.patients().size());
  }


}
