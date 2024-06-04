package gov.cdc.dataprocessing.service.implementation.role;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Service
public class RoleService implements IRoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
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
                saveRole(roleDT);
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
