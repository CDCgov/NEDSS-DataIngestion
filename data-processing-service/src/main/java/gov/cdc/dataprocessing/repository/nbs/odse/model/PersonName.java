package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Person_name")
public class PersonName {

    @Id
    @Column(name = "person_uid")
    private Long personUid;

    @Id
    @Column(name = "person_name_seq")
    private Short personNameSeq;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "default_nm_ind")
    private Character defaultNmInd;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "first_nm")
    private String firstNm;

    @Column(name = "first_nm_sndx")
    private String firstNmSndx;

    @Column(name = "from_time")
    private Date fromTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Date lastChgTime;

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
    private Date recordStatusTime;

    @Column(name = "status_cd", nullable = false)
    private Character statusCd;

    @Column(name = "status_time", nullable = false)
    private Date statusTime;

    @Column(name = "to_time")
    private Date toTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "as_of_date")
    private Date asOfDate;

    // Constructors, getters, and setters (Lombok-generated)

    // Define relationships, if any, with other entities using JPA annotations
}
