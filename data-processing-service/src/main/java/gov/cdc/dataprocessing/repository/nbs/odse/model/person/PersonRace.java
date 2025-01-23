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
public class PersonRace {

    @Id
    @Column(name = "person_uid", nullable = false)
    private Long personUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_uid", nullable = false)
    private Person person;

    @Id
    @Column(name = "race_cd", nullable = false, length = 20)
    private String raceCd;

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
