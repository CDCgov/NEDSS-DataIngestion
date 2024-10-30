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
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
