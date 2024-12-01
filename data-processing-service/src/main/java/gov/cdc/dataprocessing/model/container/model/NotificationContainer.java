package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.notification.UpdatedNotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
public class NotificationContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private NotificationDto theNotificationDT = new NotificationDto();
    private UpdatedNotificationDto theUpdatedNotificationDto = null;

    //   private Collection<Object>  theEntityLocatorParticipationDTCollection;
    public Collection<ActivityLocatorParticipationDto> theActivityLocatorParticipationDTCollection;
    public Collection<ActIdDto> theActIdDTCollection;

    //Collections added for Participation and Activity Relationship object association
    public Collection<ActRelationshipDto> theActRelationshipDTCollection;
    public Collection<ParticipationDto> theParticipationDTCollection;
}
