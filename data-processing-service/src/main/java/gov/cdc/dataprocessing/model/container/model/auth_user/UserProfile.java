package gov.cdc.dataprocessing.model.container.model.auth_user;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.auth_user.RealizedRoleDto;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class UserProfile extends BaseContainer {
    private static final long serialVersionUID = 1L;
    public Collection<RealizedRoleDto> theRealizedRoleDtoCollection;
    public User theUser;
}
