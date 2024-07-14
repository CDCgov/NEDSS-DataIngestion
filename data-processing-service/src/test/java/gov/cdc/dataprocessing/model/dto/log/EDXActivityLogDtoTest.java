package gov.cdc.dataprocessing.model.dto.log;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EDXActivityLogDtoTest {

    @Test
    void testGettersAndSetters() {
        EDXActivityLogDto dto = new EDXActivityLogDto();

        Long edxActivityLogUid = 1L;
        Long sourceUid = 2L;
        Long targetUid = 3L;
        String docType = "docType";
        String recordStatusCd = "recordStatusCd";
        String recordStatusCdHtml = "recordStatusCdHtml";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String exceptionTxt = "exceptionTxt";
        String impExpIndCd = "impExpIndCd";
        String impExpIndCdDesc = "impExpIndCdDesc";
        String sourceTypeCd = "sourceTypeCd";
        String targetTypeCd = "targetTypeCd";
        String businessObjLocalId = "businessObjLocalId";
        String docName = "docName";
        String srcName = "srcName";
        String viewLink = "viewLink";
        String exceptionShort = "exceptionShort";
        Collection<EDXActivityDetailLogDto> EDXActivityLogDTWithVocabDetails = new ArrayList<>();
        Collection<Object> EDXActivityLogDTWithQuesDetails = new ArrayList<>();
        Collection<EDXActivityDetailLogDto> EDXActivityLogDTDetails = new ArrayList<>();
        Map<Object, Object> newaddedCodeSets = new HashMap<>();
        boolean logDetailAllStatus = true;
        String algorithmAction = "algorithmAction";
        String actionId = "actionId";
        String messageId = "messageId";
        String entityNm = "entityNm";
        String accessionNbr = "accessionNbr";
        String algorithmName = "algorithmName";

        dto.setEdxActivityLogUid(edxActivityLogUid);
        dto.setSourceUid(sourceUid);
        dto.setTargetUid(targetUid);
        dto.setDocType(docType);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusCdHtml(recordStatusCdHtml);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setExceptionTxt(exceptionTxt);
        dto.setImpExpIndCd(impExpIndCd);
        dto.setImpExpIndCdDesc(impExpIndCdDesc);
        dto.setSourceTypeCd(sourceTypeCd);
        dto.setTargetTypeCd(targetTypeCd);
        dto.setBusinessObjLocalId(businessObjLocalId);
        dto.setDocName(docName);
        dto.setSrcName(srcName);
        dto.setViewLink(viewLink);
        dto.setExceptionShort(exceptionShort);
        dto.setEDXActivityLogDTWithVocabDetails(EDXActivityLogDTWithVocabDetails);
        dto.setEDXActivityLogDTWithQuesDetails(EDXActivityLogDTWithQuesDetails);
        dto.setEDXActivityLogDTDetails(EDXActivityLogDTDetails);
        dto.setNewaddedCodeSets(newaddedCodeSets);
        dto.setLogDetailAllStatus(logDetailAllStatus);
        dto.setAlgorithmAction(algorithmAction);
        dto.setActionId(actionId);
        dto.setMessageId(messageId);
        dto.setEntityNm(entityNm);
        dto.setAccessionNbr(accessionNbr);
        dto.setAlgorithmName(algorithmName);

        assertEquals(edxActivityLogUid, dto.getEdxActivityLogUid());
        assertEquals(sourceUid, dto.getSourceUid());
        assertEquals(targetUid, dto.getTargetUid());
        assertEquals(docType, dto.getDocType());
        assertEquals(recordStatusCd, dto.getRecordStatusCd());
        assertEquals(recordStatusCdHtml, dto.getRecordStatusCdHtml());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals(exceptionTxt, dto.getExceptionTxt());
        assertEquals(impExpIndCd, dto.getImpExpIndCd());
        assertEquals(impExpIndCdDesc, dto.getImpExpIndCdDesc());
        assertEquals(sourceTypeCd, dto.getSourceTypeCd());
        assertEquals(targetTypeCd, dto.getTargetTypeCd());
        assertEquals(businessObjLocalId, dto.getBusinessObjLocalId());
        assertEquals(docName, dto.getDocName());
        assertEquals(srcName, dto.getSrcName());
        assertEquals(viewLink, dto.getViewLink());
        assertEquals(exceptionShort, dto.getExceptionShort());
        assertEquals(EDXActivityLogDTWithVocabDetails, dto.getEDXActivityLogDTWithVocabDetails());
        assertEquals(EDXActivityLogDTWithQuesDetails, dto.getEDXActivityLogDTWithQuesDetails());
        assertEquals(EDXActivityLogDTDetails, dto.getEDXActivityLogDTDetails());
        assertEquals(newaddedCodeSets, dto.getNewaddedCodeSets());
        assertTrue(dto.isLogDetailAllStatus());
        assertEquals(algorithmAction, dto.getAlgorithmAction());
        assertEquals(actionId, dto.getActionId());
        assertEquals(messageId, dto.getMessageId());
        assertEquals(entityNm, dto.getEntityNm());
        assertEquals(accessionNbr, dto.getAccessionNbr());
        assertEquals(algorithmName, dto.getAlgorithmName());
    }
}
