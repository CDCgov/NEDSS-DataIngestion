package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.constant.query.NbsNoteQuery.MERGE_NBS_NOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NbsNoteJdbcRepositoryTest {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private NbsNoteJdbcRepository repository;

    @BeforeEach
    void setup() {
        jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        repository = new NbsNoteJdbcRepository(jdbcTemplate);
    }

    @Test
    void testMergeNbsNote_shouldCallJdbcTemplateWithCorrectParams() {
        // Given
        NbsNote note = new NbsNote();
        note.setNbsNoteUid(1L);
        note.setNoteParentUid(100L);
        note.setRecordStatusCd("ACTIVE");
        note.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        note.setAddTime(new Timestamp(System.currentTimeMillis()));
        note.setAddUserId(42L);
        note.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        note.setLastChgUserId(24L);
        note.setNote("This is a note");
        note.setPrivateIndCd("N");
        note.setTypeCd("CASE");

        // When
        repository.mergeNbsNote(note);

        // Then
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).update(eq(MERGE_NBS_NOTE), captor.capture());

        MapSqlParameterSource params = captor.getValue();
        assertEquals(1L, params.getValue("nbs_note_uid"));
        assertEquals(100L, params.getValue("note_parent_uid"));
        assertEquals("ACTIVE", params.getValue("record_status_cd"));
        assertEquals("This is a note", params.getValue("note"));
        assertEquals("N", params.getValue("private_ind_cd"));
        assertEquals("CASE", params.getValue("type_cd"));
    }
}
