package gov.cdc.dataprocessing.service.implementation.action;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import org.springframework.stereotype.Service;

@Service

public class LabReportProcessing implements ILabReportProcessing {
    private final IObservationService observationService;

    public LabReportProcessing(IObservationService observationService) {
        this.observationService = observationService;
    }

    public String markAsReviewedHandler(Long observationUid, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        String markAsReviewedFlag = "";
        try {

            if(edxLabInformationDT.getAssociatedPublicHealthCaseUid()==null || edxLabInformationDT.getAssociatedPublicHealthCaseUid() <0){
                boolean returnValue = observationService.processObservation(observationUid);
                if (returnValue) {
                    markAsReviewedFlag = "PROCESSED";
                }
                else {
                    markAsReviewedFlag = "UNPROCESSED";
                }
            }else {
                var investigationUid = edxLabInformationDT.getAssociatedPublicHealthCaseUid();
                observationService.setLabInvAssociation(observationUid, investigationUid);
            }
        }catch(Exception ex){
            edxLabInformationDT.setLabIsMarkedAsReviewed(false);
            edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_12);
            throw new DataProcessingException(EdxELRConstant.ELR_MASTER_MSG_ID_12, ex);
        }
        return markAsReviewedFlag;

    }

}
