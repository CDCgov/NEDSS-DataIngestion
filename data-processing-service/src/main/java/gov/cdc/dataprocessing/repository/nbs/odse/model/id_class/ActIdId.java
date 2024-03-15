package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ActIdId implements Serializable {
    private Long actUid;
    private Integer actIdSeq;
}