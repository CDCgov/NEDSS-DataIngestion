package gov.cdc.nbs.deduplication.merge.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@ExtendWith(MockitoExtension.class)
class PersonEthnicityMergeHandlerTest {

  @Mock
  private JdbcClient client;

  @InjectMocks
  private PersonEthnicityMergeHandler mergeHandler;

  @Test
  void should_not_act_survivor_equals_source() {
    // Mock
    PatientMergeRequest request = Mockito.mock(PatientMergeRequest.class);
    when(request.survivingRecord()).thenReturn("123");
    when(request.ethnicity()).thenReturn("123");

    // Act
    mergeHandler.handleMerge("1", request);

    // Verify
    verifyNoInteractions(client);
  }

  @Test
  void should_merge() {
    // Mock
    PatientMergeRequest request = Mockito.mock(PatientMergeRequest.class);
    when(request.survivingRecord()).thenReturn("123");
    when(request.ethnicity()).thenReturn("321");

    mockUpdatePerson("123", "321");
    // Surviving and source spanish origins
    mockGetSpanishOrigins(
        "123",
        List.of("2148-5", "2155-0"),
        "321",
        List.of("2148-5", "2184-0"));

    // Current user
    mockCurrentUser(99L);

    // Insert
    mockInsert("2184-0", "123", 99l, "321");

    // Set inactive
    mockSetInactive("2155-0", "123", 99L);

    // Act
    mergeHandler.handleMerge("1", request);

    // Verify
    // Person table should be updated (ethnic_group_ind and ethnic_unk_reason_cd)
    verify(client, times(1)).sql(PersonEthnicityMergeHandler.UPDATE_PERSON_ETHNIC_GROUP);

    // Should fetch spanish origin for surviving and source
    verify(client, times(2)).sql(PersonEthnicityMergeHandler.SELECT_SPANISH_ORIGIN_LIST);

    // Should insert 2184-0
    verify(client).sql(PersonEthnicityMergeHandler.INSERT_SPANISH_ORIGIN);

    // Should set inactive 2155-0
    verify(client).sql(PersonEthnicityMergeHandler.UPDATE_SPANISH_ORIGINS_TO_INACTIVE);
  }

  private void mockUpdatePerson(String survivor, String source) {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonEthnicityMergeHandler.UPDATE_PERSON_ETHNIC_GROUP)).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.SURVIVOR_ID, survivor)).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.SOURCE_ID, source)).thenReturn(statementSpec);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSpanishOrigins(
      String survivingId,
      List<String> survivingSpanishOrigins,
      String sourceId,
      List<String> sourceSpanishOrigins) {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonEthnicityMergeHandler.SELECT_SPANISH_ORIGIN_LIST)).thenReturn(statementSpec);

    StatementSpec survivingStatement = Mockito.mock(StatementSpec.class);
    when(statementSpec.param(PersonEthnicityMergeHandler.PERSON_ID, survivingId)).thenReturn(survivingStatement);

    StatementSpec sourceStatement = Mockito.mock(StatementSpec.class);
    when(statementSpec.param(PersonEthnicityMergeHandler.PERSON_ID, sourceId)).thenReturn(sourceStatement);

    MappedQuerySpec<String> survivingQuery = Mockito.mock(MappedQuerySpec.class);
    when(survivingStatement.query(String.class)).thenReturn(survivingQuery);
    when(survivingQuery.list()).thenReturn(survivingSpanishOrigins);

    MappedQuerySpec<String> sourceQuery = Mockito.mock(MappedQuerySpec.class);
    when(sourceStatement.query(String.class)).thenReturn(sourceQuery);
    when(sourceQuery.list()).thenReturn(sourceSpanishOrigins);
  }

  private void mockCurrentUser(long userId) {
    NbsUserDetails user = Mockito.mock(NbsUserDetails.class);
    when(user.getId()).thenReturn(userId);

    Authentication auth = Mockito.mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(user);

    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  private void mockInsert(String expectedEthnicity, String personId, long userId, String sourceId) {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonEthnicityMergeHandler.INSERT_SPANISH_ORIGIN)).thenReturn(statementSpec);
    when(statementSpec.param("spanishOrigin", expectedEthnicity)).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.PERSON_ID, personId)).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.USER_ID, userId)).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.SOURCE_ID, sourceId)).thenReturn(statementSpec);
  }

  private void mockSetInactive(String expectedEthnicity, String personId, long userId) {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonEthnicityMergeHandler.UPDATE_SPANISH_ORIGINS_TO_INACTIVE)).thenReturn(statementSpec);
    when(statementSpec.param("spanishOrigins", List.of(expectedEthnicity))).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.PERSON_ID, personId)).thenReturn(statementSpec);
    when(statementSpec.param(PersonEthnicityMergeHandler.USER_ID, userId)).thenReturn(statementSpec);
  }
}
