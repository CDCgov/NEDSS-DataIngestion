package gov.cdc.dataprocessing.service.model.person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonId {
    public Long personId;
    public Long personParentId;
    public String localId;

    public Long revisionId;
    public Long revisionParentId;
    public String revisionLocalId;
}
