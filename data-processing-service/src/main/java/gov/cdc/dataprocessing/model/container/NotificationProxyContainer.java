package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.NotificationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class NotificationProxyContainer extends BaseContainer
{
    private static final long serialVersionUID = 1L;
    public Collection<Object> theActRelationshipDTCollection;
    public PublicHealthCaseVO thePublicHealthCaseVO;
    public NotificationVO theNotificationVO;

}
