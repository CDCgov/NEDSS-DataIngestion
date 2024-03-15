package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.UpdatedNotificationDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
public class NotificationVO extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private NotificationDto theNotificationDT = new NotificationDto();
    private UpdatedNotificationDT theUpdatedNotificationDT = null;

    //   private Collection<Object>  theEntityLocatorParticipationDTCollection;
    public Collection<Object> theActivityLocatorParticipationDTCollection;
    public Collection<Object> theActIdDTCollection;

    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theActRelationshipDTCollection;
    public Collection<Object> theParticipationDTCollection;
}
