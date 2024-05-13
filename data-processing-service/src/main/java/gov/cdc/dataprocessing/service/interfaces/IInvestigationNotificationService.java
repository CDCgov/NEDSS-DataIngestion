package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;

public interface IInvestigationNotificationService {
    EDXActivityDetailLogDto sendNotification(Object pageObj, String nndComment) throws DataProcessingException;
}
