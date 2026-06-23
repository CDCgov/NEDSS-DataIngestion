package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationProxyContainer extends BaseContainer {
  private static final long serialVersionUID = 1L;
  public Collection<Object> theActRelationshipDTCollection;
  public PublicHealthCaseContainer thePublicHealthCaseContainer;
  public NotificationContainer theNotificationContainer;
}
