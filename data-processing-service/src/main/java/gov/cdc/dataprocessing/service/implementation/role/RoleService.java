package gov.cdc.dataprocessing.service.implementation.role;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service

public class RoleService implements IRoleService {
    @Value("${service.timezone}")
    private String tz = "UTC";
    private final RoleRepository roleRepository;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final DataModifierReposJdbc dataModifierReposJdbc;

    public RoleService(RoleRepository roleRepository,
                       PrepareAssocModelHelper prepareAssocModelHelper, DataModifierReposJdbc dataModifierReposJdbc) {
        this.roleRepository = roleRepository;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
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

    public void storeRoleDTCollection(Collection<RoleDto> roleDTColl) throws DataProcessingException {
        if(roleDTColl == null || roleDTColl.isEmpty()) return;

        for (RoleDto roleDT : roleDTColl) {
            if (roleDT == null) {
                continue;
            }

            roleDT = prepareAssocModelHelper.prepareAssocDTForRole(roleDT);
            this.saveRole(roleDT);
        }
    }



    public void saveRole(RoleDto roleDto) {
        if (roleDto.isItNew() || roleDto.isItDirty()) {
            var data = new Role(roleDto,tz);
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
        dataModifierReposJdbc.deleteRoleByPk(roleDto.getSubjectEntityUid(), roleDto.getCd(), roleDto.getRoleSeq());
    }

}
