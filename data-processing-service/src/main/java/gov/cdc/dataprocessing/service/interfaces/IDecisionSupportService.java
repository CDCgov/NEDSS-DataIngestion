package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;

public interface IDecisionSupportService {
    EdxLabInformationDto validateProxyContainer(LabResultProxyContainer labResultProxyVO,
                                                       EdxLabInformationDto edxLabInformationDT) throws DataProcessingException;
}
