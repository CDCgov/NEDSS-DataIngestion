package gov.cdc.dataprocessing.model.container.model.auth_user;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.auth_user.RealizedRoleDto;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile extends BaseContainer {
  private static final long serialVersionUID = 1L;
  public Collection<RealizedRoleDto> theRealizedRoleDtoCollection;
  public User theUser;
}
