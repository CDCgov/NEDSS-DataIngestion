package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@IdClass(ManufacturedMaterialId.class)
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class ManufacturedMaterialId implements Serializable {
    private Long materialUid;

    private Integer manufacturedMaterialSeq;
}
