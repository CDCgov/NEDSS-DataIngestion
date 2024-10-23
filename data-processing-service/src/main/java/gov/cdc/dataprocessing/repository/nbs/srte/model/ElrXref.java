package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "ELR_XREF", schema = "dbo")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class ElrXref {

    @Id
    @Column(name = "from_code_set_nm", nullable = false, length = 256)
    private String fromCodeSetNm;

    @Column(name = "from_seq_num", nullable = false)
    private Short fromSeqNum;

    @Column(name = "from_code", nullable = false, length = 20)
    private String fromCode;

    @Column(name = "to_code_set_nm", nullable = false, length = 256)
    private String toCodeSetNm;

    @Column(name = "to_seq_num", nullable = false)
    private Short toSeqNum;

    @Column(name = "to_code", nullable = false, length = 20)
    private String toCode;

    @Column(name = "effective_from_time")
    private Date effectiveFromTime;

    @Column(name = "effective_to_time")
    private Date effectiveToTime;

    @Column(name = "status_cd", length = 1)
    private Character statusCd;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "laboratory_id", length = 20)
    private String laboratoryId;

    @Column(name = "nbs_uid")
    private Integer nbsUid;
}
