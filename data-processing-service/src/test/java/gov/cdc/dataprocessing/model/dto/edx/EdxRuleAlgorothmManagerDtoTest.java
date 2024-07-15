package gov.cdc.dataprocessing.model.dto.edx;


import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdxRuleAlgorothmManagerDtoTest {

    @Test
    void testGettersAndSetters() {
        EdxRuleAlgorothmManagerDto dto = new EdxRuleAlgorothmManagerDto();

        String updateAction = "updateAction";
        String nndComment = "nndComment";
        String onFailureToCreateNND = "onFailureToCreateNND";
        String dsmAlgorithmName = "dsmAlgorithmName";
        String conditionName = "conditionName";

        Map<Object, Object> edxRuleApplyDTMap = new HashMap<>();
        Map<Object, Object> edxRuleAdvCriteriaDTMap = new HashMap<>();
        Long dsmAlgorithmUid = 1L;
        String onFailureToCreateInv = "onFailureToCreateInv";
        String action = "action";

        PageActProxyContainer pageActContainer = new PageActProxyContainer();
        PamProxyContainer pamContainer = new PamProxyContainer();

        Collection<Object> edxActivityDetailLogDTCollection = null;
        String errorText = "errorText";
        Collection<Object> sendingFacilityColl = null;
        Map<Object, Object> edxBasicCriteriaMap = new HashMap<>();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long PHCUid = 2L;
        Long PHCRevisionUid = 3L;
        NBSDocumentDto documentDT = new NBSDocumentDto();
        Long MPRUid = 4L;
        boolean isContactRecordDoc = true;
        Map<String, EDXEventProcessCaseSummaryDto> eDXEventProcessCaseSummaryDTMap = new HashMap<>();
        boolean isUpdatedDocument = true;
        boolean isLabReportDoc = true;
        boolean isMorbReportDoc = true;
        boolean isCaseUpdated = true;
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();

        dto.setUpdateAction(updateAction);
        dto.setNndComment(nndComment);
        dto.setOnFailureToCreateNND(onFailureToCreateNND);
        dto.setDsmAlgorithmName(dsmAlgorithmName);
        dto.setConditionName(conditionName);
        dto.setEdxRuleApplyDTMap(edxRuleApplyDTMap);
        dto.setEdxRuleAdvCriteriaDTMap(edxRuleAdvCriteriaDTMap);
        dto.setDsmAlgorithmUid(dsmAlgorithmUid);
        dto.setOnFailureToCreateInv(onFailureToCreateInv);
        dto.setAction(action);
        dto.setPageActContainer(pageActContainer);
        dto.setPamContainer(pamContainer);
        dto.setEdxActivityDetailLogDTCollection(edxActivityDetailLogDTCollection);
        dto.setErrorText(errorText);
        dto.setSendingFacilityColl(sendingFacilityColl);
        dto.setEdxBasicCriteriaMap(edxBasicCriteriaMap);
        dto.setLastChgTime(lastChgTime);
        dto.setPHCUid(PHCUid);
        dto.setPHCRevisionUid(PHCRevisionUid);
        dto.setDocumentDT(documentDT);
        dto.setMPRUid(MPRUid);
        dto.setContactRecordDoc(isContactRecordDoc);
        dto.setEDXEventProcessCaseSummaryDTMap(eDXEventProcessCaseSummaryDTMap);
        dto.setUpdatedDocument(isUpdatedDocument);
        dto.setLabReportDoc(isLabReportDoc);
        dto.setMorbReportDoc(isMorbReportDoc);
        dto.setCaseUpdated(isCaseUpdated);
        dto.setEdxActivityLogDto(edxActivityLogDto);

        assertEquals(updateAction, dto.getUpdateAction());
        assertEquals(nndComment, dto.getNndComment());
        assertEquals(onFailureToCreateNND, dto.getOnFailureToCreateNND());
        assertEquals(dsmAlgorithmName, dto.getDsmAlgorithmName());
        assertEquals(conditionName, dto.getConditionName());
        assertEquals(edxRuleApplyDTMap, dto.getEdxRuleApplyDTMap());
        assertEquals(edxRuleAdvCriteriaDTMap, dto.getEdxRuleAdvCriteriaDTMap());
        assertEquals(dsmAlgorithmUid, dto.getDsmAlgorithmUid());
        assertEquals(onFailureToCreateInv, dto.getOnFailureToCreateInv());
        assertEquals(action, dto.getAction());
        assertEquals(pageActContainer, dto.getPageActContainer());
        assertEquals(pamContainer, dto.getPamContainer());
        assertEquals(edxActivityDetailLogDTCollection, dto.getEdxActivityDetailLogDTCollection());
        assertEquals(errorText, dto.getErrorText());
        assertEquals(sendingFacilityColl, dto.getSendingFacilityColl());
        assertEquals(edxBasicCriteriaMap, dto.getEdxBasicCriteriaMap());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(PHCUid, dto.getPHCUid());
        assertEquals(PHCRevisionUid, dto.getPHCRevisionUid());
        assertEquals(documentDT, dto.getDocumentDT());
        assertEquals(MPRUid, dto.getMPRUid());
        assertTrue(dto.isContactRecordDoc());
        assertEquals(eDXEventProcessCaseSummaryDTMap, dto.getEDXEventProcessCaseSummaryDTMap());
        assertTrue(dto.isUpdatedDocument());
        assertTrue(dto.isLabReportDoc());
        assertTrue(dto.isMorbReportDoc());
        assertTrue(dto.isCaseUpdated());
        assertEquals(edxActivityLogDto, dto.getEdxActivityLogDto());
    }
}
