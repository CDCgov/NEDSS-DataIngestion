package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class EntityLocatorParticipationTest {

    private EntityLocatorParticipationDto dto;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        dto = new EntityLocatorParticipationDto();
        timestamp = new Timestamp(System.currentTimeMillis());

        // Set all values to avoid the if (null) case
        dto.setEntityUid(1L);
        dto.setLocatorUid(2L);
        dto.setAddReasonCd("TestAddReason");
        dto.setAddUserId(3L);
        dto.setAddTime(timestamp);
        dto.setCd("TestCd");
        dto.setCdDescTxt("TestCdDescTxt");
        dto.setClassCd("TestClassCd");
        dto.setDurationAmt("TestDurationAmt");
        dto.setDurationUnitCd("TestDurationUnitCd");
        dto.setFromTime(timestamp);
        dto.setLastChgReasonCd("TestLastChgReasonCd");
        dto.setLastChgTime(timestamp);
        dto.setLastChgUserId(4L);
        dto.setLocatorDescTxt("TestLocatorDescTxt");
        dto.setRecordStatusCd("TestRecordStatusCd");
        dto.setRecordStatusTime(timestamp);
        dto.setStatusCd("T");
        dto.setStatusTime(timestamp);
        dto.setToTime(timestamp);
        dto.setUseCd("TestUseCd");
        dto.setUserAffiliationTxt("TestUserAffiliationTxt");
        dto.setValidTimeTxt("TestValidTimeTxt");
        dto.setVersionCtrlNbr(1);
        dto.setAsOfDate(timestamp);
    }

    @Test
    void testConstructorWithDto() {

        EntityLocatorParticipation entity = new EntityLocatorParticipation(dto);

        assertEquals(dto.getEntityUid(), entity.getEntityUid());
        assertEquals(dto.getLocatorUid(), entity.getLocatorUid());
        assertEquals("TestAddReason", entity.getAddReasonCd());
        assertEquals(3L, entity.getAddUserId());
        assertEquals(timestamp, entity.getAddTime());
        assertEquals(dto.getCd(), entity.getCd());
        assertEquals(dto.getCdDescTxt(), entity.getCdDescTxt());
        assertEquals(dto.getClassCd(), entity.getClassCd());
        assertEquals(dto.getDurationAmt(), entity.getDurationAmt());
        assertEquals(dto.getDurationUnitCd(), entity.getDurationUnitCd());
        assertEquals(dto.getFromTime(), entity.getFromTime());
        assertEquals(dto.getLastChgReasonCd(), entity.getLastChgReasonCd());
        assertEquals(dto.getLastChgTime(), entity.getLastChgTime());
        assertEquals(dto.getLastChgUserId(), entity.getLastChgUserId());
        assertEquals(dto.getLocatorDescTxt(), entity.getLocatorDescTxt());
        assertEquals(dto.getRecordStatusCd(), entity.getRecordStatusCd());
        assertEquals(dto.getRecordStatusTime(), entity.getRecordStatusTime());
        assertEquals(dto.getStatusCd(), entity.getStatusCd());
        assertEquals(dto.getStatusTime(), entity.getStatusTime());
        assertEquals(dto.getToTime(), entity.getToTime());
        assertEquals(dto.getUseCd(), entity.getUseCd());
        assertEquals(dto.getUserAffiliationTxt(), entity.getUserAffiliationTxt());
        assertEquals(dto.getValidTimeTxt(), entity.getValidTimeTxt());
        assertEquals(dto.getVersionCtrlNbr(), entity.getVersionCtrlNbr());
        assertEquals(dto.getAsOfDate(), entity.getAsOfDate());
    }
}
