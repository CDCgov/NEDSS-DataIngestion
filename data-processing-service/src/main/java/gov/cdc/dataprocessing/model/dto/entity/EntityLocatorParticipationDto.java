package gov.cdc.dataprocessing.model.dto.entity;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
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
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class EntityLocatorParticipationDto extends BaseContainer {

    private Long locatorUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp asOfDate;
    private String cd;
    private String cdDescTxt;
    private String classCd;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String locatorDescTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private Timestamp toTime;
    private String useCd;
    private String userAffiliationTxt;
    private String validTimeTxt;
    private Long entityUid;
    private PostalLocatorDto thePostalLocatorDto;
    private PhysicalLocatorDto thePhysicalLocatorDto;
    private TeleLocatorDto theTeleLocatorDto;
    private Integer versionCtrlNbr;

    public EntityLocatorParticipationDto() {

    }

    public EntityLocatorParticipationDto(EntityLocatorParticipation entityLocatorParticipation) {
        this.entityUid = entityLocatorParticipation.getEntityUid();
        this.locatorUid = entityLocatorParticipation.getLocatorUid();
        this.addReasonCd = entityLocatorParticipation.getAddReasonCd();
        this.addTime = entityLocatorParticipation.getAddTime();
        this.addUserId = entityLocatorParticipation.getAddUserId();
        this.cd = entityLocatorParticipation.getCd();
        this.cdDescTxt = entityLocatorParticipation.getCdDescTxt();
        this.classCd = entityLocatorParticipation.getClassCd();
        this.durationAmt = entityLocatorParticipation.getDurationAmt();
        this.durationUnitCd = entityLocatorParticipation.getDurationUnitCd();
        this.fromTime = entityLocatorParticipation.getFromTime();
        this.lastChgReasonCd = entityLocatorParticipation.getLastChgReasonCd();
        this.lastChgTime = entityLocatorParticipation.getLastChgTime();
        this.lastChgUserId = entityLocatorParticipation.getLastChgUserId();
        this.locatorDescTxt = entityLocatorParticipation.getLocatorDescTxt();
        this.recordStatusCd = entityLocatorParticipation.getRecordStatusCd();
        this.recordStatusTime = entityLocatorParticipation.getRecordStatusTime();
        this.statusCd = entityLocatorParticipation.getStatusCd();
        this.statusTime = entityLocatorParticipation.getStatusTime();
        this.toTime = entityLocatorParticipation.getToTime();
        this.useCd = entityLocatorParticipation.getUseCd();
        this.userAffiliationTxt = entityLocatorParticipation.getUserAffiliationTxt();
        this.validTimeTxt = entityLocatorParticipation.getValidTimeTxt();
        this.versionCtrlNbr = entityLocatorParticipation.getVersionCtrlNbr();
        this.asOfDate = entityLocatorParticipation.getAsOfDate();
    }

}
