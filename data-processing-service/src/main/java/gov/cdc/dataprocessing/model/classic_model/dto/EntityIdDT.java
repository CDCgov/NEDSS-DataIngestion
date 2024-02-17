package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityId;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EntityIdDT extends AbstractVO {
    private Long entityUid;

    private Integer entityIdSeq;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private Timestamp asOfDate;

    private String assigningAuthorityCd;

    private String assigningAuthorityDescTxt;

    private String durationAmt;

    private String durationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private String rootExtensionTxt;

    private String statusCd;

    private Timestamp statusTime;

    private String typeCd;

    private String typeDescTxt;

    private String userAffiliationTxt;

    private Timestamp validFromTime;

    private Timestamp validToTime;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;
    //   private String universalIdType;

    private String assigningAuthorityIdType;

    public EntityIdDT() {

    }

    public EntityIdDT(EntityId entityId) {
        this.entityUid = entityId.getEntityUid();
        this.entityIdSeq = entityId.getEntityIdSeq();
        this.addReasonCd = entityId.getAddReasonCode();
        this.addTime = entityId.getAddTime();
        this.addUserId = entityId.getAddUserId();
        this.assigningAuthorityCd = entityId.getAssigningAuthorityCode();
        this.assigningAuthorityDescTxt = entityId.getAssigningAuthorityDescription();
        this.durationAmt = entityId.getDurationAmount();
        this.durationUnitCd = entityId.getDurationUnitCode();
        this.effectiveFromTime = entityId.getEffectiveFromTime();
        this.effectiveToTime = entityId.getEffectiveToTime();
        this.lastChgReasonCd = entityId.getLastChangeReasonCode();
        this.lastChgTime = entityId.getLastChangeTime();
        this.lastChgUserId = entityId.getLastChangeUserId();
        this.recordStatusCd = entityId.getRecordStatusCode();
        this.recordStatusTime = entityId.getRecordStatusTime();
        this.rootExtensionTxt = entityId.getRootExtensionText();
        this.statusCd = entityId.getStatusCode();
        this.statusTime = entityId.getStatusTime();
        this.typeCd = entityId.getTypeCode();
        this.typeDescTxt = entityId.getTypeDescriptionText();
        this.userAffiliationTxt = entityId.getUserAffiliationText();
        this.validFromTime = entityId.getValidFromTime();
        this.validToTime = entityId.getValidToTime();
        this.asOfDate = entityId.getAsOfDate();
        this.assigningAuthorityIdType = entityId.getAssigningAuthorityIdType();
    }

}
