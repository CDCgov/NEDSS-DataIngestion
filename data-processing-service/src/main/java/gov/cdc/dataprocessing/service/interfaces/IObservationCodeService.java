package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;

import java.util.ArrayList;

public interface IObservationCodeService {

    ArrayList<String> deriveTheConditionCodeList(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest) throws DataProcessingException;

}
