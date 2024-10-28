package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.EntityGroupDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class EntityGroupContainer extends BaseContainer {
    public Collection<Object> theEntityLocatorParticipationDTCollection;
    public Collection<Object> theEntityIdDTCollection;
    private EntityGroupDto theEntityGroupDT = new EntityGroupDto();
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;

}
