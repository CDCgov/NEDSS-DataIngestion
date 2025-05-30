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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class NbsInterfaceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nbs_interface_uid")
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

    @Column(name = "original_payload_RR", nullable = true)
    private String originalPayloadRR;
    @Column(name = "original_doc_type_cd_RR", nullable = true)
    private String originalDocTypeCdRR;
}
