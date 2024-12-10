package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

@ExtendWith(MockitoExtension.class)
class DeduplicationWriterTest {

  @Mock
  private JdbcTemplate template;

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
    entries.add(new DeduplicationEntry(
        1l,
        2l,
        "mpiPatient1",
        "mpiPerson1"));
    entries.add(new DeduplicationEntry(
        3l,
        4l,
        "mpiPatient2",
        "mpiPerson2"));
    var chunk = new Chunk<DeduplicationEntry>(entries);

    writer.write(chunk);

    verify(template, times(2)).update(Mockito.any(PreparedStatementCreator.class));
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
}
