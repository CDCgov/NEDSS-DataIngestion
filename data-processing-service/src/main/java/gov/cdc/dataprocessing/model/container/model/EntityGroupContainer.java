package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.EntityGroupDto;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityGroupContainer extends BaseContainer {
  public Collection<Object> theEntityLocatorParticipationDTCollection;
  public Collection<Object> theEntityIdDTCollection;
  private EntityGroupDto theEntityGroupDT = new EntityGroupDto();
  public Collection<Object> theParticipationDTCollection;
  public Collection<Object> theRoleDTCollection;
}
