package gov.cdc.dataprocessing.service.interfaces.role;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;

import java.util.Collection;

public interface IRoleService {
    Collection<RoleDto> findRoleScopedToPatient(Long uid);
    void saveRole(RoleDto roleDto);
    void storeRoleDTCollection(Collection<RoleDto> roleDTColl) throws DataProcessingException;
}
