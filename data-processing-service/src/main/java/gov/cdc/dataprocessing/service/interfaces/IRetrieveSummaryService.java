package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;

import java.util.Collection;

public interface IRetrieveSummaryService {
    void checkBeforeCreateAndStoreMessageLogDTCollection(Long investigationUID,
                                                                Collection<LabReportSummaryContainer> reportSumVOCollection);
}
