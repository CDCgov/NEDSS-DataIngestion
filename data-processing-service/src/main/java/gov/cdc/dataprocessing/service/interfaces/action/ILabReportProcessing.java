package gov.cdc.dataprocessing.service.interfaces.action;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;

public interface ILabReportProcessing {
    String markAsReviewedHandler(Long observationUid, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException;
}
