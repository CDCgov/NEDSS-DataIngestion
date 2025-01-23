package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

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
    // Prepare test data with status "U"
    List<DeduplicationEntry> entries = new ArrayList<>();
    entries.add(new DeduplicationEntry(1L, 2L, "mpiPatient1", "mpiPerson1", "U"));
    entries.add(new DeduplicationEntry(3L, 4L, "mpiPatient2", "mpiPerson2", "U"));
    var chunk = new Chunk<>(entries);

    // Mock behavior for the query to simulate no existing records
    when(template.queryForObject(Mockito.anyString(), Mockito.any(SqlParameterSource.class), eq(Integer.class)))
            .thenReturn(0); // No existing records found for person_uid

    // Call the method under test
    writer.write(chunk);

    verify(template, times(1)).batchUpdate(Mockito.anyString(), Mockito.any(SqlParameterSource[].class));

    // capture the actual parameters passed to the batch update
    ArgumentCaptor<SqlParameterSource[]> captor = ArgumentCaptor.forClass(SqlParameterSource[].class);
    verify(template).batchUpdate(Mockito.anyString(), captor.capture());

    // Assert that two entries were passed to batch update
    assertThat(captor.getValue()).hasSize(2);

    // Check the parameters for the first entry
    assertThat(captor.getValue()[0].getValue("person_uid")).isEqualTo(1L);
    assertThat(captor.getValue()[0].getValue("person_parent_uid")).isEqualTo(2L);
    assertThat(captor.getValue()[0].getValue("mpi_patient")).isEqualTo("mpiPatient1");
    assertThat(captor.getValue()[0].getValue("mpi_person")).isEqualTo("mpiPerson1");
    assertThat(captor.getValue()[0].getValue("status")).isEqualTo("U");

    // Check the parameters for the second entry
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
            "mpiPerson1", "U");
    String status = "U";
    SqlParameterSource source = writer.createParameterSource(entry, status);

    assertThat(source.getValue("person_uid")).isEqualTo(entry.nbsPersonId());
    assertThat(source.getValue("person_parent_uid")).isEqualTo(entry.nbsPersonParentId());
    assertThat(source.getValue("mpi_patient")).isEqualTo(entry.mpiPatientId());
    assertThat(source.getValue("mpi_person")).isEqualTo(entry.mpiPersonId());
    assertThat(source.getValue("status")).isEqualTo(entry.status());
  }

  // Test for updating existing records with status 'S'
  @Test
  void shouldUpdateExistingRecordsWithStatusS() throws Exception {
    List<DeduplicationEntry> entries = new ArrayList<>();
    entries.add(new DeduplicationEntry(1L, 2L, "mpiPatient1", "mpiPerson1", "U"));
    var chunk = new Chunk<>(entries);

    // Mock queryForObject to simulate the existence of the record ( returns 1)
    Mockito.when(template.queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Integer.class)))
            .thenReturn(1);

    writer.write(chunk);

    // Capture the arguments passed to batchUpdate
    ArgumentCaptor<SqlParameterSource[]> captor = ArgumentCaptor.forClass(SqlParameterSource[].class);
    verify(template).batchUpdate(Mockito.eq(DeduplicationWriter.UPDATE_QUERY), captor.capture());

    // Capture the batch parameters and verify its size
    SqlParameterSource[] capturedParams = captor.getValue();
    assertThat(capturedParams).hasSize(1);

    // Verify the 'status' is set to 'S' for the existing record
    assertThat(capturedParams[0].getValue("status")).isEqualTo("S");

    // Verify all other fields match the input entry
    assertThat(capturedParams[0].getValue("person_uid")).isEqualTo(1L);
    assertThat(capturedParams[0].getValue("person_parent_uid")).isEqualTo(2L);
    assertThat(capturedParams[0].getValue("mpi_patient")).isEqualTo("mpiPatient1");
    assertThat(capturedParams[0].getValue("mpi_person")).isEqualTo("mpiPerson1");
  }

  // Test for mixed scenarios (both new and existing records)
  @Test
  void shouldHandleMixedRecords() throws Exception {
    DeduplicationEntry newEntry = new DeduplicationEntry(1L, 2L, "mpiPatient1", "mpiPerson1", "U");
    DeduplicationEntry existingEntry = new DeduplicationEntry(3L, 4L, "mpiPatient2", "mpiPerson2", "U");

    List<DeduplicationEntry> entries = List.of(newEntry, existingEntry);
    var chunk = new Chunk<>(entries);

    // Simulate new and existing records
    Mockito.when(template.queryForObject(Mockito.anyString(), Mockito.any(SqlParameterSource.class), Mockito.eq(Integer.class)))
            .thenReturn(0, 1);  // Simulate no record for new entry, and one for the existing entry

    writer.write(chunk);

    // Verify batch update is called for inserts and updates
    ArgumentCaptor<SqlParameterSource[]> captor = ArgumentCaptor.forClass(SqlParameterSource[].class);
    verify(template, times(2)).batchUpdate(Mockito.anyString(), captor.capture()); // Expect two calls

    // Verify the first batch update (insert for new record)
    SqlParameterSource[] capturedParams = captor.getAllValues().get(0); // First capture for insert
    assertThat(capturedParams[0].getValue("status")).isEqualTo("U"); // New record should have status 'U'

    // Verify the second batch update (update for existing record)
    SqlParameterSource[] capturedUpdateParams = captor.getAllValues().get(1); // Second capture for update
    assertThat(capturedUpdateParams[0].getValue("status")).isEqualTo("S"); // Existing record should have status 'S'
  }

  @Test
  public void testWriteUpdatesWatermarkCorrectly() throws Exception {
    // Create a chunk of test entries
    DeduplicationEntry entry1 = new DeduplicationEntry(1L, 10L, "100", "200", "S"); // Example data
    DeduplicationEntry entry2 = new DeduplicationEntry(2L, 20L, "101", "201L", "F"); // Example data

    Chunk<DeduplicationEntry> chunk = new Chunk<>();
    chunk.add(entry1);
    chunk.add(entry2);

    // Act
    writer.write(chunk);

    // verify that the update method is called with correct last_processed_id
    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
    verify(template).update(
            eq(DeduplicationWriter.UPDATE_WATERMARK_QUERY),
            captor.capture() // Capture the argument passed to the update method
    );

    // verify that the captured value for "last_processed_id" is correct
    assertThat(captor.getValue().getValue("last_processed_id")).isEqualTo(2L); // The max nbsPersonId from the chunk
  }

  @Test
  void updatesHighWaterMark() throws Exception {
    // Arrange: Set up a chunk with multiple entries
    List<DeduplicationEntry> entries = List.of(
            new DeduplicationEntry(5L, 3L, "mpiPatient1", "mpiPerson1", "S"),
            new DeduplicationEntry(2L, 1L, "mpiPatient2", "mpiPerson2", "S")
    );

    // Stub the isExistingRecord() calls to always return false (no existing records)
    when(template.queryForObject(
            eq("SELECT COUNT(*) FROM nbs_mpi_mapping WHERE person_uid = :person_uid"),
            any(MapSqlParameterSource.class),
            eq(Integer.class)
    )).thenReturn(0);

    // Act
    writer.write(new Chunk<>(entries));

    // Verify the high-water mark update
    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
    verify(template, times(1)).update(eq(DeduplicationWriter.UPDATE_WATERMARK_QUERY), captor.capture());

    // validate the parameter passed to the high-water mark update
    MapSqlParameterSource params = captor.getValue();
    assertThat(params.getValue("last_processed_id")).isEqualTo(5L); // Max ID from the chunk
  }

  @Test
  void doesNotUpdateHighWaterMarkForEmptyChunk() throws Exception {
    // Act with an empty chunk
    var chunk = new Chunk<DeduplicationEntry>(List.of());

    // Act
    writer.write(chunk);

    // Ensure high-water mark update is not executed
    verify(template, never()).update(eq("UPDATE deduplication_watermark SET last_processed_id = :last_processed_id, updated_at = CURRENT_TIMESTAMP WHERE id = 1;"), any(MapSqlParameterSource.class));
  }

}
