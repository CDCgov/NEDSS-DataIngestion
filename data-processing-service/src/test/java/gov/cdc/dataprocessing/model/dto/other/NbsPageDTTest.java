package gov.cdc.dataprocessing.model.dto.other;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsPageDTTest {

    private NbsPageDT nbsPageDT;

    @BeforeEach
    void setUp() {
        nbsPageDT = new NbsPageDT();
    }

    @Test
    void testSettersAndGetters() {
        Long nbsPageUid = 12345L;
        Long waTemplateUid = 67890L;
        String formCd = "Form Code";
        String descTxt = "Description Text";
        byte[] jspPayload = {1, 2, 3};
        String datamartNm = "Datamart Name";
        String localId = "Local ID";
        String busObjType = "Business Object Type";
        Long lastChgUserId = 11111L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        String recordStatusCd = "Record Status Code";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());

        nbsPageDT.setNbsPageUid(nbsPageUid);
        nbsPageDT.setWaTemplateUid(waTemplateUid);
        nbsPageDT.setFormCd(formCd);
        nbsPageDT.setDescTxt(descTxt);
        nbsPageDT.setJspPayload(jspPayload);
        nbsPageDT.setDatamartNm(datamartNm);
        nbsPageDT.setLocalId(localId);
        nbsPageDT.setBusObjType(busObjType);
        nbsPageDT.setLastChgUserId(lastChgUserId);
        nbsPageDT.setLastChgTime(lastChgTime);
        nbsPageDT.setRecordStatusCd(recordStatusCd);
        nbsPageDT.setRecordStatusTime(recordStatusTime);

        assertEquals(nbsPageUid, nbsPageDT.getNbsPageUid());
        assertEquals(waTemplateUid, nbsPageDT.getWaTemplateUid());
        assertEquals(formCd, nbsPageDT.getFormCd());
        assertEquals(descTxt, nbsPageDT.getDescTxt());
        assertArrayEquals(jspPayload, nbsPageDT.getJspPayload());
        assertEquals(datamartNm, nbsPageDT.getDatamartNm());
        assertEquals(localId, nbsPageDT.getLocalId());
        assertEquals(busObjType, nbsPageDT.getBusObjType());
        assertEquals(lastChgUserId, nbsPageDT.getLastChgUserId());
        assertEquals(lastChgTime, nbsPageDT.getLastChgTime());
        assertEquals(recordStatusCd, nbsPageDT.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsPageDT.getRecordStatusTime());
    }
}
