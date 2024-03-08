package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.*;
import lombok.Data;
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