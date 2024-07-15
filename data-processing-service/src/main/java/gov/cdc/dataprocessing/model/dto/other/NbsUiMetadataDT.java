package gov.cdc.dataprocessing.model.dto.other;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;

import java.sql.Timestamp;

public class NbsUiMetadataDT extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long nbsUiMetadataUid;
    private Long nbsUiComponentUid;
    private Long nbsQuestionUid;
    private Long parentUid;
    private Long nbsPageUid;
    private String questionLabel;
    private String questionToolTip;
    private String investigationFormCd;
    private String enableInd;
    private String defaultValue;
    private String displayInd;
    private Integer orderNbr;
    private String requiredInd;
    private Integer tabOrderId;
    private String tabName;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Long maxLength;
    private String ldfPosition;
    private String cssStyle;
    private String ldfPageId;
    private String ldfStatusCd;
    private Timestamp ldfStatusTime;
    private Integer versionCtrlNbr;
    private String adminComment;
    private String fieldSize;
    private String futureDateInd;
    private Long nbsTableUid;
    private Long codeSetGroupId;
    private String dataCd;
    private String dataLocation;
    private String dataType;
    private String dataUseCd;
    private String legacyDataLocation;
    private String partTypeCd;
    private Integer questionGroupSeqNbr;
    private String questionIdentifier;
    private String questionOid;
    private String questionOidSystemTxt;
    private String questionUnitIdentifier;
    private String repeatsIndCd;
    private String unitParentIdentifier;
    private String groupNm;
    private String subGroupNm;
    private String descTxt;
    private String mask;
    private Long minValue;
    private Long maxValue;
    private String standardNndIndCd;
    private String unitTypeCd;
    private String unitValue;
    private String otherValueIndCd;
    private String coinfectionIndCd;

    /* Batch entry attributes */
    private String batchTableAppearIndCd;
    private String batchTableHeader;
    private Integer batchTableColumnWidth ;
    private String questionWithQuestionIdentifier;
    private String blockName;
    private Integer dataMartRepeatNumber;





    public NbsUiMetadataDT() {}

    public Long getNbsUiMetadataUid() {
        return nbsUiMetadataUid;
    }
    public void setNbsUiMetadataUid(Long nbsUiMetadataUid) {
        this.nbsUiMetadataUid = nbsUiMetadataUid;
    }
    public Long getNbsUiComponentUid() {
        return nbsUiComponentUid;
    }
    public void setNbsUiComponentUid(Long nbsUiComponentUid) {
        this.nbsUiComponentUid = nbsUiComponentUid;
    }
    public Long getNbsQuestionUid() {
        return nbsQuestionUid;
    }
    public void setNbsQuestionUid(Long nbsQuestionUid) {
        this.nbsQuestionUid = nbsQuestionUid;
    }
    public Long getParentUid() {
        return parentUid;
    }
    public void setParentUid(Long parentUid) {
        this.parentUid = parentUid;
    }
    public Long getNbsPageUid() {
        return nbsPageUid;
    }
    public void setNbsPageUid(Long nbsPageUid) {
        this.nbsPageUid = nbsPageUid;
    }
    public String getQuestionLabel() {
        return questionLabel;
    }
    public void setQuestionLabel(String questionLabel) {
        this.questionLabel = questionLabel;
    }
    public String getQuestionToolTip() {
        return questionToolTip;
    }
    public void setQuestionToolTip(String questionToolTip) {
        this.questionToolTip = questionToolTip;
    }
    public String getInvestigationFormCd() {
        return investigationFormCd;
    }
    public void setInvestigationFormCd(String investigationFormCd) {
        this.investigationFormCd = investigationFormCd;
    }
    public String getEnableInd() {
        return enableInd;
    }
    public void setEnableInd(String enableInd) {
        this.enableInd = enableInd;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public String getDisplayInd() {
        return displayInd;
    }
    public void setDisplayInd(String displayInd) {
        this.displayInd = displayInd;
    }
    public Integer getOrderNbr() {
        return orderNbr;
    }
    public void setOrderNbr(Integer orderNbr) {
        this.orderNbr = orderNbr;
    }
    public String getRequiredInd() {
        return requiredInd;
    }
    public void setRequiredInd(String requiredInd) {
        this.requiredInd = requiredInd;
    }
    public String getTabName() {
        return tabName;
    }
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }
    public Timestamp getAddTime() {
        return addTime;
    }
    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }
    public Long getAddUserId() {
        return addUserId;
    }
    public void setAddUserId(Long addUserId) {
        this.addUserId = addUserId;
    }
    public Timestamp getLastChgTime() {
        return lastChgTime;
    }
    public void setLastChgTime(Timestamp lastChgTime) {
        this.lastChgTime = lastChgTime;
    }
    public Long getLastChgUserId() {
        return lastChgUserId;
    }
    public void setLastChgUserId(Long lastChgUserId) {
        this.lastChgUserId = lastChgUserId;
    }
    public String getRecordStatusCd() {
        return recordStatusCd;
    }
    public void setRecordStatusCd(String recordStatusCd) {
        this.recordStatusCd = recordStatusCd;
    }
    public Timestamp getRecordStatusTime() {
        return recordStatusTime;
    }
    public void setRecordStatusTime(Timestamp recordStatusTime) {
        this.recordStatusTime = recordStatusTime;
    }
    public Long getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }
    public String getLdfPosition() {
        return ldfPosition;
    }
    public void setLdfPosition(String ldfPosition) {
        this.ldfPosition = ldfPosition;
    }
    public String getCssStyle() {
        return cssStyle;
    }
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }
    public String getLdfPageId() {
        return ldfPageId;
    }
    public void setLdfPageId(String ldfPageId) {
        this.ldfPageId = ldfPageId;
    }

    public String getJurisdictionCd() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getLastChgReasonCd() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getLocalId() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getProgAreaCd() {
        // TODO Auto-generated method stub
        return null;
    }
    public Long getProgramJurisdictionOid() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getSharedInd() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getStatusCd() {
        // TODO Auto-generated method stub
        return null;
    }
    public Timestamp getStatusTime() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getSuperclass() {
        // TODO Auto-generated method stub
        return null;
    }
    public Long getUid() {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean isItDelete() {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isItDirty() {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isItNew() {
        // TODO Auto-generated method stub
        return false;
    }
    public void setItDelete(boolean itDelete) {
        // TODO Auto-generated method stub

    }
    public void setItDirty(boolean itDirty) {
        // TODO Auto-generated method stub

    }
    public void setItNew(boolean itNew) {
        // TODO Auto-generated method stub

    }
    public void setJurisdictionCd(String jurisdictionCd) {
        // TODO Auto-generated method stub

    }
    public void setLastChgReasonCd(String lastChgReasonCd) {
        // TODO Auto-generated method stub

    }
    public void setLocalId(String localId) {
        // TODO Auto-generated method stub

    }
    public void setProgAreaCd(String progAreaCd) {
        // TODO Auto-generated method stub

    }
    public void setProgramJurisdictionOid(Long programJurisdictionOid) {
        // TODO Auto-generated method stub

    }
    public void setSharedInd(String sharedInd) {
        // TODO Auto-generated method stub

    }

    @Override
    public Integer getVersionCtrlNbr() {
        return versionCtrlNbr;
    }

    public void setStatusCd(String statusCd) {
        // TODO Auto-generated method stub

    }
    public void setStatusTime(Timestamp statusTime) {
        // TODO Auto-generated method stub

    }


}
