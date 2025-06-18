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
