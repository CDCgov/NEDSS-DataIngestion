package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NotificationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.UpdatedNotificationDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
public class NotificationVO extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private NotificationDT theNotificationDT = new NotificationDT();
    private UpdatedNotificationDT theUpdatedNotificationDT = null;

    //   private Collection<Object>  theEntityLocatorParticipationDTCollection;
    public Collection<Object> theActivityLocatorParticipationDTCollection;
    public Collection<Object> theActIdDTCollection;

    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theActRelationshipDTCollection;
    public Collection<Object> theParticipationDTCollection;
}