package gov.cdc.dataprocessing.service.interfaces.lookup_data;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public interface ISrteCodeObsService {
    Map<String, Object> getSnomed(String code, String type, String clia) throws DataProcessingException;
    String getConditionForSnomedCode(String snomedCd);
    String getConditionForLoincCode(String loinCd);
    String getDefaultConditionForLocalResultCode(String labResultCd, String laboratoryId);
    String getDefaultConditionForLabTest(String labTestCd, String laboratoryId);
    ObservationContainer labLoincSnomedLookup(ObservationContainer obsVO, String labClia);
    HashMap<Object, Object> getProgramArea(String reportingLabCLIA, Collection<ObservationContainer> observationContainerCollection, String electronicInd) throws DataProcessingException;

    String getPAFromSNOMEDCodes(String reportingLabCLIA, Collection<ObsValueCodedDto> obsValueCodedDtoColl) throws DataProcessingException;
    String getPAFromLOINCCode(String reportingLabCLIA, ObservationContainer resultTestVO) throws DataProcessingException;
    String getPAFromLocalResultCode(String reportingLabCLIA, Collection<ObsValueCodedDto> obsValueCodedDtoColl);
    String getPAFromLocalTestCode(String reportingLabCLIA, ObservationContainer resultTestVO);
}
