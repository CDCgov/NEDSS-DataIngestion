package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.PersonEthnicGroupDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonEthnicGroupId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonNameId;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "Person_ethnic_group", schema = "dbo")
@IdClass(PersonEthnicGroupId.class) // Specify the IdClass
@Data
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
    public PersonEthnicGroup(PersonEthnicGroupDT personEthnicGroupDT) {
        this.personUid = personEthnicGroupDT.getPersonUid();
        this.ethnicGroupCd = personEthnicGroupDT.getEthnicGroupCd();
        this.addReasonCd = personEthnicGroupDT.getAddReasonCd();
        this.addTime = personEthnicGroupDT.getAddTime();
        this.addUserId = personEthnicGroupDT.getAddUserId();
        this.ethnicGroupDescTxt = personEthnicGroupDT.getEthnicGroupDescTxt();
        this.lastChgReasonCd = personEthnicGroupDT.getLastChgReasonCd();
        this.lastChgTime = personEthnicGroupDT.getLastChgTime();
        this.lastChgUserId = personEthnicGroupDT.getLastChgUserId();
        this.recordStatusCd = personEthnicGroupDT.getRecordStatusCd();
        this.recordStatusTime = personEthnicGroupDT.getRecordStatusTime();
        this.userAffiliationTxt = personEthnicGroupDT.getUserAffiliationTxt();
    }

}
