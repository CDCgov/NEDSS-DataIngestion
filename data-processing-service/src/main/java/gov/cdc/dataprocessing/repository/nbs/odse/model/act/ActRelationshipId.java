package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ActRelationshipId implements Serializable {

    @Column(name = "source_act_uid")
    private Long sourceActUid;

    @Column(name = "target_act_uid")
    private Long targetActUid;

    @Column(name = "type_cd")
    private String typeCd;

    // Constructors, equals, and hashCode methods
}