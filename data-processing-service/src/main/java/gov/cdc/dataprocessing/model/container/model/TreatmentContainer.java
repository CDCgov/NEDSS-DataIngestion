package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter

public class TreatmentContainer extends BaseContainer implements RootDtoInterface
{
    public String arTypeCd;

    /**
     * index
     * (Not for display purposes)
     */
    private String index;

    /**
     * from per.person_uid
     * (Not for display purposes)
     */
    private Long personUid;

    /**
     * from set in InvestigationProxyEJB with the isAssociated field
     */
    private String yesNoFlag;


    /**
     * from treatment_administered
     */
    private String treatmentNameCode;

    /**
     * from treatment_administered
     */
    private String customTreatmentNameCode;


    /**
     * from SRT Code Set, based on material.nm
     */
    private String treatmentAdministered;

    /**
     * from Treatment.treatmentUid
     * (Not for display purposes)
     */
    private Long treatmentUid;
    private Long uid;


    /**
     * from Treatment.localId
     */
    private String localId;

    /**
     * from Treatment.activityFromTime
     */
    private Timestamp activityFromTime;

    /**
     * from Treatment.activityToTime
     */
    private Timestamp activityToTime;

    /**
     * from par.record_status_cd,
     */
    private String recordStatusCd;


    /**
     * from ar.target_act_uid PHC_uid
     * (Not for display purposes)
     */
    private Long phcUid;

    /**
     * from ar.target_act_uid parentUid
     * (Not for display purposes)
     */
    private Long parentUid;



    /**
     * MorbReportSummaryVO
     */
    private Collection<Object> morbReportSummaryVOColl;

    /**
     * Populated by front-end to indicate if the isAssociated attribute may have been
     * changed by the user.
     * (not for display purposes)
     */
    private boolean isTouched;

    /**
     * Set by back-end and front-end to indicate if an ActRelationship entry exists or
     * should be created to support associating the Vaccination with the Investigation.
     * (not for display purposes)
     */
    private boolean isAssociated;

    private Character isRadioBtnAssociated;   // same as isAssociated but we need Character to be able to sort.

    private String actionLink;

    private String checkBoxId;

    private Timestamp createDate;

    private Map<Object,Object> associationMap;

    private String providerFirstName = "";

    private Long nbsDocumentUid;


    private String providerLastName = "";
    private String providerSuffix = "";
    private String providerPrefix = "";
    private String degree="" ;

    @Override
    public Long getLastChgUserId() {
        return null;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {

    }

    @Override
    public String getJurisdictionCd() {
        return null;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {

    }

    @Override
    public String getProgAreaCd() {
        return null;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {

    }

    @Override
    public Timestamp getLastChgTime() {
        return null;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {

    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public void setLocalId(String aLocalId) {

    }

    @Override
    public Long getAddUserId() {
        return null;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {

    }

    @Override
    public String getLastChgReasonCd() {
        return null;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {

    }

    @Override
    public String getRecordStatusCd() {
        return null;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {

    }

    @Override
    public Timestamp getRecordStatusTime() {
        return null;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {

    }

    @Override
    public String getStatusCd() {
        return null;
    }

    @Override
    public void setStatusCd(String aStatusCd) {

    }

    @Override
    public Timestamp getStatusTime() {
        return null;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {

    }

    @Override
    public String getSuperclass() {
        return null;
    }

    @Override
    public Long getUid() {
        return null;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {

    }

    @Override
    public Timestamp getAddTime() {
        return null;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return null;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {

    }

    @Override
    public String getSharedInd() {
        return null;
    }

    @Override
    public void setSharedInd(String aSharedInd) {

    }

    @Override
    public Integer getVersionCtrlNbr() {
        return null;
    }
}
