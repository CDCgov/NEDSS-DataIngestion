package gov.cdc.dataprocessing.service.interfaces.notification;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.NotificationVO;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.NotificationProxyContainer;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;

public interface INotificationService {
    boolean checkForExistingNotification(BaseContainer vo) throws DataProcessingException;
    NotificationDto getNotificationById(Long uid);
    Long saveNotification(NotificationVO notificationVO);
    Long setNotificationProxy(NotificationProxyContainer notificationProxyVO) throws DataProcessingException;
}
