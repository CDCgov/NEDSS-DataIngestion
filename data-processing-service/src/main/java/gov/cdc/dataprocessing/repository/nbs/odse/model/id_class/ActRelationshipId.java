package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActRelationshipId implements Serializable {
  private Long sourceActUid;
  private Long targetActUid;
  private String typeCd;

  // Constructors, equals, and hashCode methods
}
