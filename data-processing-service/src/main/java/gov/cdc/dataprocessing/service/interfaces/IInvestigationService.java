package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;

import java.util.Collection;

public interface IInvestigationService {
    void updateAutoResendNotificationsAsync(BaseContainer v);
    void setAssociations(Long investigationUID,
                         Collection<LabReportSummaryContainer> reportSumVOCollection,
                         Collection<Object>  vaccinationSummaryVOCollection,
                         Collection<Object>  summaryDTColl,
                         Collection<Object> treatmentSumColl,
                         Boolean isNNDResendCheckRequired) throws DataProcessingException;
}
