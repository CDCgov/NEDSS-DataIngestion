package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@SuppressWarnings("all")
public class NotificationProxyContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    public Collection<Object> theActRelationshipDTCollection;
    public PublicHealthCaseContainer thePublicHealthCaseContainer;
    public NotificationContainer theNotificationContainer;

}
