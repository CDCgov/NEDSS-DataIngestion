package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ParticipationHistId  implements Serializable {
    private Long subjectEntityUid;
    private Long actUid;
    private String typeCd;
    private Integer versionCtrlNbr;
}
