package gov.cdc.dataprocessing.repository.nbs.odse.model.person;

import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonRaceId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Entity
@Table(name = "Person_race", schema = "dbo")
@IdClass(PersonRaceId.class) // Specify the IdClass
@Data
public class PersonRace {

    @Id
    @Column(name = "person_uid", nullable = false)
    private Long personUid;

    @Id
    @Column(name = "race_cd", nullable = false, length = 20)
    private String raceCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_uid", nullable = false, insertable = false, updatable = false)
    private Person person;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

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
    public PersonRace(PersonRaceDto personRaceDto, String tz) {
        var timestamp = getCurrentTimeStamp(tz);
        this.personUid = personRaceDto.getPersonUid();
        this.raceCd = personRaceDto.getRaceCd();
        this.addReasonCd = personRaceDto.getAddReasonCd();
        this.addTime = personRaceDto.getAddTime();
        this.addUserId = personRaceDto.getAddUserId();
        this.lastChgReasonCd = personRaceDto.getLastChgReasonCd();
        if (personRaceDto.getLastChgTime() == null) {
            this.lastChgTime = timestamp;
        } else {
            this.lastChgTime = personRaceDto.getLastChgTime();
        }
        this.lastChgUserId = personRaceDto.getLastChgUserId();
        this.raceCategoryCd = personRaceDto.getRaceCategoryCd();
        this.raceDescTxt = personRaceDto.getRaceDescTxt();
        this.recordStatusCd = personRaceDto.getRecordStatusCd();
        this.recordStatusTime = personRaceDto.getRecordStatusTime();
        this.userAffiliationTxt = personRaceDto.getUserAffiliationTxt();
        this.asOfDate = personRaceDto.getAsOfDate();
    }
}
