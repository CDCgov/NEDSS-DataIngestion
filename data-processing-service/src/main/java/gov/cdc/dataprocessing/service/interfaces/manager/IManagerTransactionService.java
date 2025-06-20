package gov.cdc.dataprocessing.service.interfaces.manager;

import gov.cdc.dataprocessing.exception.DataProcessingDBException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;

public interface IManagerTransactionService {
    void processWithTransactionSeparation(Integer id) throws DataProcessingDBException, EdxLogException, DataProcessingException;

}
