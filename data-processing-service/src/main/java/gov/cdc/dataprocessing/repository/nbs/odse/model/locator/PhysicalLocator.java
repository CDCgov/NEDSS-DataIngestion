package gov.cdc.dataprocessing.repository.nbs.odse.model.locator;

import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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
    public PhysicalLocator(PhysicalLocatorDto physicalLocatorDto) {
        this.physicalLocatorUid = physicalLocatorDto.getPhysicalLocatorUid();
        if (physicalLocatorDto.getAddReasonCd() == null) {
            this.addReasonCd = "Add";
        } else {
            this.addReasonCd = physicalLocatorDto.getAddReasonCd();
        }
        this.addTime = physicalLocatorDto.getAddTime();
        this.addUserId = physicalLocatorDto.getAddUserId();
        this.imageTxt = Arrays.toString(physicalLocatorDto.getImageTxt());
        this.lastChgReasonCd = physicalLocatorDto.getLastChgReasonCd();
        this.lastChgTime = physicalLocatorDto.getLastChgTime();
        this.lastChgUserId = physicalLocatorDto.getLastChgUserId();
        this.locatorTxt = physicalLocatorDto.getLocatorTxt();
        this.recordStatusCd = physicalLocatorDto.getRecordStatusCd();
        this.recordStatusTime = physicalLocatorDto.getRecordStatusTime();
        this.userAffiliationTxt = physicalLocatorDto.getUserAffiliationTxt();
    }

    public PhysicalLocator() {
        // Default constructor
    }

}
