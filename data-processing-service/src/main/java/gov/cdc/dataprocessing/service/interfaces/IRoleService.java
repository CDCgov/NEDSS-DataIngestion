package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.model.dto.entity.RoleDto;

import java.util.Collection;

public interface IRoleService {
    Collection<RoleDto> findRoleScopedToPatient(Long uid);
}
