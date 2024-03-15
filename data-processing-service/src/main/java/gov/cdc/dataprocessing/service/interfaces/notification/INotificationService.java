package gov.cdc.dataprocessing.service.interfaces.notification;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.BaseContainer;

public interface INotificationService {
    boolean checkForExistingNotification(BaseContainer vo) throws DataProcessingException;
}
