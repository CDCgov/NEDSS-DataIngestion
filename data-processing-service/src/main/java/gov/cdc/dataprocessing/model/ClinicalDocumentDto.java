package gov.cdc.dataprocessing.model;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.ClinicalDocument;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ClinicalDocumentDto extends BaseContainer implements RootDtoInterface
{
    private static final long serialVersionUID = 1L;
    private Long clinicalDocumentUid;

    private String activityDurationAmt;

    private String activityDurationUnitCd;

    private Timestamp activityFromTime;

    private Timestamp activityToTime;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private String cd;

    private String cdDescTxt;

    private String confidentialityCd;

    private String confidentialityDescTxt;

    private Timestamp copyFromTime;

    private Timestamp copyToTime;

    private String effectiveDurationAmt;

    private String effectiveDurationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String localId;

    private String practiceSettingCd;

    private String practiceSettingDescTxt;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private String statusCd;

    private Timestamp statusTime;

    private String txt;

    private String userAffiliationTxt;

    private Integer versionNbr;

    private Long programJurisdictionOid;

    private String sharedInd;

    private Integer versionCtrlNbr;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    public ClinicalDocumentDto() {

    }

    public ClinicalDocumentDto(ClinicalDocument clinicalDocument) {
        this.clinicalDocumentUid = clinicalDocument.getClinicalDocumentUid();
        this.activityDurationAmt = clinicalDocument.getActivityDurationAmt();
        this.activityDurationUnitCd = clinicalDocument.getActivityDurationUnitCd();
        this.activityFromTime = clinicalDocument.getActivityFromTime();
        this.activityToTime = clinicalDocument.getActivityToTime();
        this.addReasonCd = clinicalDocument.getAddReasonCd();
        this.addTime = clinicalDocument.getAddTime();
        this.addUserId = clinicalDocument.getAddUserId();
        this.cd = clinicalDocument.getCd();
        this.cdDescTxt = clinicalDocument.getCdDescTxt();
        this.confidentialityCd = clinicalDocument.getConfidentialityCd();
        this.confidentialityDescTxt = clinicalDocument.getConfidentialityDescTxt();
        this.copyFromTime = clinicalDocument.getCopyFromTime();
        this.copyToTime = clinicalDocument.getCopyToTime();
        this.effectiveDurationAmt = clinicalDocument.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = clinicalDocument.getEffectiveDurationUnitCd();
        this.effectiveFromTime = clinicalDocument.getEffectiveFromTime();
        this.effectiveToTime = clinicalDocument.getEffectiveToTime();
        this.lastChgReasonCd = clinicalDocument.getLastChgReasonCd();
        this.lastChgTime = clinicalDocument.getLastChgTime();
        this.lastChgUserId = clinicalDocument.getLastChgUserId();
        this.localId = clinicalDocument.getLocalId();
        this.practiceSettingCd = clinicalDocument.getPracticeSettingCd();
        this.practiceSettingDescTxt = clinicalDocument.getPracticeSettingDescTxt();
        this.recordStatusCd = clinicalDocument.getRecordStatusCd();
        this.recordStatusTime = clinicalDocument.getRecordStatusTime();
        this.statusCd = clinicalDocument.getStatusCd();
        this.statusTime = clinicalDocument.getStatusTime();
        this.txt = clinicalDocument.getTxt();
        this.userAffiliationTxt = clinicalDocument.getUserAffiliationTxt();
        this.versionNbr = clinicalDocument.getVersionNbr();
        this.programJurisdictionOid = clinicalDocument.getProgramJurisdictionOid();
        this.sharedInd = clinicalDocument.getSharedInd();
        this.versionCtrlNbr = clinicalDocument.getVersionCtrlNbr();
    }


    @Override
    public String getSuperclass() {
        return null;
    }

    @Override
    public Long getUid() {
        return null;
    }
}
