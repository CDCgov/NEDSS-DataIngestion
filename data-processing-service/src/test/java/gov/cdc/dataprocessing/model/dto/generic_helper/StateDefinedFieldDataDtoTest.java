package gov.cdc.dataprocessing.model.dto.generic_helper;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateDefinedFieldDataDtoTest {

    @Test
    void testGettersAndSetters() {
        StateDefinedFieldDataDto dto = new StateDefinedFieldDataDto();

        Long ldfUid = 1L;
        String businessObjNm = "businessObjNm";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long businessObjUid = 2L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        String ldfValue = "ldfValue";
        Integer versionCtrlNbr = 1;
        String conditionCd = "conditionCd";
        boolean itDirty = true;
        String codeSetNm = "codeSetNm";
        String fieldSize = "fieldSize";
        String dataType = "dataType";

        dto.setLdfUid(ldfUid);
        dto.setBusinessObjNm(businessObjNm);
        dto.setAddTime(addTime);
        dto.setBusinessObjUid(businessObjUid);
        dto.setLastChgTime(lastChgTime);
        dto.setLdfValue(ldfValue);
        dto.setVersionCtrlNbr(versionCtrlNbr);
        dto.setConditionCd(conditionCd);
        dto.setItDirty(itDirty);
        dto.setCodeSetNm(codeSetNm);
        dto.setFieldSize(fieldSize);
        dto.setDataType(dataType);

        assertEquals(ldfUid, dto.getLdfUid());
        assertEquals(businessObjNm, dto.getBusinessObjNm());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(businessObjUid, dto.getBusinessObjUid());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(ldfValue, dto.getLdfValue());
        assertEquals(versionCtrlNbr, dto.getVersionCtrlNbr());
        assertEquals(conditionCd, dto.getConditionCd());
        assertTrue(dto.isItDirty());
        assertEquals(codeSetNm, dto.getCodeSetNm());
        assertEquals(fieldSize, dto.getFieldSize());
        assertEquals(dataType, dto.getDataType());
    }
}
