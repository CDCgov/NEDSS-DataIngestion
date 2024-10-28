package gov.cdc.dataprocessing.repository.nbs.srte.model;

import gov.cdc.dataprocessing.repository.nbs.srte.model.id_class.JurisdictionParticipationId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Jurisdiction_participation")
@IdClass(JurisdictionParticipationId.class)
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class JurisdictionParticipation  implements Serializable{
    @Id
    @Column(name = "jurisdiction_cd")
    private String jurisdictionCd;

    @Id
    @Column(name = "fips_cd")
    private String fipsCd;

    @Id
    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;
}
