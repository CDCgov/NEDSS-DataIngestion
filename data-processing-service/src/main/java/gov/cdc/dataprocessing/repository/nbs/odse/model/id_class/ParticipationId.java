package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationId implements Serializable {
  private Long subjectEntityUid;
  private Long actUid;
  private String typeCode;
}
