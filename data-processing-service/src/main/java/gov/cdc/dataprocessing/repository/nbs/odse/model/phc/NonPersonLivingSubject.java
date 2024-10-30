package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.NonPersonLivingSubjectDto;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Non_Person_living_subject")
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
public class NonPersonLivingSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "non_person_uid")
    private Long nonPersonUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "birth_sex_cd")
    private String birthSexCd;

    @Column(name = "birth_order_nbr")
    private Integer birthOrderNbr;

    @Column(name = "birth_time")
    private Timestamp birthTime;

    @Column(name = "breed_cd")
    private String breedCd;

    @Column(name = "breed_desc_txt")
    private String breedDescTxt;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "deceased_ind_cd")
    private String deceasedIndCd;

    @Column(name = "deceased_time")
    private Timestamp deceasedTime;

    @Column(name = "description")
    private String description;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "multiple_birth_ind")
    private String multipleBirthInd;

    @Column(name = "nm")
    private String nm;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "taxonomic_classification_cd")
    private String taxonomicClassificationCd;

    @Column(name = "taxonomic_classification_desc")
    private String taxonomicClassificationDesc;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    public NonPersonLivingSubject() {

    }

    public NonPersonLivingSubject(NonPersonLivingSubjectDto dto) {
        this.nonPersonUid = dto.getNonPersonUid();
        this.addReasonCd = dto.getAddReasonCd();
        this.addTime = dto.getAddTime();
        this.addUserId = dto.getAddUserId();
        this.birthSexCd = dto.getBirthSexCd();
        this.birthOrderNbr = dto.getBirthOrderNbr();
        this.birthTime = dto.getBirthTime();
        this.breedCd = dto.getBreedCd();
        this.breedDescTxt = dto.getBreedDescTxt();
        this.cd = dto.getCd();
        this.cdDescTxt = dto.getCdDescTxt();
        this.deceasedIndCd = dto.getDeceasedIndCd();
        this.deceasedTime = dto.getDeceasedTime();
        this.description = dto.getDescription();
        this.lastChgReasonCd = dto.getLastChgReasonCd();
        this.lastChgTime = dto.getLastChgTime();
        this.lastChgUserId = dto.getLastChgUserId();
        this.localId = dto.getLocalId();
        this.multipleBirthInd = dto.getMultipleBirthInd();
        this.nm = dto.getNm();
        this.recordStatusCd = dto.getRecordStatusCd();
        this.recordStatusTime = dto.getRecordStatusTime();
        this.statusCd = dto.getStatusCd();
        this.statusTime = dto.getStatusTime();
        this.taxonomicClassificationCd = dto.getTaxonomicClassificationCd();
        this.taxonomicClassificationDesc = dto.getTaxonomicClassificationDesc();
        this.userAffiliationTxt = dto.getUserAffiliationTxt();
        this.versionCtrlNbr = dto.getVersionCtrlNbr();
    }

}
