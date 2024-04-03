package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;

public class CdaPhcProcessor {
    public static void setStandardNBSCaseAnswerVals(PublicHealthCaseDT phcDT,
                                                    NbsCaseAnswerDto nbsCaseAnswerDT) throws DataProcessingException {
        try {
            nbsCaseAnswerDT.setActUid(phcDT.getPublicHealthCaseUid());
            nbsCaseAnswerDT.setAddTime(phcDT.getAddTime());
            nbsCaseAnswerDT.setLastChgTime(phcDT.getLastChgTime());
            nbsCaseAnswerDT.setAddUserId(phcDT.getAddUserId());
            nbsCaseAnswerDT.setLastChgUserId(phcDT.getLastChgUserId());
            nbsCaseAnswerDT
                    .setRecordStatusCd(NEDSSConstant.OPEN_INVESTIGATION);
            if (nbsCaseAnswerDT.getSeqNbr() != null
                    && nbsCaseAnswerDT.getSeqNbr().intValue() < 0)
                nbsCaseAnswerDT.setSeqNbr(0);
            nbsCaseAnswerDT.setRecordStatusTime(phcDT.getRecordStatusTime());
            nbsCaseAnswerDT.setItNew(true);
        } catch (Exception ex) {
            String errorString = "Exception occured while setting standard values for NBS Case Answer DT. "+ex.getMessage();
            ex.printStackTrace();
            throw new DataProcessingException(errorString,ex);
        }
    }

}
