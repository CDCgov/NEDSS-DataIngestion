package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ManufacturedMaterialDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MaterialDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.MaterialVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.ManufacturedMaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.MaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.IMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class MaterialService implements IMaterialService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialService.class);
    private final MaterialRepository materialRepository;
    private final EntityIdRepository entityIdRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;
    private final ParticipationRepository participationRepository;
    private final ManufacturedMaterialRepository manufacturedMaterialRepository;

    public MaterialService(MaterialRepository materialRepository,
                           EntityIdRepository entityIdRepository,
                           EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                           RoleRepository roleRepository,
                           ParticipationRepository participationRepository,
                           ManufacturedMaterialRepository manufacturedMaterialRepository) {
        this.materialRepository = materialRepository;
        this.entityIdRepository = entityIdRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.roleRepository = roleRepository;
        this.participationRepository = participationRepository;
        this.manufacturedMaterialRepository = manufacturedMaterialRepository;
    }

    public MaterialVO loadMaterialObject(Long materialUid) {
        MaterialVO materialContainer = new MaterialVO();

        var materialResult = materialRepository.findById(materialUid);
        MaterialDT materialDT = null;
        if (materialResult.isPresent()) {
            materialDT = new MaterialDT(materialResult.get());
            materialDT.setItNew(false);
            materialDT.setItDirty(false);
        }
        materialContainer.setTheMaterialDT(materialDT);

        var entityIdResult = entityIdRepository.findByParentUid(materialUid);
        Collection<EntityIdDto> entityIdDto = new ArrayList<>();
        if (entityIdResult.isPresent()) {
            for (EntityId item : entityIdResult.get()) {
                var elem = new EntityIdDto(item);
                elem.setItNew(false);
                elem.setItDirty(false);
                entityIdDto.add(elem);
            }
        }
        materialContainer.setTheEntityIdDtoCollection(entityIdDto);

        var entityLocatorResult = entityLocatorParticipationRepository.findByParentUid(materialUid);
        Collection<EntityLocatorParticipationDto> entityLocatorParticipationDtoCollection = new ArrayList<>();
        if (entityLocatorResult.isPresent()) {
            for (var item : entityLocatorResult.get()) {
                var elem = new EntityLocatorParticipationDto(item);
                elem.setItNew(false);
                elem.setItDirty(false);
                entityLocatorParticipationDtoCollection.add(elem);
            }
        }
        materialContainer.setTheEntityLocatorParticipationDTCollection(entityLocatorParticipationDtoCollection);


        var roleResult = roleRepository.findByParentUid(materialUid);
        Collection<RoleDto> roleDtoCollection = new ArrayList<>();
        if (roleResult.isPresent()) {
            for (var item : roleResult.get()) {
                var elem = new RoleDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                roleDtoCollection.add(elem);
            }
        }
        materialContainer.setTheRoleDTCollection(roleDtoCollection);


        var participationResult = participationRepository.findByParentUid(materialUid);
        Collection<ParticipationDT> participationDTCollection = new ArrayList<>();
        if (participationResult.isPresent()) {
            for (var item : participationResult.get()) {
                var elem = new ParticipationDT(item);
                elem.setItNew(false);
                elem.setItDirty(false);
                participationDTCollection.add(elem);
            }
        }
        materialContainer.setTheParticipationDTCollection(participationDTCollection);

        var manuMaterialResult = manufacturedMaterialRepository.findByParentUid(materialUid);
        Collection<ManufacturedMaterialDT> manufacturedMaterialDTCollection = new ArrayList<>();
        if (manuMaterialResult.isPresent()) {
            for (var item : manuMaterialResult.get()) {
                var elem = new ManufacturedMaterialDT(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                manufacturedMaterialDTCollection.add(elem);
            }
        }
        materialContainer.setTheManufacturedMaterialDTCollection(manufacturedMaterialDTCollection);


        materialContainer.setItNew(false);
        materialContainer.setItDirty(false);

        return materialContainer;
    }
}
