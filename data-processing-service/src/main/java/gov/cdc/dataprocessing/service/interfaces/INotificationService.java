package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;

public interface INotificationService {
    boolean checkForExistingNotification(AbstractVO vo) throws DataProcessingException;
}
