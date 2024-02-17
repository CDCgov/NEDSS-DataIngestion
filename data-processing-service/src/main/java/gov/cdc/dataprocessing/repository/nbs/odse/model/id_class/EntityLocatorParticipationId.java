package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EntityLocatorParticipationId implements Serializable {
    private Long entityUid;
    private Long locatorUid;
}
