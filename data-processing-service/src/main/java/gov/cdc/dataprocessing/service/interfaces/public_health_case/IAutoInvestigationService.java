package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;

import java.util.Collection;
import java.util.Map;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface IAutoInvestigationService {
    /**
     * Description: this method create either pageAct or pam; for object to Become PAM investigation type must be INV_FORM_VAR or INV_FORM_RVCT.
     * This investigation type is ultimately coming from WDS Algo
     * */
    Object autoCreateInvestigation(ObservationContainer observationVO, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException;
    Object transferValuesTOActProxyVO(PageActProxyContainer pageActProxyContainer, PamProxyContainer pamActProxyVO,
                                      Collection<PersonContainer> personVOCollection,
                                      ObservationContainer rootObservationVO,
                                      Collection<Object> entities,
                                      Map<Object, Object> questionIdentifierMap) throws DataProcessingException;
}
