package gov.cdc.dataprocessing.model.dto.act;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
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
