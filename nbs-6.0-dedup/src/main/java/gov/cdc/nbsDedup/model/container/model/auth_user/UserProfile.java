package gov.cdc.nbsDedup.model.container.model.auth_user;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.model.dto.auth_user.RealizedRoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class UserProfile extends BaseContainer {
    private static final long serialVersionUID = 1L;
    public Collection<RealizedRoleDto> theRealizedRoleDtoCollection;
    public User theUser;
}
