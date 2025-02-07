package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

@ExtendWith(MockitoExtension.class)
class DeduplicationWriterTest {

  @Mock
  private NamedParameterJdbcTemplate template;

  @InjectMocks
  private DeduplicationWriter writer;

  @Test
  void initializes() {
    DeduplicationWriter newWriter = new DeduplicationWriter(template);
    assertThat(newWriter).isNotNull();
  }

  @Test
  void writesChunk() throws Exception {
    List<DeduplicationEntry> entries = new ArrayList<>();
    entries.add(new DeduplicationEntry(1L, 2L, "mpiPatient1", "mpiPerson1"));
    entries.add(new DeduplicationEntry(3L, 4L, "mpiPatient2", "mpiPerson2"));
    var chunk = new Chunk<DeduplicationEntry>(entries);

    writer.write(chunk);
    verify(template, times(1)).batchUpdate(Mockito.anyString(), Mockito.any(SqlParameterSource[].class));

    ArgumentCaptor<SqlParameterSource[]> captor = ArgumentCaptor.forClass(SqlParameterSource[].class);
    verify(template).batchUpdate(Mockito.anyString(), captor.capture());

    assertThat(captor.getValue()).hasSize(2);

    assertThat(captor.getValue()[0].getValue("person_uid")).isEqualTo(1L);
    assertThat(captor.getValue()[0].getValue("person_parent_uid")).isEqualTo(2L);
    assertThat(captor.getValue()[0].getValue("mpi_patient")).isEqualTo("mpiPatient1");
    assertThat(captor.getValue()[0].getValue("mpi_person")).isEqualTo("mpiPerson1");
    assertThat(captor.getValue()[0].getValue("status")).isEqualTo("P");

    assertThat(captor.getValue()[1].getValue("person_uid")).isEqualTo(3L);
    assertThat(captor.getValue()[1].getValue("person_parent_uid")).isEqualTo(4L);
    assertThat(captor.getValue()[1].getValue("mpi_patient")).isEqualTo("mpiPatient2");
    assertThat(captor.getValue()[1].getValue("mpi_person")).isEqualTo("mpiPerson2");
    assertThat(captor.getValue()[1].getValue("status")).isEqualTo("P");
  }

  @Test
  void createsParameterSource() {
    DeduplicationEntry entry = new DeduplicationEntry(
        1l,
        2l,
        "mpiPatient1",
        "mpiPerson1");
    SqlParameterSource source = writer.createParameterSource(entry);

    assertThat(source.getValue("person_uid")).isEqualTo(entry.nbsPersonId());
    assertThat(source.getValue("person_parent_uid")).isEqualTo(entry.nbsPersonParentId());
    assertThat(source.getValue("mpi_patient")).isEqualTo(entry.mpiPatientId());
    assertThat(source.getValue("mpi_person")).isEqualTo(entry.mpiPersonId());
  }

  @Test
  void testUpdateLastProcessedId() {
    DeduplicationWriter testWriter = new DeduplicationWriter(template);
    testWriter.updateLastProcessedId(5L);

    verify(template).update(eq(DeduplicationWriter.UPDATE_LAST_PROCESSED_ID), any(SqlParameterSource.class));
  }

  @Test
  void testUpdateLastProcessedId_whenUpdateFails_logsError() {
    doThrow(new RuntimeException("Database error")).when(template)
            .update(eq(DeduplicationWriter.UPDATE_LAST_PROCESSED_ID), any(SqlParameterSource.class));

    writer.updateLastProcessedId(5L);

    // Verify no exception is thrown & error is logged
    verify(template, times(1)).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  void testWrite_whenNoValidLargestProcessedId_doesNotUpdateLastProcessedId() throws Exception {
    List<DeduplicationEntry> entries = new ArrayList<>();
    entries.add(new DeduplicationEntry(null, null, null, null)); // Invalid entry
    var chunk = new Chunk<>(entries);

    writer.write(chunk);

    verify(template, times(1)).batchUpdate(anyString(), any(SqlParameterSource[].class));
    verify(template, never()).update(eq(DeduplicationWriter.UPDATE_LAST_PROCESSED_ID), any(SqlParameterSource.class));
  }

}
