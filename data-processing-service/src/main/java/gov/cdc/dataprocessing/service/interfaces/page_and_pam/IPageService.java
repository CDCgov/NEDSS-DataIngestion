package gov.cdc.dataprocessing.service.interfaces.page_and_pam;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;

public interface IPageService {
    Long setPageProxyWithAutoAssoc(String typeCd, PageActProxyVO pageProxyVO, Long observationUid,
                                          String observationTypeCd, String processingDecision) throws DataProcessingException;
}
