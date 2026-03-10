package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObsValueTxtId implements Serializable {
  private Long observationUid;
  private Integer obsValueTxtSeq;
}
