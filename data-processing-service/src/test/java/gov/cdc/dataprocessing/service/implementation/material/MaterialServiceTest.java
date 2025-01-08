package gov.cdc.dataprocessing.service.implementation.material;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.MaterialContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.material.ManufacturedMaterialDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.ManufacturedMaterial;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.Material;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.ManufacturedMaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.MaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.implementation.entity.EntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class MaterialServiceTest {

    @InjectMocks
    MaterialService materialService;

    @Mock
    MaterialRepository materialRepository;

    @Mock
    IOdseIdGeneratorWCacheService odseIdGeneratorService;

    @Mock
    EntityHelper entityHelper;

    @Mock
    EntityIdRepository entityIdRepository;

    @Mock
    EntityLocatorParticipationRepository entityLocatorParticipationRepository;

    @Mock
    EntityLocatorParticipationService entityLocatorParticipationService;

    @Mock
    EntityRepository entityRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    ParticipationRepository participationRepository;

    @Mock
    ManufacturedMaterialRepository manufacturedMaterialRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadMaterialObject() {
        Long materialUid = 1L;
        when(materialRepository.findById(materialUid)).thenReturn(Optional.of(getMaterialDto()));

        when(entityIdRepository.findByParentUid(materialUid)).thenReturn(getEntityId());

        when(entityLocatorParticipationRepository.findByParentUid(materialUid)).thenReturn(getEntityLocatorPart());

        when(roleRepository.findByParentUid(materialUid)).thenReturn(Optional.of(getRole()));

        when(participationRepository.findByParentUid(materialUid)).thenReturn(Optional.of(getParts()));

        when(manufacturedMaterialRepository.findByParentUid(materialUid)).thenReturn(Optional.of(getManMaterials()));

        MaterialContainer result = materialService.loadMaterialObject(materialUid);

        assertEquals(1L, result.getTheMaterialDto().getMaterialUid());
    }

    private List<ManufacturedMaterial> getManMaterials() {
        ManufacturedMaterialDto materialDto = new ManufacturedMaterialDto();
        materialDto.setMaterialUid(1L);

        List<ManufacturedMaterial> manufacturedMaterials = new ArrayList<>();
        manufacturedMaterials.add(new ManufacturedMaterial(materialDto));

        return manufacturedMaterials;
    }

    private List<Participation> getParts() {
        ParticipationDto part = new ParticipationDto();
        part.setCd("test participation");

        List<Participation> participations = new ArrayList<>();
        participations.add(new Participation(part));

        return participations;
    }

    private List<Role> getRole() {
        RoleDto roleDto = new RoleDto();
        roleDto.setCd("test role");

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(roleDto));

        return roles;
    }

    private Optional<List<EntityLocatorParticipation>> getEntityLocatorPart() {
        EntityLocatorParticipationDto entityLocatorParticipationDto = new EntityLocatorParticipationDto();
        entityLocatorParticipationDto.setEntityUid(3L);
        entityLocatorParticipationDto.setAddUserId(123L);

        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        entityLocatorParticipations.add(new EntityLocatorParticipation(entityLocatorParticipationDto, "UTC"));
        return Optional.of(entityLocatorParticipations);
    }

    private Optional<List<EntityId>> getEntityId() {
        EntityIdDto entityIdDto = new EntityIdDto();
        entityIdDto.setEntityUid(2L);

        List<EntityId> entityIds = new ArrayList<>();

        entityIds.add(new EntityId(entityIdDto, "UTC"));

        return Optional.of(entityIds);
    }

    private Material getMaterialDto() {
        MaterialDto materialDto = new MaterialDto();
        materialDto.setMaterialUid(1L);
        materialDto.setDescription("test material");
        materialDto.setAddUserId(123L);
        materialDto.setLastChgUserId(123L);

        return new Material(materialDto);
    }

    @Test
    void saveMaterial() throws DataProcessingException {
        Long materialUid = 1L;
        when(materialRepository.findById(materialUid)).thenReturn(Optional.of(getMaterialDto()));

        when(entityRepository.save(any())).thenReturn(null);

        when(entityHelper.iterateRDT(any())).thenReturn(getRoleDto());

        when(entityHelper.iterateELPDTForEntityLocatorParticipation(any())).thenReturn(getEntityLocatorPartDto());

        doNothing().when(entityLocatorParticipationService).updateEntityLocatorParticipation(any(), any());

        when(entityHelper.iteratePDTForParticipation(any())).thenReturn(getPartsDto());

        Long result = materialService.saveMaterial(getMaterialContainer());
        assertEquals(1L, result);
    }

    @Test
    void saveMaterialNew() throws DataProcessingException {
        Long materialUid = 1L;
        when(materialRepository.findById(materialUid)).thenReturn(Optional.empty());

        when(entityRepository.save(any())).thenReturn(null);

        when(entityHelper.iterateRDT(any())).thenReturn(getRoleDto());

        when(entityHelper.iterateELPDTForEntityLocatorParticipation(any())).thenReturn(getEntityLocatorPartDto());

        doNothing().when(entityLocatorParticipationService).updateEntityLocatorParticipation(any(), any());

        when(entityHelper.iteratePDTForParticipation(any())).thenReturn(getPartsDto());

        when(odseIdGeneratorService.getValidLocalUid(any(), eq(true))).thenReturn(getLocalUidGenerator());

        Long result = materialService.saveMaterial(getMaterialContainer());
        assertEquals(1L, result);
    }

    private LocalUidModel getLocalUidGenerator() {
        LocalUidModel localUidGenerator = new LocalUidModel();
        localUidGenerator.setGaTypeUid(new LocalUidGeneratorDto());
        localUidGenerator.setClassTypeUid(new LocalUidGeneratorDto());

        localUidGenerator.getGaTypeUid().setUidPrefixCd("123");
        localUidGenerator.getGaTypeUid().setUidPrefixCd("234");
        localUidGenerator.getGaTypeUid().setTypeCd("type");
        localUidGenerator.getGaTypeUid().setClassNameCd("class name");
        localUidGenerator.getGaTypeUid().setSeedValueNbr(1L);

        localUidGenerator.getClassTypeUid().setUidPrefixCd("123");
        localUidGenerator.getClassTypeUid().setUidPrefixCd("234");
        localUidGenerator.getClassTypeUid().setTypeCd("type");
        localUidGenerator.getClassTypeUid().setClassNameCd("class name");
        localUidGenerator.getClassTypeUid().setSeedValueNbr(1L);
        return localUidGenerator;
    }

    private MaterialContainer getMaterialContainer() {
        MaterialContainer materialContainer = new MaterialContainer();
        materialContainer.setTheMaterialDto(new MaterialDto(getMaterialDto()));
        materialContainer.setTheRoleDTCollection(getRoleDto());
        materialContainer.setTheEntityLocatorParticipationDTCollection(getEntityLocatorPartDto());
        materialContainer.setTheParticipationDtoCollection(getPartsDto());
        materialContainer.setTheEntityIdDtoCollection(getEntityIdDto());
        materialContainer.setTheManufacturedMaterialDtoCollection(getManMaterialsDto());
        return materialContainer;
    }

    private List<ManufacturedMaterialDto> getManMaterialsDto() {
        ManufacturedMaterialDto materialDto = new ManufacturedMaterialDto();
        materialDto.setMaterialUid(1L);
        materialDto.setManufacturedMaterialSeq(123);

        List<ManufacturedMaterialDto> manufacturedMaterials = new ArrayList<>();
        manufacturedMaterials.add(materialDto);

        return manufacturedMaterials;
    }

    private  List<EntityIdDto> getEntityIdDto() {
        EntityIdDto entityIdDto = new EntityIdDto();
        entityIdDto.setEntityUid(2L);

        List<EntityIdDto> entityIds = new ArrayList<>();

        entityIds.add(entityIdDto);

        return entityIds;
    }

    private List<EntityLocatorParticipationDto> getEntityLocatorPartDto() {
        EntityLocatorParticipationDto entityLocatorParticipationDto = new EntityLocatorParticipationDto();
        entityLocatorParticipationDto.setEntityUid(3L);
        entityLocatorParticipationDto.setAddUserId(123L);

        List<EntityLocatorParticipationDto> entityLocatorParticipations = new ArrayList<>();
        entityLocatorParticipations.add(entityLocatorParticipationDto);
        return entityLocatorParticipations;
    }

    private List<RoleDto> getRoleDto() {
        RoleDto roleDto = new RoleDto();
        roleDto.setCd("test role");

        List<RoleDto> roles = new ArrayList<>();
        roles.add(roleDto);

        return roles;
    }

    private List<ParticipationDto> getPartsDto() {
        ParticipationDto part = new ParticipationDto();
        part.setCd("test participation");

        List<ParticipationDto> participations = new ArrayList<>();
        participations.add(part);

        return participations;
    }
}