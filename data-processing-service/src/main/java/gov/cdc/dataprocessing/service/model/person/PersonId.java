package gov.cdc.dataprocessing.service.model.person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class PersonId {
    public Long personId; //NOSONAR
    public Long personParentId; //NOSONAR
    public String localId; //NOSONAR

    public Long revisionId; //NOSONAR
    public Long revisionParentId; //NOSONAR
    public String revisionLocalId; //NOSONAR
}
