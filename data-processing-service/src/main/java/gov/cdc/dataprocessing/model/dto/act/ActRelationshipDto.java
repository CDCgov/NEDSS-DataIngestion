package gov.cdc.dataprocessing.model.dto.act;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ActRelationshipDto extends BaseContainer
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


    public ActRelationshipDto() {

    }

    public ActRelationshipDto(ActRelationship actRelationship) {
        this.addReasonCd = actRelationship.getAddReasonCd();
        this.addTime = actRelationship.getAddTime();
        this.addUserId = actRelationship.getAddUserId();
        this.durationAmt = actRelationship.getDurationAmt();
        this.durationUnitCd = actRelationship.getDurationUnitCd();
        this.fromTime = actRelationship.getFromTime();
        this.lastChgReasonCd = actRelationship.getLastChgReasonCd();
        this.lastChgTime = actRelationship.getLastChgTime();
        this.lastChgUserId = actRelationship.getLastChgUserId();
        this.recordStatusCd = actRelationship.getRecordStatusCd();
        this.recordStatusTime = actRelationship.getRecordStatusTime();
        this.sequenceNbr = actRelationship.getSequenceNbr();
        this.statusCd = actRelationship.getStatusCd();
        this.statusTime = actRelationship.getStatusTime();
        this.toTime = actRelationship.getToTime();
        this.userAffiliationTxt = actRelationship.getUserAffiliationTxt();
        this.sourceActUid = actRelationship.getSourceActUid();
        this.typeDescTxt = actRelationship.getTypeDescTxt();
        this.targetActUid = actRelationship.getTargetActUid();
        this.sourceClassCd = actRelationship.getSourceClassCd();
        this.targetClassCd = actRelationship.getTargetClassCd();
        this.typeCd = actRelationship.getTypeCd();
    }

}
