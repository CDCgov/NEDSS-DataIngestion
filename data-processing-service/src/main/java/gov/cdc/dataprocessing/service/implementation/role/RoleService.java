package gov.cdc.dataprocessing.service.implementation.role;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final PrepareAssocModelHelper prepareAssocModelHelper;

    public RoleService(RoleRepository roleRepository,
                       PrepareAssocModelHelper prepareAssocModelHelper) {
        this.roleRepository = roleRepository;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
    }

    public Collection<RoleDto> findRoleScopedToPatient(Long uid) {
        Collection<RoleDto> roleDtoCollection = new ArrayList<>();
        var result = roleRepository.findRoleScopedToPatient(uid);
        if (result.isPresent()) {
            for(var item: result.get()) {
                var elem = new RoleDto(item);
                elem.setItNew(false);
                elem.setItDirty(false);

                roleDtoCollection.add(elem);
            }
        }

        return roleDtoCollection;
    }

    @Transactional
    public void storeRoleDTCollection(Collection<RoleDto> roleDTColl) throws DataProcessingException {
        try {
            if(roleDTColl == null || roleDTColl.isEmpty()) return;

            for (RoleDto roleDT : roleDTColl) {
                if (roleDT == null) {
                    continue;
                }

                roleDT = prepareAssocModelHelper.prepareAssocDTForRole(roleDT);
                this.saveRole(roleDT);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }



    @Transactional
    public void saveRole(RoleDto roleDto) {
        if (roleDto.isItNew() || roleDto.isItDirty()) {
            var data = new Role(roleDto);
            roleRepository.save(data);
        }
        else if (roleDto.isItDelete()) {
            removeRole(roleDto);
        }
    }

    public Integer loadCountBySubjectCdComb(RoleDto roleDto) {
        var result = roleRepository.loadCountBySubjectCdComb(roleDto.getSubjectEntityUid(), roleDto.getCd());
        return result.orElse(0);
    }

    public Integer loadCountBySubjectScpingCdComb(RoleDto roleDto) {
        var result = roleRepository.loadCountBySubjectScpingCdComb(roleDto.getSubjectEntityUid(), roleDto.getCd(), roleDto.getScopingEntityUid());
        return result.orElse(0);
    }

    private void removeRole(RoleDto roleDto) {
        roleRepository.deleteRoleByPk(roleDto.getSubjectEntityUid(), roleDto.getCd(), roleDto.getRoleSeq());
    }

}
