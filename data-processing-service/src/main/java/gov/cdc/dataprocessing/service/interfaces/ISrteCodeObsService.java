package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface ISrteCodeObsService {
    Map<String, Object> getSnomed(String code, String type, String clia) throws DataProcessingException;
    String getConditionForSnomedCode(String snomedCd);
    String getConditionForLoincCode(String loinCd);
    String getDefaultConditionForLocalResultCode(String labResultCd, String laboratoryId);
    String getDefaultConditionForLabTest(String labTestCd, String laboratoryId);
    ObservationVO labLoincSnomedLookup(ObservationVO obsVO, String labClia);
    HashMap<Object, Object> getProgramArea(String reportingLabCLIA, Collection<ObservationVO> observationVOCollection, String electronicInd) throws DataProcessingException;
}
