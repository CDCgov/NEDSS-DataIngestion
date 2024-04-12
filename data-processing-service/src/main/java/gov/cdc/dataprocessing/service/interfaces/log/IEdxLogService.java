package gov.cdc.dataprocessing.service.interfaces.log;

import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;

public interface IEdxLogService {
    Object processingLog() throws EdxLogException;
    void saveEdxActivityLog(EDXActivityLogDto edxActivityLogDto) throws EdxLogException;
    void saveEdxActivityDetailLog(EDXActivityDetailLogDto detailLogDto) throws EdxLogException;
}
