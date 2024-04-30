package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.EntityGroupDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class EntityGroupContainer extends BaseContainer{
    public Collection<Object> theEntityLocatorParticipationDTCollection;
    public Collection<Object> theEntityIdDTCollection;
    private EntityGroupDto theEntityGroupDT = new EntityGroupDto();
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;

}
