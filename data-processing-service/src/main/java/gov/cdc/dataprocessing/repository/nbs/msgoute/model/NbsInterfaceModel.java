package gov.cdc.dataprocessing.repository.nbs.msgoute.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "NBS_interface")
@NoArgsConstructor
@Getter
@Setter
@SuppressWarnings("all")
public class NbsInterfaceModel {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column(name="nbs_interface_uid")
    private Integer nbsInterfaceUid;

    @Column(name = "payload", length = 2048, nullable = false)
    private String payload;

    @Column(name = "imp_exp_ind_cd", length = 8, nullable = true)
    private String impExpIndCd;

    @Column(name = "record_status_cd", length = 100, nullable = true)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "add_time", length = 100, nullable = false)
    private Timestamp addTime;

    @Column(name = "system_nm", length = 50, nullable = true)
    private String systemNm;

    @Column(name = "doc_type_cd", length = 20, nullable = true)
    private String docTypeCd;

    @Column(name = "original_payload", length = 1024, nullable = true)
    private String originalPayload;

    @Column(name = "original_doc_type_cd", length = 100, nullable = true)
    private String originalDocTypeCd;

    @Column(name = "filler_order_nbr", length = 250, nullable = true)
    private String fillerOrderNbr;

    @Column(name = "lab_clia", length = 250, nullable = true)
    private String labClia;

    @Column(name = "specimen_coll_date")
    private Timestamp specimenCollDate;

    @Column(name = "order_test_code", length = 250, nullable = true)
    private String orderTestCode;

    @Column(name = "OBSERVATION_UID")
    private Integer observationUid;
}
