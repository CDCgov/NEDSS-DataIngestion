package gov.cdc.dataprocessing.model.container.model.auth_user;


import gov.cdc.dataprocessing.model.dto.auth_user.RealizedRoleDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserProfileTest {

    @Test
    void testGettersAndSetters() {
        UserProfile userProfile = new UserProfile();

        // Test inherited boolean fields from BaseContainer
        userProfile.setItNew(true);
        userProfile.setItOld(true);
        userProfile.setItDirty(true);
        userProfile.setItDelete(true);

        assertTrue(userProfile.isItNew());
        assertTrue(userProfile.isItOld());
        assertTrue(userProfile.isItDirty());
        assertTrue(userProfile.isItDelete());

        // Test inherited String field from BaseContainer
        String superClassType = "TestSuperClass";
        userProfile.setSuperClassType(superClassType);
        assertEquals(superClassType, userProfile.getSuperClassType());

        // Test inherited Collection field from BaseContainer
        Collection<Object> ldfs = new ArrayList<>();
        ldfs.add("TestObject");
        userProfile.setLdfs(ldfs);
        assertEquals(ldfs, userProfile.getLdfs());

        // Test UserProfile specific fields
        Collection<RealizedRoleDto> realizedRoleDtoCollection = new ArrayList<>();
        realizedRoleDtoCollection.add(new RealizedRoleDto());
        userProfile.setTheRealizedRoleDtoCollection(realizedRoleDtoCollection);
        assertEquals(realizedRoleDtoCollection, userProfile.getTheRealizedRoleDtoCollection());

        User user = new User();
        userProfile.setTheUser(user);
        assertEquals(user, userProfile.getTheUser());
    }
}