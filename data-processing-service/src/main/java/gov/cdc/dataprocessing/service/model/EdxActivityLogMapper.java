package gov.cdc.dataprocessing.service.model;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import org.springframework.stereotype.Service;

@Service
public class EdxActivityLogMapper {
    public EdxActivityLog map(EDXActivityLogDto edxActivityLogDto) {
        EdxActivityLog edxActivityLog = new EdxActivityLog();
        edxActivityLog.setId(edxActivityLogDto.getEdxActivityLogUid());
        edxActivityLog.setSourceUid(edxActivityLogDto.getSourceUid());
        edxActivityLog.setTargetUid(edxActivityLogDto.getTargetUid());
        edxActivityLog.setDocType(edxActivityLogDto.getDocType());
        edxActivityLog.setRecordStatusCd(edxActivityLogDto.getRecordStatusCd());
        edxActivityLog.setRecordStatusTime(edxActivityLogDto.getRecordStatusTime());
        edxActivityLog.setExceptionTxt(edxActivityLogDto.getExceptionTxt());
        edxActivityLog.setImpExpIndCd(edxActivityLogDto.getImpExpIndCd());
        edxActivityLog.setSourceTypeCd(edxActivityLogDto.getSourceTypeCd());
        edxActivityLog.setTargetTypeCd(edxActivityLogDto.getTargetTypeCd());
        edxActivityLog.setBusinessObjLocalid(edxActivityLogDto.getBusinessObjLocalId());
        edxActivityLog.setDocNm(edxActivityLogDto.getDocName());
        edxActivityLog.setSourceNm(edxActivityLogDto.getSrcName());
        edxActivityLog.setAlgorithmAction(edxActivityLogDto.getAlgorithmAction());
        edxActivityLog.setAlgorithmName(edxActivityLogDto.getAlgorithmName());
        edxActivityLog.setMessageId(edxActivityLogDto.getMessageId());
        edxActivityLog.setEntityNm(edxActivityLogDto.getEntityNm());
        edxActivityLog.setAccessionNbr(edxActivityLogDto.getAccessionNbr());

        return edxActivityLog;
    }
}
