package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.EdxLogException;

public interface IEdxLogService {
    Object processingLog() throws EdxLogException;
}
