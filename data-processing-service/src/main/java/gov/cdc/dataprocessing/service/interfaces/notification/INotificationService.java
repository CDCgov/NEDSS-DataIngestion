package gov.cdc.dataprocessing.service.interfaces.notification;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationProxyContainer;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;

public interface INotificationService {
    boolean checkForExistingNotification(BaseContainer vo) throws DataProcessingException;
    NotificationDto getNotificationById(Long uid);
    Long saveNotification(NotificationContainer notificationContainer);
    Long setNotificationProxy(NotificationProxyContainer notificationProxyVO) throws DataProcessingException;
}
