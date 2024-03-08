package gov.cdc.dataprocessing.utilities.component;


import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ObservationUtil {
    public static Long getUid(Collection<ParticipationDT> participationDTCollection,
                        Collection<ActRelationshipDT> actRelationshipDTCollection,
                        String uidListType, String uidClassCd, String uidTypeCd,
                        String uidActClassCd, String uidRecordStatusCd) throws DataProcessingException {
        Long anUid = null;
        try {
            if (participationDTCollection != null) {
                for (ParticipationDT partDT : participationDTCollection) {
                    if (
                            (
                                    (
                                            partDT.getSubjectClassCd() != null
                                                    && partDT.getSubjectClassCd().equalsIgnoreCase(uidClassCd)
                                    )
                                            && (partDT.getTypeCd() != null
                                            && partDT.getTypeCd().equalsIgnoreCase(uidTypeCd))
                                            && (partDT.getActClassCd() != null
                                            && partDT.getActClassCd().equalsIgnoreCase(uidActClassCd))
                                            && (partDT.getRecordStatusCd() != null
                                            && partDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd))
                            )
                    )
                    {
                        anUid = partDT.getSubjectEntityUid();
                    }
                }
            }
            else if (actRelationshipDTCollection != null) {
                for (ActRelationshipDT actRelDT : actRelationshipDTCollection) {
                    if (
                            (
                                    actRelDT.getSourceClassCd() != null
                                            && actRelDT.getSourceClassCd().equalsIgnoreCase(uidClassCd)
                            )
                                    && (
                                    actRelDT.getTypeCd() != null
                                            && actRelDT.getTypeCd().equalsIgnoreCase(uidTypeCd)
                            )
                                    && (
                                    actRelDT.getTargetClassCd() != null
                                            && actRelDT.getTargetClassCd().equalsIgnoreCase(uidActClassCd)
                            )
                                    && (
                                    actRelDT.getRecordStatusCd() != null
                                            && actRelDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd)
                            )

                    ) {
                        if (uidListType.equalsIgnoreCase(NEDSSConstant.ACT_UID_LIST_TYPE)) {
                            anUid = actRelDT.getTargetActUid();
                        } else if (uidListType.equalsIgnoreCase(NEDSSConstant.SOURCE_ACT_UID_LIST_TYPE)) {
                            anUid = actRelDT.getSourceActUid();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            throw new DataProcessingException("Error while retrieving a " + uidListType + " uid. " + ex.toString(), ex);
        }

        return anUid;
    }

}
