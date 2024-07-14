package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.phc.EntityGroupDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.EntityGroup;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EntityGroupTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        EntityGroup entityGroup = new EntityGroup();

        // Assert
        assertNull(entityGroup.getEntityGroupUid());
        assertNull(entityGroup.getAddReasonCd());
        assertNull(entityGroup.getAddTime());
        assertNull(entityGroup.getAddUserId());
        assertNull(entityGroup.getCd());
        assertNull(entityGroup.getCdDescTxt());
        assertNull(entityGroup.getDescription());
        assertNull(entityGroup.getDurationAmt());
        assertNull(entityGroup.getDurationUnitCd());
        assertNull(entityGroup.getFromTime());
        assertNull(entityGroup.getGroupCnt());
        assertNull(entityGroup.getLastChgReasonCd());
        assertNull(entityGroup.getLastChgTime());
        assertNull(entityGroup.getLastChgUserId());
        assertNull(entityGroup.getLocalId());
        assertNull(entityGroup.getNm());
        assertNull(entityGroup.getRecordStatusCd());
        assertNull(entityGroup.getRecordStatusTime());
        assertNull(entityGroup.getStatusCd());
        assertNull(entityGroup.getStatusTime());
        assertNull(entityGroup.getToTime());
        assertNull(entityGroup.getUserAffiliationTxt());
        assertNull(entityGroup.getVersionCtrlNbr());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long entityGroupUid = 1L;
        String addReasonCd = "reason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "code";
        String cdDescTxt = "description";
        String description = "entity description";
        String durationAmt = "1h";
        String durationUnitCd = "hour";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        Integer groupCnt = 5;
        String lastChgReasonCd = "last change reason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "local123";
        String nm = "entity name";
        String recordStatusCd = "active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String userAffiliationTxt = "affiliation";
        Integer versionCtrlNbr = 1;

        EntityGroupDto dto = new EntityGroupDto();
        dto.setEntityGroupUid(entityGroupUid);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setCd(cd);
        dto.setCdDescTxt(cdDescTxt);
        dto.setDescription(description);
        dto.setDurationAmt(durationAmt);
        dto.setDurationUnitCd(durationUnitCd);
        dto.setFromTime(fromTime);
        dto.setGroupCnt(groupCnt);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLocalId(localId);
        dto.setNm(nm);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setToTime(toTime);
        dto.setUserAffiliationTxt(userAffiliationTxt);
        dto.setVersionCtrlNbr(versionCtrlNbr);

        // Act
        EntityGroup entityGroup = new EntityGroup(dto);

        // Assert
        assertEquals(entityGroupUid, entityGroup.getEntityGroupUid());
        assertEquals(addReasonCd, entityGroup.getAddReasonCd());
        assertNotNull(entityGroup.getAddTime());
        assertNull( entityGroup.getAddUserId());
        assertEquals(cd, entityGroup.getCd());
        assertEquals(cdDescTxt, entityGroup.getCdDescTxt());
        assertEquals(description, entityGroup.getDescription());
        assertEquals(durationAmt, entityGroup.getDurationAmt());
        assertEquals(durationUnitCd, entityGroup.getDurationUnitCd());
        assertEquals(fromTime, entityGroup.getFromTime());
        assertEquals(groupCnt, entityGroup.getGroupCnt());
        assertEquals(lastChgReasonCd, entityGroup.getLastChgReasonCd());
        assertEquals(lastChgTime, entityGroup.getLastChgTime());
        assertEquals(lastChgUserId, entityGroup.getLastChgUserId());
        assertEquals(localId, entityGroup.getLocalId());
        assertEquals(nm, entityGroup.getNm());
        assertEquals(recordStatusCd, entityGroup.getRecordStatusCd());
        assertEquals(recordStatusTime, entityGroup.getRecordStatusTime());
        assertEquals(statusCd, entityGroup.getStatusCd());
        assertEquals(statusTime, entityGroup.getStatusTime());
        assertEquals(toTime, entityGroup.getToTime());
        assertEquals(userAffiliationTxt, entityGroup.getUserAffiliationTxt());
        assertEquals(versionCtrlNbr, entityGroup.getVersionCtrlNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        EntityGroup entityGroup = new EntityGroup();

        Long entityGroupUid = 1L;
        String addReasonCd = "reason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "code";
        String cdDescTxt = "description";
        String description = "entity description";
        String durationAmt = "1h";
        String durationUnitCd = "hour";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        Integer groupCnt = 5;
        String lastChgReasonCd = "last change reason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "local123";
        String nm = "entity name";
        String recordStatusCd = "active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String userAffiliationTxt = "affiliation";
        Integer versionCtrlNbr = 1;

        // Act
        entityGroup.setEntityGroupUid(entityGroupUid);
        entityGroup.setAddReasonCd(addReasonCd);
        entityGroup.setAddTime(addTime);
        entityGroup.setAddUserId(addUserId);
        entityGroup.setCd(cd);
        entityGroup.setCdDescTxt(cdDescTxt);
        entityGroup.setDescription(description);
        entityGroup.setDurationAmt(durationAmt);
        entityGroup.setDurationUnitCd(durationUnitCd);
        entityGroup.setFromTime(fromTime);
        entityGroup.setGroupCnt(groupCnt);
        entityGroup.setLastChgReasonCd(lastChgReasonCd);
        entityGroup.setLastChgTime(lastChgTime);
        entityGroup.setLastChgUserId(lastChgUserId);
        entityGroup.setLocalId(localId);
        entityGroup.setNm(nm);
        entityGroup.setRecordStatusCd(recordStatusCd);
        entityGroup.setRecordStatusTime(recordStatusTime);
        entityGroup.setStatusCd(statusCd);
        entityGroup.setStatusTime(statusTime);
        entityGroup.setToTime(toTime);
        entityGroup.setUserAffiliationTxt(userAffiliationTxt);
        entityGroup.setVersionCtrlNbr(versionCtrlNbr);

        // Assert
        assertEquals(entityGroupUid, entityGroup.getEntityGroupUid());
        assertEquals(addReasonCd, entityGroup.getAddReasonCd());
        assertEquals(addTime, entityGroup.getAddTime());
        assertEquals(addUserId, entityGroup.getAddUserId());
        assertEquals(cd, entityGroup.getCd());
        assertEquals(cdDescTxt, entityGroup.getCdDescTxt());
        assertEquals(description, entityGroup.getDescription());
        assertEquals(durationAmt, entityGroup.getDurationAmt());
        assertEquals(durationUnitCd, entityGroup.getDurationUnitCd());
        assertEquals(fromTime, entityGroup.getFromTime());
        assertEquals(groupCnt, entityGroup.getGroupCnt());
        assertEquals(lastChgReasonCd, entityGroup.getLastChgReasonCd());
        assertEquals(lastChgTime, entityGroup.getLastChgTime());
        assertEquals(lastChgUserId, entityGroup.getLastChgUserId());
        assertEquals(localId, entityGroup.getLocalId());
        assertEquals(nm, entityGroup.getNm());
        assertEquals(recordStatusCd, entityGroup.getRecordStatusCd());
        assertEquals(recordStatusTime, entityGroup.getRecordStatusTime());
        assertEquals(statusCd, entityGroup.getStatusCd());
        assertEquals(statusTime, entityGroup.getStatusTime());
        assertEquals(toTime, entityGroup.getToTime());
        assertEquals(userAffiliationTxt, entityGroup.getUserAffiliationTxt());
        assertEquals(versionCtrlNbr, entityGroup.getVersionCtrlNbr());
    }
}
