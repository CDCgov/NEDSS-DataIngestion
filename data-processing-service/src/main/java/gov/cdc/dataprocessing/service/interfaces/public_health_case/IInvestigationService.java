package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;

import java.util.Collection;

public interface IInvestigationService {
    void updateAutoResendNotificationsAsync(BaseContainer v);
    void setAssociations(Long investigationUID,
                         Collection<LabReportSummaryContainer> reportSumVOCollection,
                         Collection<Object>  vaccinationSummaryVOCollection,
                         Collection<Object>  summaryDTColl,
                         Collection<Object> treatmentSumColl,
                         Boolean isNNDResendCheckRequired) throws DataProcessingException;
    PageActProxyContainer getPageProxyVO(String typeCd, Long publicHealthCaseUID) throws DataProcessingException;
    void setObservationAssociationsImpl(Long investigationUID, Collection<LabReportSummaryContainer>  reportSumVOCollection, boolean invFromEvent) throws DataProcessingException;
}
