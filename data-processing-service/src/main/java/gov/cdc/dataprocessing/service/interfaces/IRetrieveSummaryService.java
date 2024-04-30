package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface IRetrieveSummaryService {
    void checkBeforeCreateAndStoreMessageLogDTCollection(Long investigationUID,
                                                                Collection<LabReportSummaryContainer> reportSumVOCollection);
    Collection<Object>  notificationSummaryOnInvestigation(PublicHealthCaseVO publicHealthCaseVO, Object object) throws DataProcessingException;
    Map<Object,Object> retrieveTreatmentSummaryVOForInv(Long publicHealthUID) throws DataProcessingException;
    Map<Object,Object> retrieveDocumentSummaryVOForInv(Long publicHealthUID) throws DataProcessingException;
    Map<Object,Object> getAssociatedDocumentList(Long uid, String targetClassCd, String sourceClassCd) throws DataProcessingException;
    void updateNotification(Long notificationUid,
                            String businessTriggerCd,
                            String phcCd,
                            String phcClassCd,
                            String progAreaCd,
                            String jurisdictionCd,
                            String sharedInd) throws DataProcessingException;
}
