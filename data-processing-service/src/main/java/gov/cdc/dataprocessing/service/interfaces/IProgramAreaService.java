package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;

public interface IProgramAreaService {
    String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest) throws DataProcessingException;
}
