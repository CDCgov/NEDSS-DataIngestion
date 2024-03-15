package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;

import java.util.ArrayList;

public interface IObservationCodeService {

    ArrayList<String> deriveTheConditionCodeList(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest) throws DataProcessingException;

}
