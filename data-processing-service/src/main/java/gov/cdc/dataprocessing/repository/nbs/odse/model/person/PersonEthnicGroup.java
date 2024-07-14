package gov.cdc.dataprocessing.repository.nbs.odse.model.person;

import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonEthnicGroupId;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Person_ethnic_group", schema = "dbo")
@IdClass(PersonEthnicGroupId.class) // Specify the IdClass
@Getter
@Setter
public class PersonEthnicGroup {

    @Column(name = "person_uid", nullable = false)
    private Long personUid;

    @Id
    @Column(name = "ethnic_group_cd", nullable = false, length = 20)
    private String ethnicGroupCd;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "ethnic_group_desc_txt", length = 100)
    private String ethnicGroupDescTxt;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationTxt;

    // Add getters and setters as needed

    public PersonEthnicGroup() {

    }

    public PersonEthnicGroup(PersonEthnicGroupDto personEthnicGroupDto) {
        LocalDateTime currentTime = LocalDateTime.now();
        Timestamp currentTimestamp = Timestamp.valueOf(currentTime);

        if (personEthnicGroupDto.getAddUserId() == null) {
            this.addUserId = AuthUtil.authUser.getAuthUserUid();
        } else {
            this.addUserId = personEthnicGroupDto.getAddUserId();
        }
        this.personUid = personEthnicGroupDto.getPersonUid();
        this.ethnicGroupCd = personEthnicGroupDto.getEthnicGroupCd();
        this.addReasonCd = personEthnicGroupDto.getAddReasonCd();
        this.addTime = currentTimestamp;
        this.ethnicGroupDescTxt = personEthnicGroupDto.getEthnicGroupDescTxt();
        this.lastChgReasonCd = personEthnicGroupDto.getLastChgReasonCd();
        this.lastChgTime = currentTimestamp;
        this.lastChgUserId = personEthnicGroupDto.getLastChgUserId();
        this.recordStatusCd = personEthnicGroupDto.getRecordStatusCd();
        this.recordStatusTime = currentTimestamp;
        this.userAffiliationTxt = personEthnicGroupDto.getUserAffiliationTxt();
    }

}
