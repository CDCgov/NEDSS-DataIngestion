package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.exception.DataProcessingDBException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerTransactionService;
import org.springframework.stereotype.Service;

@Service
public class ManagerTransactionService implements IManagerTransactionService {
    private final ManagerService managerService;

    public ManagerTransactionService(ManagerService managerService) {
        this.managerService = managerService;
    }

    public void processWithTransactionSeparation(Integer id, boolean retryApplied) throws DataProcessingDBException, EdxLogException, DataProcessingException {
        var result = managerService.processingELR(id, retryApplied);  // must call through proxy
        if (result != null) {
            managerService.handlingWdsAndLab(result, retryApplied);  // must call through proxy
        }
    }
}
