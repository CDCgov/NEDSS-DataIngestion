package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObsValueCodedId implements Serializable {
  private Long observationUid;
  private String code;
}
