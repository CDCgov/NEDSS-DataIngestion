package gov.cdc.dataprocessing.repository.nbs.odse.model.person;

import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonEthnicGroupId;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Person_ethnic_group", schema = "dbo")
@IdClass(PersonEthnicGroupId.class) // Specify the IdClass
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
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
        this.lastChgTime =  currentTimestamp;
        this.lastChgUserId = personEthnicGroupDto.getLastChgUserId();
        this.recordStatusCd = personEthnicGroupDto.getRecordStatusCd();
        this.recordStatusTime =  currentTimestamp;
        this.userAffiliationTxt = personEthnicGroupDto.getUserAffiliationTxt();
    }

}
