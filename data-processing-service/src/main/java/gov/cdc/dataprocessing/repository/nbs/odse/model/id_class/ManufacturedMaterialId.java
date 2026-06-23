package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.IdClass;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@IdClass(ManufacturedMaterialId.class)
public class ManufacturedMaterialId implements Serializable {
  private Long materialUid;

  private Integer manufacturedMaterialSeq;
}
