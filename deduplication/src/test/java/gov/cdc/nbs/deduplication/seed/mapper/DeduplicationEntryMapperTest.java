package gov.cdc.nbs.deduplication.seed.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;

class DeduplicationEntryMapperTest {

  DeduplicationEntryMapper mapper = new DeduplicationEntryMapper();

  @Test
  void testParse() throws SQLException {
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    when(resultSet.getLong("person_uid")).thenReturn(1l);
    when(resultSet.getLong("person_parent_uid")).thenReturn(2l);
    when(resultSet.getString("mpi_patient_uuid")).thenReturn("patientUuid");
    when(resultSet.getString("mpi_person_uuid")).thenReturn("personUuid");

    DeduplicationEntry entry = mapper.mapRow(resultSet, 0);

    assertThat(entry).isNotNull();
    assertThat(entry.nbsPersonId()).isEqualTo(1l);
    assertThat(entry.nbsPersonParentId()).isEqualTo(2l);
    assertThat(entry.mpiPatientId()).isEqualTo("patientUuid");
    assertThat(entry.mpiPersonId()).isEqualTo("personUuid");
  }

}
