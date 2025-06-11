package gov.cdc.dataprocessing.service.interfaces.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;


public interface IMsgOutEStoredProcService {
    void callUpdateSpecimenCollDateSP(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException;
}
