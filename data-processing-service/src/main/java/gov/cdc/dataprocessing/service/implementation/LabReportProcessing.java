package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.service.interfaces.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LabReportProcessing implements ILabReportProcessing {
    private final IObservationService observationService;

    public LabReportProcessing(IObservationService observationService) {
        this.observationService = observationService;
    }

    @Transactional
    public String markAsReviewedHandler(Long observationUid, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        String markAsReviewedFlag = "";
        try {

            if(edxLabInformationDT.getAssociatedPublicHealthCaseUid()==null || edxLabInformationDT.getAssociatedPublicHealthCaseUid().longValue()<0){
                boolean returnValue = observationService.processObservation(observationUid);
                if (returnValue) {
                    markAsReviewedFlag = "PROCESSED";
                }
                else {
                    markAsReviewedFlag = "UNPROCESSED";
                }
            }else {
                observationService.setLabInvAssociation(observationUid, edxLabInformationDT.getAssociatedPublicHealthCaseUid());
            }
        }catch(Exception ex){
            edxLabInformationDT.setLabIsMarkedAsReviewed(false);
            edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_12);
            throw new DataProcessingException(EdxELRConstant.ELR_MASTER_MSG_ID_12);
        }
        return markAsReviewedFlag;

    }

}
