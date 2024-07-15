package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NbsActEntityHistTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NbsActEntityHist nbsActEntityHist = new NbsActEntityHist();

        // Assert
        assertNull(nbsActEntityHist.getNbsActEntityUid());
        assertNull(nbsActEntityHist.getActUid());
        assertNull(nbsActEntityHist.getAddTime());
        assertNull(nbsActEntityHist.getAddUserId());
        assertNull(nbsActEntityHist.getEntityUid());
        assertNull(nbsActEntityHist.getEntityVersionCtrlNbr());
        assertNull(nbsActEntityHist.getLastChgTime());
        assertNull(nbsActEntityHist.getLastChgUserId());
        assertNull(nbsActEntityHist.getRecordStatusCd());
        assertNull(nbsActEntityHist.getRecordStatusTime());
        assertNull(nbsActEntityHist.getTypeCd());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long nbsActEntityUid = 1L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        Long entityUid = 3L;
        Integer entityVersionCtrlNbr = 4;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 5L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String typeCd = "Type1";
        Long actUid = 6L;

        NbsActEntityDto dto = new NbsActEntityDto();
        dto.setNbsActEntityUid(nbsActEntityUid);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setEntityUid(entityUid);
        dto.setEntityVersionCtrlNbr(entityVersionCtrlNbr);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setTypeCd(typeCd);
        dto.setActUid(actUid);

        // Act
        NbsActEntityHist nbsActEntityHist = new NbsActEntityHist(dto);

        // Assert
        assertEquals(nbsActEntityUid, nbsActEntityHist.getNbsActEntityUid());
        assertEquals(addTime, nbsActEntityHist.getAddTime());
        assertEquals(addUserId, nbsActEntityHist.getAddUserId());
        assertEquals(entityUid, nbsActEntityHist.getEntityUid());
        assertEquals(entityVersionCtrlNbr, nbsActEntityHist.getEntityVersionCtrlNbr());
        assertEquals(lastChgTime, nbsActEntityHist.getLastChgTime());
        assertEquals(lastChgUserId, nbsActEntityHist.getLastChgUserId());
        assertEquals(recordStatusCd, nbsActEntityHist.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsActEntityHist.getRecordStatusTime());
        assertEquals(typeCd, nbsActEntityHist.getTypeCd());
        assertEquals(actUid, nbsActEntityHist.getActUid());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NbsActEntityHist nbsActEntityHist = new NbsActEntityHist();

        Long nbsActEntityUid = 1L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        Long entityUid = 3L;
        Integer entityVersionCtrlNbr = 4;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 5L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String typeCd = "Type1";
        Long actUid = 6L;

        // Act
        nbsActEntityHist.setNbsActEntityUid(nbsActEntityUid);
        nbsActEntityHist.setAddTime(addTime);
        nbsActEntityHist.setAddUserId(addUserId);
        nbsActEntityHist.setEntityUid(entityUid);
        nbsActEntityHist.setEntityVersionCtrlNbr(entityVersionCtrlNbr);
        nbsActEntityHist.setLastChgTime(lastChgTime);
        nbsActEntityHist.setLastChgUserId(lastChgUserId);
        nbsActEntityHist.setRecordStatusCd(recordStatusCd);
        nbsActEntityHist.setRecordStatusTime(recordStatusTime);
        nbsActEntityHist.setTypeCd(typeCd);
        nbsActEntityHist.setActUid(actUid);

        // Assert
        assertEquals(nbsActEntityUid, nbsActEntityHist.getNbsActEntityUid());
        assertEquals(addTime, nbsActEntityHist.getAddTime());
        assertEquals(addUserId, nbsActEntityHist.getAddUserId());
        assertEquals(entityUid, nbsActEntityHist.getEntityUid());
        assertEquals(entityVersionCtrlNbr, nbsActEntityHist.getEntityVersionCtrlNbr());
        assertEquals(lastChgTime, nbsActEntityHist.getLastChgTime());
        assertEquals(lastChgUserId, nbsActEntityHist.getLastChgUserId());
        assertEquals(recordStatusCd, nbsActEntityHist.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsActEntityHist.getRecordStatusTime());
        assertEquals(typeCd, nbsActEntityHist.getTypeCd());
        assertEquals(actUid, nbsActEntityHist.getActUid());
    }
}
