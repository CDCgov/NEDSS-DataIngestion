package gov.cdc.dataprocessing.model.dto.act;


import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ActIdDtoTest {

    @Test
    void testGettersAndSetters() {
        ActIdDto dto = new ActIdDto();

        Long actUid = 1L;
        Integer actIdSeq = 1;
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String assigningAuthorityCd = "authorityCd";
        String assigningAuthorityDescTxt = "authorityDesc";
        String durationAmt = "durationAmt";
        String durationUnitCd = "durationUnitCd";
        String lastChgReasonCd = "lastChgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String recordStatusCd = "recordStatusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String rootExtensionTxt = "rootExtensionTxt";
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String typeCd = "typeCd";
        String typeDescTxt = "typeDescTxt";
        String userAffiliationTxt = "userAffiliationTxt";
        Timestamp validFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp validToTime = new Timestamp(System.currentTimeMillis());
        Integer versionCtrlNbr = 4;
        String progAreaCd = "progAreaCd";
        String jurisdictionCd = "jurisdictionCd";
        Long programJurisdictionOid = 5L;
        String sharedInd = "sharedInd";

        dto.setActUid(actUid);
        dto.setActIdSeq(actIdSeq);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setAssigningAuthorityCd(assigningAuthorityCd);
        dto.setAssigningAuthorityDescTxt(assigningAuthorityDescTxt);
        dto.setDurationAmt(durationAmt);
        dto.setDurationUnitCd(durationUnitCd);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLocalId(localId);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setRootExtensionTxt(rootExtensionTxt);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setTypeCd(typeCd);
        dto.setTypeDescTxt(typeDescTxt);
        dto.setUserAffiliationTxt(userAffiliationTxt);
        dto.setValidFromTime(validFromTime);
        dto.setValidToTime(validToTime);
        dto.setVersionCtrlNbr(versionCtrlNbr);
        dto.setProgAreaCd(progAreaCd);
        dto.setJurisdictionCd(jurisdictionCd);
        dto.setProgramJurisdictionOid(programJurisdictionOid);
        dto.setSharedInd(sharedInd);

        assertEquals(actUid, dto.getActUid());
        assertEquals(actIdSeq, dto.getActIdSeq());
        assertEquals(addReasonCd, dto.getAddReasonCd());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(addUserId, dto.getAddUserId());
        assertEquals(assigningAuthorityCd, dto.getAssigningAuthorityCd());
        assertEquals(assigningAuthorityDescTxt, dto.getAssigningAuthorityDescTxt());
        assertEquals(durationAmt, dto.getDurationAmt());
        assertEquals(durationUnitCd, dto.getDurationUnitCd());
        assertEquals(lastChgReasonCd, dto.getLastChgReasonCd());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(lastChgUserId, dto.getLastChgUserId());
        assertEquals(localId, dto.getLocalId());
        assertEquals(recordStatusCd, dto.getRecordStatusCd());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals(rootExtensionTxt, dto.getRootExtensionTxt());
        assertEquals(statusCd, dto.getStatusCd());
        assertEquals(statusTime, dto.getStatusTime());
        assertEquals(typeCd, dto.getTypeCd());
        assertEquals(typeDescTxt, dto.getTypeDescTxt());
        assertEquals(userAffiliationTxt, dto.getUserAffiliationTxt());
        assertEquals(validFromTime, dto.getValidFromTime());
        assertEquals(validToTime, dto.getValidToTime());
        assertEquals(versionCtrlNbr, dto.getVersionCtrlNbr());
        assertEquals(progAreaCd, dto.getProgAreaCd());
        assertEquals(jurisdictionCd, dto.getJurisdictionCd());
        assertEquals(programJurisdictionOid, dto.getProgramJurisdictionOid());
        assertEquals(sharedInd, dto.getSharedInd());
        assertNotNull(dto.getSuperclass());
        assertNotNull(dto.getUid());
    }

    @Test
    void testConstructor() {
        ActId actId = new ActId();
        actId.setActUid(1L);
        actId.setActIdSeq(1);
        actId.setAddReasonCd("reasonCd");
        actId.setAddTime(new Timestamp(System.currentTimeMillis()));
        actId.setAddUserId(2L);
        actId.setAssigningAuthorityCd("authorityCd");
        actId.setAssigningAuthorityDescTxt("authorityDesc");
        actId.setDurationAmt("durationAmt");
        actId.setDurationUnitCd("durationUnitCd");
        actId.setLastChgReasonCd("lastChgReasonCd");
        actId.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        actId.setLastChgUserId(3L);
        actId.setRecordStatusCd("recordStatusCd");
        actId.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        actId.setRootExtensionTxt("rootExtensionTxt");
        actId.setStatusCd("statusCd");
        actId.setStatusTime(new Timestamp(System.currentTimeMillis()));
        actId.setTypeCd("typeCd");
        actId.setTypeDescTxt("typeDescTxt");
        actId.setUserAffiliationTxt("userAffiliationTxt");
        actId.setValidFromTime(new Timestamp(System.currentTimeMillis()));
        actId.setValidToTime(new Timestamp(System.currentTimeMillis()));

        ActIdDto dto = new ActIdDto(actId);

        assertEquals(actId.getActUid(), dto.getActUid());
        assertEquals(actId.getActIdSeq(), dto.getActIdSeq());
        assertEquals(actId.getAddReasonCd(), dto.getAddReasonCd());
        assertEquals(actId.getAddTime(), dto.getAddTime());
        assertEquals(actId.getAddUserId(), dto.getAddUserId());
        assertEquals(actId.getAssigningAuthorityCd(), dto.getAssigningAuthorityCd());
        assertEquals(actId.getAssigningAuthorityDescTxt(), dto.getAssigningAuthorityDescTxt());
        assertEquals(actId.getDurationAmt(), dto.getDurationAmt());
        assertEquals(actId.getDurationUnitCd(), dto.getDurationUnitCd());
        assertEquals(actId.getLastChgReasonCd(), dto.getLastChgReasonCd());
        assertEquals(actId.getLastChgTime(), dto.getLastChgTime());
        assertEquals(actId.getLastChgUserId(), dto.getLastChgUserId());
        assertEquals(actId.getRecordStatusCd(), dto.getRecordStatusCd());
        assertEquals(actId.getRecordStatusTime(), dto.getRecordStatusTime());
        assertEquals(actId.getRootExtensionTxt(), dto.getRootExtensionTxt());
        assertEquals(actId.getStatusCd(), dto.getStatusCd());
        assertEquals(actId.getStatusTime(), dto.getStatusTime());
        assertEquals(actId.getTypeCd(), dto.getTypeCd());
        assertEquals(actId.getTypeDescTxt(), dto.getTypeDescTxt());
        assertEquals(actId.getUserAffiliationTxt(), dto.getUserAffiliationTxt());
        assertEquals(actId.getValidFromTime(), dto.getValidFromTime());
        assertEquals(actId.getValidToTime(), dto.getValidToTime());
    }
}
