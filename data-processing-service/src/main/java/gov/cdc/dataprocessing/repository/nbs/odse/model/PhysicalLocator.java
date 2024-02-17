package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.PhysicalLocatorDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;

@Entity
@Table(name = "Physical_locator", schema = "dbo")
@Data
public class PhysicalLocator {

    @Id
    @Column(name = "physical_locator_uid", nullable = false)
    private Long physicalLocatorUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "image_txt")
    private String imageTxt;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "locator_txt")
    private String locatorTxt;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    // Add getters and setters as needed
    public PhysicalLocator(PhysicalLocatorDT physicalLocatorDT) {
        this.physicalLocatorUid = physicalLocatorDT.getPhysicalLocatorUid();
        this.addReasonCd = physicalLocatorDT.getAddReasonCd();
        this.addTime = physicalLocatorDT.getAddTime();
        this.addUserId = physicalLocatorDT.getAddUserId();
        this.imageTxt = Arrays.toString(physicalLocatorDT.getImageTxt());
        this.lastChgReasonCd = physicalLocatorDT.getLastChgReasonCd();
        this.lastChgTime = physicalLocatorDT.getLastChgTime();
        this.lastChgUserId = physicalLocatorDT.getLastChgUserId();
        this.locatorTxt = physicalLocatorDT.getLocatorTxt();
        this.recordStatusCd = physicalLocatorDT.getRecordStatusCd();
        this.recordStatusTime = physicalLocatorDT.getRecordStatusTime();
        this.userAffiliationTxt = physicalLocatorDT.getUserAffiliationTxt();
    }

    public PhysicalLocator() {
        // Default constructor
    }

}
