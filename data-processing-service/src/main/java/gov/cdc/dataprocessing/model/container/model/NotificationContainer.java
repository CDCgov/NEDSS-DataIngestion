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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
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
