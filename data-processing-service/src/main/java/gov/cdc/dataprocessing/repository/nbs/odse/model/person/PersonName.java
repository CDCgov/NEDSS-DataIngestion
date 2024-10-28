package gov.cdc.dataprocessing.repository.nbs.odse.model.person;

import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonNameId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Data
@Entity
@IdClass(PersonNameId.class) // Specify the IdClass
@Table(name = "Person_name")
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
public class PersonName {
    @Id
    @Column(name = "person_uid")
    private Long personUid;

    @Id
    @Column(name = "person_name_seq")
    private Integer personNameSeq;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "default_nm_ind")
    private String defaultNmInd;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "first_nm")
    private String firstNm;

    @Column(name = "first_nm_sndx")
    private String firstNmSndx;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "last_nm")
    private String lastNm;

    @Column(name = "last_nm_sndx")
    private String lastNmSndx;

    @Column(name = "last_nm2")
    private String lastNm2;

    @Column(name = "last_nm2_sndx")
    private String lastNm2Sndx;

    @Column(name = "middle_nm")
    private String middleNm;

    @Column(name = "middle_nm2")
    private String middleNm2;

    @Column(name = "nm_degree")
    private String nmDegree;

    @Column(name = "nm_prefix")
    private String nmPrefix;

    @Column(name = "nm_suffix")
    private String nmSuffix;

    @Column(name = "nm_use_cd")
    private String nmUseCd;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "status_cd", nullable = false)
    private String statusCd;

    @Column(name = "status_time", nullable = false)
    private Timestamp statusTime;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    // Constructors, getters, and setters (Lombok-generated)

    // Define relationships, if any, with other entities using JPA annotations

    public PersonName() {

    }
    public PersonName(PersonNameDto personNameDto) {
        var timeStamp = getCurrentTimeStamp();
        this.personUid = personNameDto.getPersonUid();
        this.personNameSeq = personNameDto.getPersonNameSeq();
        this.addReasonCd = personNameDto.getAddReasonCd();
        this.addTime = personNameDto.getAddTime();
        this.addUserId = personNameDto.getAddUserId();
        this.defaultNmInd = personNameDto.getDefaultNmInd();
        this.durationAmt = personNameDto.getDurationAmt();
        this.durationUnitCd = personNameDto.getDurationUnitCd();
        this.firstNm = personNameDto.getFirstNm();
        this.firstNmSndx = personNameDto.getFirstNmSndx();
        this.fromTime = personNameDto.getFromTime();
        this.lastChgReasonCd = personNameDto.getLastChgReasonCd();
        this.lastChgTime = personNameDto.getLastChgTime();
        this.lastChgUserId = personNameDto.getLastChgUserId();
        this.lastNm = personNameDto.getLastNm();
        this.lastNmSndx = personNameDto.getLastNmSndx();
        this.lastNm2 = personNameDto.getLastNm2();
        this.lastNm2Sndx = personNameDto.getLastNm2Sndx();
        this.middleNm = personNameDto.getMiddleNm();
        this.middleNm2 = personNameDto.getMiddleNm2();
        this.nmDegree = personNameDto.getNmDegree();
        this.nmPrefix = personNameDto.getNmPrefix();
        this.nmSuffix = personNameDto.getNmSuffix();
        this.nmUseCd = personNameDto.getNmUseCd();
        this.recordStatusCd = personNameDto.getRecordStatusCd();
        this.recordStatusTime = timeStamp;
        this.statusCd = personNameDto.getStatusCd();
        this.statusTime = personNameDto.getStatusTime();
        this.toTime = personNameDto.getToTime() ;
        this.userAffiliationTxt = personNameDto.getUserAffiliationTxt();

        if (personNameDto.getAsOfDate() == null) {
            this.asOfDate = timeStamp;
        } else {
            this.asOfDate = personNameDto.getAsOfDate();
        }
    }
}
