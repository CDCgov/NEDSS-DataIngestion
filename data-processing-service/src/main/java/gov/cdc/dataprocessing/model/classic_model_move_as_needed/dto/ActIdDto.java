package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.RootDtoInterface;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ActIdDto extends AbstractVO implements RootDtoInterface
{
    private Long actUid;
    private Integer actIdSeq;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String assigningAuthorityCd;
    private String assigningAuthorityDescTxt;
    private String durationAmt;
    private String durationUnitCd;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
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
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    // NOTE: Act Hist is also a Entity Type
    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ENTITY;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return actUid;
    }

    public ActIdDto() {

    }

    public ActIdDto(ActId actId) {
        this.actUid = actId.getActUid();
        this.actIdSeq = actId.getActIdSeq();
        this.addReasonCd = actId.getAddReasonCd();
        this.addTime = actId.getAddTime();
        this.addUserId = actId.getAddUserId();
        this.assigningAuthorityCd = actId.getAssigningAuthorityCd();
        this.assigningAuthorityDescTxt = actId.getAssigningAuthorityDescTxt();
        this.durationAmt = actId.getDurationAmt();
        this.durationUnitCd = actId.getDurationUnitCd();
        this.lastChgReasonCd = actId.getLastChgReasonCd();
        this.lastChgTime = actId.getLastChgTime();
        this.lastChgUserId = actId.getLastChgUserId();
        this.recordStatusCd = actId.getRecordStatusCd();
        this.recordStatusTime = actId.getRecordStatusTime();
        this.rootExtensionTxt = actId.getRootExtensionTxt();
        this.statusCd = actId.getStatusCd();
        this.statusTime = actId.getStatusTime();
        this.typeCd = actId.getTypeCd();
        this.typeDescTxt = actId.getTypeDescTxt();
        this.userAffiliationTxt = actId.getUserAffiliationTxt();
        this.validFromTime = actId.getValidFromTime();
        this.validToTime = actId.getValidToTime();
    }
}
