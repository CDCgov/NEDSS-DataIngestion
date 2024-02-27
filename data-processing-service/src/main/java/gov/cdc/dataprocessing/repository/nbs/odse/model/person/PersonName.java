package gov.cdc.dataprocessing.repository.nbs.odse.model.person;

import gov.cdc.dataprocessing.model.classic_model.dto.PersonNameDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonNameId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Data
@Entity
@IdClass(PersonNameId.class) // Specify the IdClass
@Table(name = "Person_name")
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
    public PersonName(PersonNameDT personNameDT) {
        var timeStamp = getCurrentTimeStamp();
        this.personUid = personNameDT.getPersonUid();
        this.personNameSeq = personNameDT.getPersonNameSeq();
        this.addReasonCd = personNameDT.getAddReasonCd();
        this.addTime = personNameDT.getAddTime();
        this.addUserId = personNameDT.getAddUserId();
        this.defaultNmInd = personNameDT.getDefaultNmInd();
        this.durationAmt = personNameDT.getDurationAmt();
        this.durationUnitCd = personNameDT.getDurationUnitCd();
        this.firstNm = personNameDT.getFirstNm();
        this.firstNmSndx = personNameDT.getFirstNmSndx();
        this.fromTime = personNameDT.getFromTime();
        this.lastChgReasonCd = personNameDT.getLastChgReasonCd();
        this.lastChgTime = personNameDT.getLastChgTime();
        this.lastChgUserId = personNameDT.getLastChgUserId();
        this.lastNm = personNameDT.getLastNm();
        this.lastNmSndx = personNameDT.getLastNmSndx();
        this.lastNm2 = personNameDT.getLastNm2();
        this.lastNm2Sndx = personNameDT.getLastNm2Sndx();
        this.middleNm = personNameDT.getMiddleNm();
        this.middleNm2 = personNameDT.getMiddleNm2();
        this.nmDegree = personNameDT.getNmDegree();
        this.nmPrefix = personNameDT.getNmPrefix();
        this.nmSuffix = personNameDT.getNmSuffix();
        this.nmUseCd = personNameDT.getNmUseCd();
        this.recordStatusCd = personNameDT.getRecordStatusCd();
        this.recordStatusTime = timeStamp;
        this.statusCd = personNameDT.getStatusCd();
        this.statusTime = personNameDT.getStatusTime();
        this.toTime = personNameDT.getToTime() ;
        this.userAffiliationTxt = personNameDT.getUserAffiliationTxt();

        if (personNameDT.getAsOfDate() == null) {
            this.asOfDate = timeStamp;
        } else {
            this.asOfDate = personNameDT.getAsOfDate();
        }
    }
}
