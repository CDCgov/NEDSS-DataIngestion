package gov.cdc.dataprocessing.service.interfaces.uid_generator;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.NotificationProxyContainer;
import gov.cdc.dataprocessing.model.container.PamProxyContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;

public interface IUidService {
    /**
     * This method checks for the negative uid value for any ACT & ENTITY DT then compare them
     * with respective negative values in ActRelationshipDto and ParticipationDto as received from
     * the investigationProxyVO(determined in the addInvestigation method).
     * As it has also got the actualUID (determined in the addInvestigation method) it replaces them accordingly.
     */
    void setFalseToNewForObservation(BaseContainer proxyVO, Long falseUid, Long actualUid);

    /**
     * This method update uid for items in the following collection
     * Participation collection
     * Act Relationship collection
     * Role collection
     * - This is crucial in Observation Flow
     * */
    void setFalseToNewPersonAndOrganization(LabResultProxyContainer labResultProxyContainer, Long falseUid, Long actualUid) throws DataProcessingException;

    void setFalseToNewForPageAct(PageActProxyVO pageProxyVO, Long falseUid, Long actualUid) throws DataProcessingException;
    void setFalseToNewForPam(PamProxyContainer pamProxyVO, Long falseUid, Long actualUid) throws DataProcessingException;
    ActRelationshipDto setFalseToNewForNotification(NotificationProxyContainer notificationProxyVO, Long falseUid, Long actualUid) throws DataProcessingException;
}
