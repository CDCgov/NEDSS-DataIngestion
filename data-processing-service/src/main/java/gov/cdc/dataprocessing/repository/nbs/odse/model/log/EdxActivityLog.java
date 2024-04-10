package gov.cdc.dataprocessing.repository.nbs.odse.model.log;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "EDX_activity_log", schema = "dbo")
public class EdxActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edx_activity_log_uid", nullable = false)
    private Long id;

    @Column(name = "source_uid")
    private Long sourceUid;

    @Column(name = "target_uid")
    private Long targetUid;

    @Column(name = "doc_type", length = 50)
    private String docType;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Instant recordStatusTime;

    @Lob
    @Column(name = "exception_txt")
    private String exceptionTxt;

    @Column(name = "imp_exp_ind_cd")
    private Character impExpIndCd;

    @Column(name = "source_type_cd", length = 50)
    private String sourceTypeCd;

    @Column(name = "target_type_cd", length = 50)
    private String targetTypeCd;

    @Column(name = "business_obj_localId", length = 50)
    private String businessObjLocalid;

    @Column(name = "doc_nm", length = 250)
    private String docNm;

    @Column(name = "source_nm", length = 250)
    private String sourceNm;

    @Column(name = "algorithm_action", length = 10)
    private String algorithmAction;

    @Column(name = "algorithm_name", length = 250)
    private String algorithmName;

    @Column(name = "Message_id")
    private String messageId;

    @Column(name = "Entity_nm")
    private String entityNm;

    @Column(name = "Accession_nbr", length = 100)
    private String accessionNbr;

//    @OneToMany(mappedBy = "edxActivityLogUid")
//    private Set<gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog> edxActivityDetailLogs = new LinkedHashSet<>();

}