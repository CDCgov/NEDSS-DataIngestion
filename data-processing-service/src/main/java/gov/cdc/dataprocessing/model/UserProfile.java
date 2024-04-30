package gov.cdc.dataprocessing.model;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class UserProfile extends BaseContainer {
    private static final long serialVersionUID = 1L;
    public Collection<RealizedRole> theRealizedRoleCollection;
    public User theUser;
}
