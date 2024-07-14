package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;

import java.util.ArrayList;

public interface IObservationCodeService {

    ArrayList<String> deriveTheConditionCodeList(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest) throws DataProcessingException;

    String getReportingLabCLIA(BaseContainer proxy) throws DataProcessingException;
}
