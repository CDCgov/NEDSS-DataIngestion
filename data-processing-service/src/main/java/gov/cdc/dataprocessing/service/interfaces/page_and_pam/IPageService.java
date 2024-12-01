package gov.cdc.dataprocessing.service.interfaces.page_and_pam;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;

public interface IPageService {
    Long setPageProxyWithAutoAssoc(String typeCd, PageActProxyContainer pageProxyVO, Long observationUid,
                                   String observationTypeCd, String processingDecision) throws DataProcessingException;
}
