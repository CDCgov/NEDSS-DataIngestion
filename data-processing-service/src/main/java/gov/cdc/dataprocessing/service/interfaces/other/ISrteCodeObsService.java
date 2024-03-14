package gov.cdc.dataprocessing.service.interfaces.other;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface ISrteCodeObsService {
    Map<String, Object> getSnomed(String code, String type, String clia) throws DataProcessingException;
    String getConditionForSnomedCode(String snomedCd);
    String getConditionForLoincCode(String loinCd);
    String getDefaultConditionForLocalResultCode(String labResultCd, String laboratoryId);
    String getDefaultConditionForLabTest(String labTestCd, String laboratoryId);
    ObservationContainer labLoincSnomedLookup(ObservationContainer obsVO, String labClia);
    HashMap<Object, Object> getProgramArea(String reportingLabCLIA, Collection<ObservationContainer> observationContainerCollection, String electronicInd) throws DataProcessingException;
}
