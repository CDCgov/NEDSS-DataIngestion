package gov.cdc.dataprocessing.service.interfaces.manager;

import gov.cdc.dataprocessing.exception.DataProcessingDBException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;

import java.util.List;


public interface IManagerService {
    PublicHealthCaseFlowContainer processingELR(Integer data) throws EdxLogException, DataProcessingDBException;

    void handlingWdsAndLab(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException, DataProcessingDBException, EdxLogException;

    void updateNbsInterfaceStatus(List<Integer> ids);
}
