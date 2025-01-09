package gov.cdc.dataingestion.odse.repository.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "EDX_activity_log")
@Data
public class EdxActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edx_activity_log_uid")
    private Long edxActivityLogUid;

    @Column(name = "source_uid")
    private Long sourceUid;

    @Column(name = "target_uid")
    private Long targetUid;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private String recordStatusTime;

    @Column(name = "exception_txt")
    private String exceptionTxt;

    @Column(name = "imp_exp_ind_cd", columnDefinition = "CHAR(1)")
    private String impExpIndCd;

    @Column(name = "source_type_cd")
    private String sourceTypeCd;

    @Column(name = "target_type_cd")
    private String targetTypeCd;

    @Column(name = "business_obj_localId")
    private String businessObjLocalId;

    @Column(name = "doc_nm")
    private String docNm;

    @Column(name = "source_nm")
    private String sourceNm;

    @Column(name = "algorithm_action")
    private String algorithmAction;

    @Column(name = "algorithm_name")
    private String algorithmName;

    @Column(name = "Message_id")
    private String messageId;

    @Column(name = "Entity_nm")
    private String entityNm;

    @Column(name = "Accession_nbr")
    private String accessionNbr;
}
