package gov.cdc.nbs.deduplication.merge.id;

import static org.assertj.core.api.Assertions.assertThat;
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

import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator.EntityType;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class LocalUidGeneratorTest {

  @Mock
  private JdbcClient client;

  @InjectMocks
  private LocalUidGenerator idGenerator;

  @Test
  void should_return_nbs_id() {
    // Mock

    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(LocalUidGenerator.QUERY_BY_NBS_TYPE_CD)).thenReturn(statementSpec);

    MappedQuerySpec<GeneratedId> querySpec = Mockito.mock(MappedQuerySpec.class);
    when(statementSpec.query(GeneratedId.class)).thenReturn(querySpec);
    when(querySpec.optional()).thenReturn(Optional.of(new GeneratedId(12L, "prefix", "suffix")));

    when(client.sql(LocalUidGenerator.INCREMENT_BY_NBS_TYPE_CD)).thenReturn(statementSpec);

    // Act
    GeneratedId id = idGenerator.getNextValidId(EntityType.NBS);

    // Verify
    assertThat(id.id()).isEqualTo(12L);
    assertThat(id.prefix()).isEqualTo("prefix");
    assertThat(id.suffix()).isEqualTo("suffix");
    assertThat(id.toLocalId()).isEqualTo("prefix12suffix");

    verify(client, times(1)).sql(LocalUidGenerator.QUERY_BY_NBS_TYPE_CD);
    verify(client, times(1)).sql(LocalUidGenerator.INCREMENT_BY_NBS_TYPE_CD);
    verify(client, times(0)).sql(LocalUidGenerator.QUERY_BY_ID);
    verify(client, times(0)).sql(LocalUidGenerator.INCREMENT_BY_ID);
  }

  @Test
  void should_return_non_nbs_id() {
    // Mock

    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    when(client.sql(LocalUidGenerator.QUERY_BY_ID)).thenReturn(statementSpec);
    when(statementSpec.param("classCd", "PERSON")).thenReturn(statementSpec);

    MappedQuerySpec<GeneratedId> querySpec = Mockito.mock(MappedQuerySpec.class);
    when(statementSpec.query(GeneratedId.class)).thenReturn(querySpec);
    when(querySpec.optional()).thenReturn(Optional.of(new GeneratedId(12L, "prefix", "suffix")));

    when(client.sql(LocalUidGenerator.INCREMENT_BY_ID)).thenReturn(statementSpec);

    // Act
    GeneratedId id = idGenerator.getNextValidId(EntityType.PERSON);

    // Verify
    assertThat(id.id()).isEqualTo(12L);
    assertThat(id.prefix()).isEqualTo("prefix");
    assertThat(id.suffix()).isEqualTo("suffix");

    verify(client, times(1)).sql(LocalUidGenerator.QUERY_BY_ID);
    verify(client, times(1)).sql(LocalUidGenerator.INCREMENT_BY_ID);
    verify(client, times(0)).sql(LocalUidGenerator.QUERY_BY_NBS_TYPE_CD);
    verify(client, times(0)).sql(LocalUidGenerator.INCREMENT_BY_NBS_TYPE_CD);
  }

}
