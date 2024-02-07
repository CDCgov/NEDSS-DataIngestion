package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "Updated_notification")
public class UpdatedNotification {

    @Id
    @Column(name = "notification_uid")
    private Long notificationUid;

    @Column(name = "version_ctrl_nbr", nullable = false)
    private Short versionControlNumber;

    @Column(name = "case_class_cd", length = 20)
    private String caseClassCode;

    @Column(name = "case_status_chg_ind", length = 1)
    private Character caseStatusChangeIndicator;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "last_chg_time")
    private Date lastChangeTime;

    @Column(name = "last_chg_user_id")
    private Long lastChangeUserId;

    @Column(name = "status_cd", length = 1)
    private Character statusCode;

    @Column(name = "status_time")
    private Date statusTime;

    // Constructors, getters, and setters (if needed)

}
