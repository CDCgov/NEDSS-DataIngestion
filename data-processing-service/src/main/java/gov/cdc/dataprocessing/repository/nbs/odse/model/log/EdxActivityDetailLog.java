package gov.cdc.dataprocessing.repository.nbs.odse.model.log;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EDX_activity_detail_log", schema = "dbo")
public class EdxActivityDetailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edx_activity_detail_log_uid", nullable = false)
    private Long id;

//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "edx_activity_log_uid", nullable = false)
//    private EdxActivityLog edxActivityLogUid;

    @Column(name = "record_id", length = 256)
    private String recordId;

    @Column(name = "record_type", length = 50)
    private String recordType;

    @Column(name = "record_nm", length = 250)
    private String recordNm;

    @Column(name = "log_type", length = 50)
    private String logType;

    @Column(name = "log_comment", length = 2000)
    private String logComment;
}