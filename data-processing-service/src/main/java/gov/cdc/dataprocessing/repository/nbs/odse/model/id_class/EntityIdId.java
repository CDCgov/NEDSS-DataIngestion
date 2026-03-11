package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityIdId implements Serializable {
  private Long entityUid;
  private Integer entityIdSeq;
}
