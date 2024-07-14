package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@SuppressWarnings("all")
public class NNDActivityLogId implements Serializable {
    private Long nndActivityLogUid;
    private Integer nndActivityLogSeq;
}