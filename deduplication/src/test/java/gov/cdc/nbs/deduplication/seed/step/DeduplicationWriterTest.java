package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
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

  @Mock
  PatientRecordService patientRecordService;

  @InjectMocks
  private DeduplicationWriter writer;

  @Test
  void initializes() {
    DeduplicationWriter newWriter = new DeduplicationWriter(template, patientRecordService);
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
    assertThat(captor.getValue()[0].getValue("status")).isEqualTo("U");

    assertThat(captor.getValue()[1].getValue("person_uid")).isEqualTo(3L);
    assertThat(captor.getValue()[1].getValue("person_parent_uid")).isEqualTo(4L);
    assertThat(captor.getValue()[1].getValue("mpi_patient")).isEqualTo("mpiPatient2");
    assertThat(captor.getValue()[1].getValue("mpi_person")).isEqualTo("mpiPerson2");
    assertThat(captor.getValue()[1].getValue("status")).isEqualTo("U");
  }

  @Test
  void createsParameterSource() {
    LocalDateTime now = LocalDateTime.now();
    DeduplicationEntry entry = new DeduplicationEntry(
        1l,
        2l,
        "mpiPatient1",
        "mpiPerson1");
    SqlParameterSource source = writer.createParameterSource(entry,now);

    assertThat(source.getValue("person_uid")).isEqualTo(entry.nbsPersonId());
    assertThat(source.getValue("person_parent_uid")).isEqualTo(entry.nbsPersonParentId());
    assertThat(source.getValue("mpi_patient")).isEqualTo(entry.mpiPatientId());
    assertThat(source.getValue("mpi_person")).isEqualTo(entry.mpiPersonId());
  }
}
