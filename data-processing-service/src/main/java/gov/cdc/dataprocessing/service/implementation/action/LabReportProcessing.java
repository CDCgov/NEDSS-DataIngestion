package gov.cdc.dataprocessing.service.implementation.action;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class LabReportProcessing implements ILabReportProcessing {
    private final IObservationService observationService;

    public LabReportProcessing(IObservationService observationService) {
        this.observationService = observationService;
    }

    @Transactional
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
