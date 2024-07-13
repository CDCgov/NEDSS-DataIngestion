package gov.cdc.dataprocessing.model.dto.nbs;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NbsNoteDtoTest {

    @Test
    public void testConstructor() {
        // Create an instance of NbsNoteDto using constructor
        NbsNoteDto dto = new NbsNoteDto();

        // Assert default values
        assertNull(dto.getNbsNoteUid());
        assertNull(dto.getNoteParentUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getAddUserId());
        assertNull(dto.getRecordStatusCode());
        assertNull(dto.getRecordStatusTime());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLastChgUserId());
        assertNull(dto.getNote());
        assertNull(dto.getPrivateIndCd());
        assertNull(dto.getTypeCd());
        assertNull(dto.getLastChgUserNm());
    }

}
