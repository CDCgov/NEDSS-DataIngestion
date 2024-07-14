package gov.cdc.dataprocessing.service.model.person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class PersonId {
    public Long personId; //NOSONAR
    public Long personParentId; //NOSONAR
    public String localId; //NOSONAR

    public Long revisionId; //NOSONAR
    public Long revisionParentId; //NOSONAR
    public String revisionLocalId; //NOSONAR
}
