package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ActRelationshipDT extends AbstractVO
{
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Integer sequenceNbr;
    private String statusCd;
    private Timestamp statusTime;
    private Timestamp toTime;

    private String userAffiliationTxt;

    private Long sourceActUid;
    private String typeDescTxt;
    private Long targetActUid;
    private String sourceClassCd;
    private String targetClassCd;
    private String typeCd;
    private boolean isShareInd;
    private boolean isNNDInd;
    private boolean isExportInd;


}
