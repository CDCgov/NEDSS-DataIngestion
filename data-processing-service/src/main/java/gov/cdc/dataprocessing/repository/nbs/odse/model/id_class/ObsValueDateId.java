package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter

public class ObsValueDateId implements Serializable {
    private Long observationUid;
    private Integer obsValueDateSeq;
}
