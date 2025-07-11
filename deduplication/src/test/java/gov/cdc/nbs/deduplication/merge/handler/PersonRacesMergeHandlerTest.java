package gov.cdc.nbs.deduplication.merge.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import gov.cdc.nbs.deduplication.merge.handler.PersonRacesMergeHandler.RaceEntry;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest.RaceId;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class PersonRacesMergeHandlerTest {

  @Mock
  private JdbcClient client;

  @InjectMocks
  private PersonRacesMergeHandler mergeHandler;

  @Test
  void should_merge_race() {
    // Mock
    mockCurrentUser(99L);
    PatientMergeRequest request = Mockito.mock(PatientMergeRequest.class);
    when(request.survivingRecord()).thenReturn("1");
    when(request.races()).thenReturn(List.of(
        new RaceId("1", "A"),
        new RaceId("2", "B")));

    mockSetInactive(99L, "1");
    mockSelectRaceEntries();
    mockEntryExists();
    mockInsert();
    mockUpdate();

    // Act
    mergeHandler.handleMerge("matchId", request);

    // Verify
    verify(client, times(1)).sql(PersonRacesMergeHandler.SET_RACE_ENTRIES_TO_INACTIVE);

    verify(client, times(1)).sql(PersonRacesMergeHandler.UPDATE_EXISTING_RACE_ENTRY);
    verify(client, times(1)).sql(PersonRacesMergeHandler.INSERT_NEW_RACE_ENTRY);
  }

  private void mockUpdate() {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonRacesMergeHandler.UPDATE_EXISTING_RACE_ENTRY)).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.PERSON_ID, "1")).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.RACE, "A")).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.DETAILED_RACE, "Z")).thenReturn(statementSpec);
  }

  private void mockInsert() {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonRacesMergeHandler.INSERT_NEW_RACE_ENTRY)).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.PERSON_ID, "1")).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.RACE, "B")).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.DETAILED_RACE, "X")).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.USER_ID, 99L)).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.SOURCE_ID, "2")).thenReturn(statementSpec);
  }

  private void mockEntryExists() {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonRacesMergeHandler.SELECT_RACE_ENTRY_EXISTS)).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.PERSON_ID, "1")).thenReturn(statementSpec);

    StatementSpec bxSpec = Mockito.mock(StatementSpec.class);
    when(statementSpec.param(PersonRacesMergeHandler.RACE, "B")).thenReturn(bxSpec);
    when(bxSpec.param(PersonRacesMergeHandler.DETAILED_RACE, "X")).thenReturn(bxSpec);
    MappedQuerySpec<Boolean> bxBooleanSpec = Mockito.mock(MappedQuerySpec.class);
    when(bxSpec.query(Boolean.class)).thenReturn(bxBooleanSpec);
    when(bxBooleanSpec.single()).thenReturn(false);

    StatementSpec azSpec = Mockito.mock(StatementSpec.class);
    when(statementSpec.param(PersonRacesMergeHandler.RACE, "A")).thenReturn(azSpec);
    when(azSpec.param(PersonRacesMergeHandler.DETAILED_RACE, "Z")).thenReturn(azSpec);
    MappedQuerySpec<Boolean> azBooleanSpec = Mockito.mock(MappedQuerySpec.class);
    when(azSpec.query(Boolean.class)).thenReturn(azBooleanSpec);
    when(azBooleanSpec.single()).thenReturn(true);
  }

  private void mockSelectRaceEntries() {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    StatementSpec person1Statement = Mockito.mock(StatementSpec.class);
    StatementSpec person2Statement = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonRacesMergeHandler.SELECT_RACE_ENTRIES)).thenReturn(statementSpec);

    // person 1
    when(statementSpec.param(PersonRacesMergeHandler.PERSON_ID, "1")).thenReturn(person1Statement);
    when(person1Statement.param(PersonRacesMergeHandler.RACE, "A")).thenReturn(person1Statement);

    MappedQuerySpec<RaceEntry> person1QuerySpec = Mockito.mock(MappedQuerySpec.class);
    when(person1Statement.query(RaceEntry.class)).thenReturn(person1QuerySpec);
    when(person1QuerySpec.list()).thenReturn(List.of(new RaceEntry("A", "Z")));

    // perosn 2
    when(statementSpec.param(PersonRacesMergeHandler.PERSON_ID, "2")).thenReturn(person2Statement);
    when(person2Statement.param(PersonRacesMergeHandler.RACE, "B")).thenReturn(person2Statement);

    MappedQuerySpec<RaceEntry> person2QuerySpec = Mockito.mock(MappedQuerySpec.class);
    when(person2Statement.query(RaceEntry.class)).thenReturn(person2QuerySpec);
    when(person2QuerySpec.list()).thenReturn(List.of(new RaceEntry("B", "X")));
  }

  private void mockSetInactive(long userId, String survivorId) {
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(PersonRacesMergeHandler.SET_RACE_ENTRIES_TO_INACTIVE)).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.USER_ID, userId)).thenReturn(statementSpec);
    when(statementSpec.param(PersonRacesMergeHandler.PERSON_ID, survivorId)).thenReturn(statementSpec);
  }

  private void mockCurrentUser(long userId) {
    NbsUserDetails user = Mockito.mock(NbsUserDetails.class);
    when(user.getId()).thenReturn(userId);

    Authentication auth = Mockito.mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(user);

    SecurityContextHolder.getContext().setAuthentication(auth);
  }

}
