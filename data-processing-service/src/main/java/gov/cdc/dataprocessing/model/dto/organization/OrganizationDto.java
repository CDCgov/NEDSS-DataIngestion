package gov.cdc.dataprocessing.model.dto.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class OrganizationDto extends BaseContainer implements RootDtoInterface {
    private Long organizationUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String cd;
    private String cdDescTxt;
    private String description;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String standardIndustryClassCd;
    private String standardIndustryDescTxt;
    private String statusCd;
    private Timestamp statusTime;
    private Timestamp toTime;
    private String userAffiliationTxt;
    private String displayNm;
    private String streetAddr1;
    private String streetAddr2;
    private String cityCd;
    private String cityDescTxt;
    private String stateCd;
    private String cntyCd;
    private String cntryCd;
    private String zipCd;
    private String phoneNbr;
    private String phoneCntryCd;
    private String electronicInd;
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;

    private String edxInd = null;

    //NOTE: Org Hist also same type
    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ENTITY;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return organizationUid;
    }

    public OrganizationDto(){
        itDirty = false;
        itNew = true;
        itDelete = false;
    }
    public OrganizationDto(Organization organization){
        this.organizationUid=organization.getOrganizationUid();
        this.addReasonCd=organization.getAddReasonCode();
        this.addTime=organization.getAddTime();
        this.addUserId=organization.getAddUserId();
        this.cd=organization.getCode();
        this.cdDescTxt=organization.getCodeDescTxt();
        this.description=organization.getDescription();
        this.durationAmt=organization.getDurationAmt();
        this.durationUnitCd=organization.getDurationUnitCd();
        this.fromTime=organization.getFromTime();
        this.lastChgReasonCd=organization.getLastChgReasonCd();
        this.lastChgTime=organization.getLastChgTime();
        this.lastChgUserId=organization.getLastChgUserId();
        this.localId=organization.getLocalId();
        this.recordStatusCd=organization.getRecordStatusCd();
        this.recordStatusTime=organization.getRecordStatusTime();
        this.standardIndustryClassCd=organization.getStandardIndustryClassCd();
        this.standardIndustryDescTxt=organization.getStandardIndustryDescTxt();
        this.statusCd=organization.getStatusCd();
        this.statusTime=organization.getStatusTime();
        this.toTime=organization.getToTime();
        this.userAffiliationTxt=organization.getUserAffiliationTxt();
        this.displayNm=organization.getDisplayNm();
        this.streetAddr1=organization.getStreetAddr1();
        this.streetAddr2=organization.getStreetAddr2();
        this.cityCd=organization.getCityCd();
        this.cityDescTxt=organization.getCityDescTxt();
        this.stateCd=organization.getStateCd();
        this.cntyCd=organization.getCntyCd();
        this.cntryCd=organization.getCntryCd();
        this.zipCd=organization.getZipCd();
        this.phoneNbr=organization.getPhoneNbr();
        this.phoneCntryCd=organization.getPhoneCntryCd();
        this.electronicInd=organization.getElectronicInd();
        this.versionCtrlNbr=organization.getVersionCtrlNbr();
    }
}