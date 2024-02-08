package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.PersonRaceDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "Person_race", schema = "dbo")
@Data
public class PersonRace {

    @Id
    @Column(name = "person_uid", nullable = false)
    private BigInteger personUid;

    @Column(name = "race_cd", nullable = false, length = 20)
    private String raceCd;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private BigInteger addUserId;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private BigInteger lastChgUserId;

    @Column(name = "race_category_cd", length = 20)
    private String raceCategoryCd;

    @Column(name = "race_desc_txt", length = 100)
    private String raceDescTxt;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationTxt;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    // Add getters and setters as needed
    public PersonRace() {

    }
    public PersonRace(PersonRaceDT personRaceDT) {
        this.personUid = BigInteger.valueOf(personRaceDT.getPersonUid());
        this.raceCd = personRaceDT.getRaceCd();
        this.addReasonCd = personRaceDT.getAddReasonCd();
        this.addTime = personRaceDT.getAddTime();
        this.addUserId = BigInteger.valueOf(personRaceDT.getAddUserId());
        this.lastChgReasonCd = personRaceDT.getLastChgReasonCd();
        this.lastChgTime = personRaceDT.getLastChgTime();
        this.lastChgUserId = BigInteger.valueOf(personRaceDT.getLastChgUserId());
        this.raceCategoryCd = personRaceDT.getRaceCategoryCd();
        this.raceDescTxt = personRaceDT.getRaceDescTxt();
        this.recordStatusCd = personRaceDT.getRecordStatusCd();
        this.recordStatusTime = personRaceDT.getRecordStatusTime();
        this.userAffiliationTxt = personRaceDT.getUserAffiliationTxt();
        this.asOfDate = personRaceDT.getAsOfDate();
    }
}
