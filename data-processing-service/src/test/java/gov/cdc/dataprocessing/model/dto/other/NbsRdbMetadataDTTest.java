package gov.cdc.dataprocessing.model.dto.other;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsRdbMetadataDTTest {

    private NbsRdbMetadataDT nbsRdbMetadataDT;

    @BeforeEach
    void setUp() {
        nbsRdbMetadataDT = new NbsRdbMetadataDT();
    }

    @Test
    void testSettersAndGetters() {
        Long nbsRdbMetadataUid = 12345L;
        Long nbsPageUid = 67890L;
        Long nbsUiMetadataUid = 11111L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 22222L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        String localId = "LocalId";
        String rptAdminColumnNm = "RptAdminColumnName";
        String rdbTableNm = "RdbTableName";
        String userDefinedColumnNm = "UserDefinedColumnName";
        String rdbColumnNm = "RdbColumnName";
        Integer dataMartRepeatNbr = 1;

        nbsRdbMetadataDT.setNbsRdbMetadataUid(nbsRdbMetadataUid);
        nbsRdbMetadataDT.setNbsPageUid(nbsPageUid);
        nbsRdbMetadataDT.setNbsUiMetadataUid(nbsUiMetadataUid);
        nbsRdbMetadataDT.setRecordStatusCd(recordStatusCd);
        nbsRdbMetadataDT.setRecordStatusTime(recordStatusTime);
        nbsRdbMetadataDT.setLastChgUserId(lastChgUserId);
        nbsRdbMetadataDT.setLastChgTime(lastChgTime);
        nbsRdbMetadataDT.setLocalId(localId);
        nbsRdbMetadataDT.setRptAdminColumnNm(rptAdminColumnNm);
        nbsRdbMetadataDT.setRdbTableNm(rdbTableNm);
        nbsRdbMetadataDT.setUserDefinedColumnNm(userDefinedColumnNm);
        nbsRdbMetadataDT.setRdbColumnNm(rdbColumnNm);
        nbsRdbMetadataDT.setDataMartRepeatNbr(dataMartRepeatNbr);

        assertEquals(nbsRdbMetadataUid, nbsRdbMetadataDT.getNbsRdbMetadataUid());
        assertEquals(nbsPageUid, nbsRdbMetadataDT.getNbsPageUid());
        assertEquals(nbsUiMetadataUid, nbsRdbMetadataDT.getNbsUiMetadataUid());
        assertEquals(recordStatusCd, nbsRdbMetadataDT.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsRdbMetadataDT.getRecordStatusTime());
        assertEquals(lastChgUserId, nbsRdbMetadataDT.getLastChgUserId());
        assertEquals(lastChgTime, nbsRdbMetadataDT.getLastChgTime());
        assertEquals(localId, nbsRdbMetadataDT.getLocalId());
        assertEquals(rptAdminColumnNm, nbsRdbMetadataDT.getRptAdminColumnNm());
        assertEquals(rdbTableNm, nbsRdbMetadataDT.getRdbTableNm());
        assertEquals(userDefinedColumnNm, nbsRdbMetadataDT.getUserDefinedColumnNm());
        assertEquals(rdbColumnNm, nbsRdbMetadataDT.getRdbColumnNm());
        assertEquals(dataMartRepeatNbr, nbsRdbMetadataDT.getDataMartRepeatNbr());
    }
}
