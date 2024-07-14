package gov.cdc.dataprocessing.model.dto.other;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
public class NndMetadataDT extends BaseContainer implements RootDtoInterface {
    private Long nndMetadataUid;
    private String investigationFormCd;
    private String questionIdentifierNnd;
    private String questionLabelNnd;
    private String questionRequiredNnd;
    private String questionDataTypeNnd;
    private String HL7SegmentField;
    private String orderGroupId;
    private String translationTableNm;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String questionIdentifier;
    private String msgTriggerIndCd;
    private String xmlPath;
    private String xmlTag;
    private String xmlDataType;
    private String partTypeCd;
    private Integer repeatGroupSeqNbr;
    private Integer questionOrderNnd;
    private Long nbsPageUid;
    private Long nbsUiMetadataUid;
    private String questionMap;
    private String indicatorCd;
    private static final long serialVersionUID = 1L;

    public NndMetadataDT() {}


    @Override
    public String getJurisdictionCd() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getLastChgReasonCd() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getLocalId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getProgAreaCd() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Long getProgramJurisdictionOid() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getSharedInd() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getStatusCd() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Timestamp getStatusTime() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getSuperclass() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Long getUid() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Integer getVersionCtrlNbr() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isItDelete() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isItDirty() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isItNew() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void setItDelete(boolean itDelete) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setItDirty(boolean itDirty) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setItNew(boolean itNew) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setJurisdictionCd(String jurisdictionCd) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setLastChgReasonCd(String lastChgReasonCd) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setLocalId(String localId) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setProgAreaCd(String progAreaCd) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setProgramJurisdictionOid(Long programJurisdictionOid) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setSharedInd(String sharedInd) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setStatusCd(String statusCd) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setStatusTime(Timestamp statusTime) {
        // TODO Auto-generated method stub

    }
}
