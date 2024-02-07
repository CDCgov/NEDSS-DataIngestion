package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;

public interface IMsgOutEStoredProcService {
    void callUpdateSpecimenCollDateSP(EdxLabInformationDT edxLabInformationDT) throws DataProcessingException;
}
