package gov.cdc.dataingestion.odse.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EDX_activity_detail_log")
public class EdxActivityDetailLog {
    @Id
    @Column(name = "edx_activity_detail_log_uid", nullable = false)
    private Long id;

    @Size(max = 256)
    @Column(name = "record_id", length = 256)
    private String recordId;

    @Size(max = 50)
    @Column(name = "record_type", length = 50)
    private String recordType;

    @Size(max = 250)
    @Column(name = "record_nm", length = 250)
    private String recordNm;

    @Size(max = 50)
    @Column(name = "log_type", length = 50)
    private String logType;

    @Size(max = 2000)
    @Column(name = "log_comment", length = 2000)
    private String logComment;

}