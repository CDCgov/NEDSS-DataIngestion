package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@IdClass(ManufacturedMaterialId.class)
public class ManufacturedMaterialId implements Serializable {
    private Long materialUid;

    private Integer manufacturedMaterialSeq;
}
