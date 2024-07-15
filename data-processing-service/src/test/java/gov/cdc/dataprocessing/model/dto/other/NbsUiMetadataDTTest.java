package gov.cdc.dataprocessing.model.dto.other;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsUiMetadataDTTest {

    private NbsUiMetadataDT nbsUiMetadataDT;

    @BeforeEach
    void setUp() {
        nbsUiMetadataDT = new NbsUiMetadataDT();
    }

    @Test
    void testSettersAndGetters() {
        Long id = 12345L;
        String label = "Question Label";
        String toolTip = "ToolTip";
        String formCd = "INV_FORM";
        String enableInd = "Y";
        String defaultValue = "Default";
        String displayInd = "Y";
        Integer orderNbr = 1;
        String requiredInd = "Y";
        Integer tabOrderId = 1;
        String tabName = "Tab1";
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        nbsUiMetadataDT.setNbsUiMetadataUid(id);
        nbsUiMetadataDT.setNbsUiComponentUid(id);
        nbsUiMetadataDT.setNbsQuestionUid(id);
        nbsUiMetadataDT.setParentUid(id);
        nbsUiMetadataDT.setNbsPageUid(id);
        nbsUiMetadataDT.setQuestionLabel(label);
        nbsUiMetadataDT.setQuestionToolTip(toolTip);
        nbsUiMetadataDT.setInvestigationFormCd(formCd);
        nbsUiMetadataDT.setEnableInd(enableInd);
        nbsUiMetadataDT.setDefaultValue(defaultValue);
        nbsUiMetadataDT.setDisplayInd(displayInd);
        nbsUiMetadataDT.setOrderNbr(orderNbr);
        nbsUiMetadataDT.setRequiredInd(requiredInd);
        nbsUiMetadataDT.setTabName(tabName);
        nbsUiMetadataDT.setAddTime(currentTime);
        nbsUiMetadataDT.setAddUserId(id);
        nbsUiMetadataDT.setLastChgTime(currentTime);
        nbsUiMetadataDT.setLastChgUserId(id);
        nbsUiMetadataDT.setRecordStatusCd("Active");
        nbsUiMetadataDT.setRecordStatusTime(currentTime);
        nbsUiMetadataDT.setMaxLength(id);
        nbsUiMetadataDT.setLdfPosition("Position");
        nbsUiMetadataDT.setCssStyle("Style");
        nbsUiMetadataDT.setLdfPageId("Page1");

        assertEquals(id, nbsUiMetadataDT.getNbsUiMetadataUid());
        assertEquals(id, nbsUiMetadataDT.getNbsUiComponentUid());
        assertEquals(id, nbsUiMetadataDT.getNbsQuestionUid());
        assertEquals(id, nbsUiMetadataDT.getParentUid());
        assertEquals(id, nbsUiMetadataDT.getNbsPageUid());
        assertEquals(label, nbsUiMetadataDT.getQuestionLabel());
        assertEquals(toolTip, nbsUiMetadataDT.getQuestionToolTip());
        assertEquals(formCd, nbsUiMetadataDT.getInvestigationFormCd());
        assertEquals(enableInd, nbsUiMetadataDT.getEnableInd());
        assertEquals(defaultValue, nbsUiMetadataDT.getDefaultValue());
        assertEquals(displayInd, nbsUiMetadataDT.getDisplayInd());
        assertEquals(orderNbr, nbsUiMetadataDT.getOrderNbr());
        assertEquals(requiredInd, nbsUiMetadataDT.getRequiredInd());
        assertEquals(tabName, nbsUiMetadataDT.getTabName());
        assertEquals(currentTime, nbsUiMetadataDT.getAddTime());
        assertEquals(id, nbsUiMetadataDT.getAddUserId());
        assertEquals(currentTime, nbsUiMetadataDT.getLastChgTime());
        assertEquals(id, nbsUiMetadataDT.getLastChgUserId());
        assertEquals("Active", nbsUiMetadataDT.getRecordStatusCd());
        assertEquals(currentTime, nbsUiMetadataDT.getRecordStatusTime());
        assertEquals(id, nbsUiMetadataDT.getMaxLength());
        assertEquals("Position", nbsUiMetadataDT.getLdfPosition());
        assertEquals("Style", nbsUiMetadataDT.getCssStyle());
        assertEquals("Page1", nbsUiMetadataDT.getLdfPageId());
    }

    @Test
    void testUnimplementedMethods() {
        assertNull(nbsUiMetadataDT.getJurisdictionCd());
        assertNull(nbsUiMetadataDT.getLastChgReasonCd());
        assertNull(nbsUiMetadataDT.getLocalId());
        assertNull(nbsUiMetadataDT.getProgAreaCd());
        assertNull(nbsUiMetadataDT.getProgramJurisdictionOid());
        assertNull(nbsUiMetadataDT.getSharedInd());
        assertNull(nbsUiMetadataDT.getStatusCd());
        assertNull(nbsUiMetadataDT.getStatusTime());
        assertNull(nbsUiMetadataDT.getSuperclass());
        assertNull(nbsUiMetadataDT.getUid());
        assertNull(nbsUiMetadataDT.getVersionCtrlNbr());
        assertFalse(nbsUiMetadataDT.isItDelete());
        assertFalse(nbsUiMetadataDT.isItDirty());
        assertFalse(nbsUiMetadataDT.isItNew());

        nbsUiMetadataDT.setItDelete(true);
        nbsUiMetadataDT.setItDirty(true);
        nbsUiMetadataDT.setItNew(true);
        nbsUiMetadataDT.setJurisdictionCd("Jurisdiction");
        nbsUiMetadataDT.setLastChgReasonCd("Reason");
        nbsUiMetadataDT.setLocalId("LocalId");
        nbsUiMetadataDT.setProgAreaCd("ProgArea");
        nbsUiMetadataDT.setProgramJurisdictionOid(123L);
        nbsUiMetadataDT.setSharedInd("Shared");
        nbsUiMetadataDT.setStatusCd("Status");
        nbsUiMetadataDT.setStatusTime(new Timestamp(System.currentTimeMillis()));

        assertFalse(nbsUiMetadataDT.isItDelete());
        assertFalse(nbsUiMetadataDT.isItDirty());
        assertFalse(nbsUiMetadataDT.isItNew());
    }
}
