package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import gov.cdc.nbs.deduplication.seed.logger.LoggingService;
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

  @Mock
  private LoggingService loggingService;

  @Test
  void initializes() {
    DeduplicationWriter newWriter = new DeduplicationWriter(template, loggingService);
    assertThat(newWriter).isNotNull();
  }

  @Test
  void writesChunk() throws Exception {
    var chunk = createChunk();

    writer.write(chunk);
    verify(template, times(1)).batchUpdate(Mockito.anyString(), Mockito.any(SqlParameterSource[].class));

    ArgumentCaptor<SqlParameterSource[]> captor = ArgumentCaptor.forClass(SqlParameterSource[].class);
    verify(template).batchUpdate(Mockito.anyString(), captor.capture());

    assertThat(captor.getValue()).hasSize(2);

    assertThat(captor.getValue()[0].getValue("person_uid")).isEqualTo(1L);
    assertThat(captor.getValue()[0].getValue("person_parent_uid")).isEqualTo(2L);
    assertThat(captor.getValue()[0].getValue("mpi_patient")).isEqualTo("mpiPatient1");
    assertThat(captor.getValue()[0].getValue("mpi_person")).isEqualTo("mpiPerson1");
    assertThat(captor.getValue()[0].getValue("status")).isEqualTo("U");

    assertThat(captor.getValue()[1].getValue("person_uid")).isEqualTo(3L);
    assertThat(captor.getValue()[1].getValue("person_parent_uid")).isEqualTo(4L);
    assertThat(captor.getValue()[1].getValue("mpi_patient")).isEqualTo("mpiPatient2");
    assertThat(captor.getValue()[1].getValue("mpi_person")).isEqualTo("mpiPerson2");
    assertThat(captor.getValue()[1].getValue("status")).isEqualTo("U");
  }

  @Test
  void createsParameterSource() {
    DeduplicationEntry entry = new DeduplicationEntry(
        1L,
        2L,
        "mpiPatient1",
        "mpiPerson1");
    SqlParameterSource source = writer.createParameterSource(entry);

    assertThat(source.getValue("person_uid")).isEqualTo(entry.nbsPersonId());
    assertThat(source.getValue("person_parent_uid")).isEqualTo(entry.nbsPersonParentId());
    assertThat(source.getValue("mpi_patient")).isEqualTo(entry.mpiPatientId());
    assertThat(source.getValue("mpi_person")).isEqualTo(entry.mpiPersonId());
  }

  @Test
  void writesChunkThrowsException() {
    var chunk = createChunk();

    doThrow(new RuntimeException("Database error")).when(template)
        .batchUpdate(Mockito.anyString(), Mockito.any(SqlParameterSource[].class));

    Exception exception = assertThrows(RuntimeException.class, () ->
        writer.write(chunk));


    verify(loggingService).logError(eq("DeduplicationWriter"), eq("Error writing nbs_mpi mapping to the database."),
        any(RuntimeException.class));
    assertThat(exception.getMessage()).contains("Database error");
  }

  private Chunk<DeduplicationEntry> createChunk() {
    List<DeduplicationEntry> entries = new ArrayList<>();
    entries.add(new DeduplicationEntry(1L, 2L, "mpiPatient1", "mpiPerson1"));
    entries.add(new DeduplicationEntry(3L, 4L, "mpiPatient2", "mpiPerson2"));
    return new Chunk<>(entries);
  }

}
