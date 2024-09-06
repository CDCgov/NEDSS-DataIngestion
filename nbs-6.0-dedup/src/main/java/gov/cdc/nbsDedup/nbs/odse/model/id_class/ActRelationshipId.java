package gov.cdc.nbsDedup.nbs.odse.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ActRelationshipId implements Serializable {
    private Long sourceActUid;
    private Long targetActUid;
    private String typeCd;

    // Constructors, equals, and hashCode methods
}
