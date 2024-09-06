package gov.cdc.nbsDedup.model.container.model;

import gov.cdc.nbsDedup.model.NotificationContainer;
import gov.cdc.nbsDedup.model.PublicHealthCaseContainer;
import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class NotificationProxyContainer extends BaseContainer
{
    private static final long serialVersionUID = 1L;
    public Collection<Object> theActRelationshipDTCollection;
    public PublicHealthCaseContainer thePublicHealthCaseContainer;
    public NotificationContainer theNotificationContainer;

}
