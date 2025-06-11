package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CdaPhcProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CdaPhcProcessor.class); //NOSONAR

    public static void setStandardNBSCaseAnswerVals(PublicHealthCaseDto phcDT,
                                                    NbsCaseAnswerDto nbsCaseAnswerDT)   {
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

    }

}
