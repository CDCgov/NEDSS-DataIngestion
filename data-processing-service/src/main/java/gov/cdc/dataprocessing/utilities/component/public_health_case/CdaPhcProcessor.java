package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.kafka.consumer.KafkaHandleLabConsumer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class CdaPhcProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CdaPhcProcessor.class); //NOSONAR

    public static void setStandardNBSCaseAnswerVals(PublicHealthCaseDto phcDT,
                                                    NbsCaseAnswerDto nbsCaseAnswerDT) throws DataProcessingException {
        try {
            nbsCaseAnswerDT.setActUid(phcDT.getPublicHealthCaseUid());
            nbsCaseAnswerDT.setAddTime(phcDT.getAddTime());
            nbsCaseAnswerDT.setLastChgTime(phcDT.getLastChgTime());
            nbsCaseAnswerDT.setAddUserId(phcDT.getAddUserId());
            nbsCaseAnswerDT.setLastChgUserId(phcDT.getLastChgUserId());
            nbsCaseAnswerDT.setRecordStatusCd(NEDSSConstant.OPEN_INVESTIGATION);
            if (nbsCaseAnswerDT.getSeqNbr() != null && nbsCaseAnswerDT.getSeqNbr() < 0)
            {
                nbsCaseAnswerDT.setSeqNbr(0);
            }
            nbsCaseAnswerDT.setRecordStatusTime(phcDT.getRecordStatusTime());
            nbsCaseAnswerDT.setItNew(true);
        } catch (Exception ex) {
            String errorString = "Exception occured while setting standard values for NBS Case Answer DT. "+ex.getMessage();
            logger.info(ex.getMessage()); // NOSONAR
            throw new DataProcessingException(errorString,ex);
        }
    }

}
