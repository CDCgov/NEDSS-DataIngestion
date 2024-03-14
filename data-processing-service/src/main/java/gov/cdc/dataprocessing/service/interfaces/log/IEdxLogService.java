package gov.cdc.dataprocessing.service.interfaces.log;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.EdxLogException;

public interface IEdxLogService {
    Object processingLog() throws EdxLogException;
}
