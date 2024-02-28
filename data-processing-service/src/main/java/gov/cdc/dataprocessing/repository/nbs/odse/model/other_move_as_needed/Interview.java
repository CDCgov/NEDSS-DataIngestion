package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Interview")
public class Interview  {

    @Id
    @Column(name = "interview_uid")
    private Long interviewUid;

    @Column(name = "interviewee_role_cd")
    private String intervieweeRoleCd;

    @Column(name = "interview_date")
    private Timestamp interviewDate;

    @Column(name = "interview_type_cd")
    private String interviewTypeCd;

    @Column(name = "interview_status_cd")
    private String interviewStatusCd;

    @Column(name = "interview_loc_cd")
    private String interviewLocCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;
}
