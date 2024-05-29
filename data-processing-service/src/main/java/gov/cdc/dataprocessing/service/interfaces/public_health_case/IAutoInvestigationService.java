package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;

import java.util.Collection;
import java.util.Map;

public interface IAutoInvestigationService {
    /**
     * Description: this method create either pageAct or pam; for object to Become PAM investigation type must be INV_FORM_VAR or INV_FORM_RVCT.
     * This investigation type is ultimately coming from WDS Algo
     * */
    Object autoCreateInvestigation(ObservationContainer observationVO, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException;
    Object transferValuesTOActProxyVO(PageActProxyVO pageActProxyVO, PamProxyContainer pamActProxyVO,
                                      Collection<PersonContainer> personVOCollection,
                                      ObservationContainer rootObservationVO,
                                      Collection<Object> entities,
                                      Map<Object, Object> questionIdentifierMap) throws DataProcessingException;
}
