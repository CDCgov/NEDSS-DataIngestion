package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;

import java.util.Collection;
import java.util.Map;


public interface IRetrieveSummaryService {
    Collection<Object>  notificationSummaryOnInvestigation(PublicHealthCaseContainer publicHealthCaseContainer, Object object) throws DataProcessingException;
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
