package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "edx_activity_detail_log")
public class EdxActivityDetailLog {

    @Id
    @Column(name = "edx_activity_log_uid")
    private Long edxActivityLogUid;

    @Column(name = "record_id")
    private String recordId;

    @Column(name = "record_type")
    private String recordType;

    @Column(name = "record_name")
    private String recordName;

    @Column(name = "log_type")
    private String logType;

    @Column(name = "comment")
    private String comment;

    @Column(name = "log_type_html")
    private String logTypeHtml;

    @Column(name = "comment_html")
    private String commentHtml;

}
