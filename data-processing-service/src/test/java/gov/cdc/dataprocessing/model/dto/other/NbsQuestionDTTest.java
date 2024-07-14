package gov.cdc.dataprocessing.model.dto.other;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsQuestionDTTest {

    private NbsQuestionDT nbsQuestionDT;

    @BeforeEach
    void setUp() {
        nbsQuestionDT = new NbsQuestionDT();
    }

    @Test
    void testSettersAndGetters() {
        Long nbsQuestionUid = 12345L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 67890L;
        Long codeSetGroupId = 11111L;
        String dataCd = "Data Code";
        String dataLocation = "Data Location";
        String questionIdentifier = "Question Identifier";
        String questionOid = "Question OID";
        String questionOidSystemTxt = "Question OID System Text";
        String questionUnitIdentifier = "Question Unit Identifier";
        String dataType = "Data Type";
        String dataUseCd = "Data Use Code";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 22222L;
        String questionLabel = "Question Label";
        String questionToolTip = "Question ToolTip";
        String datamartColumnNm = "Datamart Column Name";
        String partTypeCd = "Part Type Code";
        String defaultValue = "Default Value";
        Integer versionCtrlNbr = 1;
        String unitParentIdentifier = "Unit Parent Identifier";

        nbsQuestionDT.setNbsQuestionUid(nbsQuestionUid);
        nbsQuestionDT.setAddTime(addTime);
        nbsQuestionDT.setAddUserId(addUserId);
        nbsQuestionDT.setCodeSetGroupId(codeSetGroupId);
        nbsQuestionDT.setDataCd(dataCd);
        nbsQuestionDT.setDataLocation(dataLocation);
        nbsQuestionDT.setQuestionIdentifier(questionIdentifier);
        nbsQuestionDT.setQuestionOid(questionOid);
        nbsQuestionDT.setQuestionOidSystemTxt(questionOidSystemTxt);
        nbsQuestionDT.setQuestionUnitIdentifier(questionUnitIdentifier);
        nbsQuestionDT.setDataType(dataType);
        nbsQuestionDT.setDataUseCd(dataUseCd);
        nbsQuestionDT.setLastChgTime(lastChgTime);
        nbsQuestionDT.setLastChgUserId(lastChgUserId);
        nbsQuestionDT.setQuestionLabel(questionLabel);
        nbsQuestionDT.setQuestionToolTip(questionToolTip);
        nbsQuestionDT.setDatamartColumnNm(datamartColumnNm);
        nbsQuestionDT.setPartTypeCd(partTypeCd);
        nbsQuestionDT.setDefaultValue(defaultValue);
        nbsQuestionDT.setVersionCtrlNbr(versionCtrlNbr);
        nbsQuestionDT.setUnitParentIdentifier(unitParentIdentifier);

        assertEquals(nbsQuestionUid, nbsQuestionDT.getNbsQuestionUid());
        assertEquals(addTime, nbsQuestionDT.getAddTime());
        assertEquals(addUserId, nbsQuestionDT.getAddUserId());
        assertEquals(codeSetGroupId, nbsQuestionDT.getCodeSetGroupId());
        assertEquals(dataCd, nbsQuestionDT.getDataCd());
        assertEquals(dataLocation, nbsQuestionDT.getDataLocation());
        assertEquals(questionIdentifier, nbsQuestionDT.getQuestionIdentifier());
        assertEquals(questionOid, nbsQuestionDT.getQuestionOid());
        assertEquals(questionOidSystemTxt, nbsQuestionDT.getQuestionOidSystemTxt());
        assertEquals(questionUnitIdentifier, nbsQuestionDT.getQuestionUnitIdentifier());
        assertEquals(dataType, nbsQuestionDT.getDataType());
        assertEquals(dataUseCd, nbsQuestionDT.getDataUseCd());
        assertEquals(lastChgTime, nbsQuestionDT.getLastChgTime());
        assertEquals(lastChgUserId, nbsQuestionDT.getLastChgUserId());
        assertEquals(questionLabel, nbsQuestionDT.getQuestionLabel());
        assertEquals(questionToolTip, nbsQuestionDT.getQuestionToolTip());
        assertEquals(datamartColumnNm, nbsQuestionDT.getDatamartColumnNm());
        assertEquals(partTypeCd, nbsQuestionDT.getPartTypeCd());
        assertEquals(defaultValue, nbsQuestionDT.getDefaultValue());
        assertEquals(versionCtrlNbr, nbsQuestionDT.getVersionCtrlNbr());
        assertEquals(unitParentIdentifier, nbsQuestionDT.getUnitParentIdentifier());
    }
}
