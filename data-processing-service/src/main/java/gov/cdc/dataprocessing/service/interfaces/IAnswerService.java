package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageVO;

public interface IAnswerService {
    PageVO getNbsAnswerAndAssociation(Long uid) throws DataProcessingException;
    void storePageAnswer(PageVO pageVO, ObservationDT observationDT) throws DataProcessingException;
    void insertPageVO(PageVO pageVO,ObservationDT rootDTInterface) throws DataProcessingException;
}
