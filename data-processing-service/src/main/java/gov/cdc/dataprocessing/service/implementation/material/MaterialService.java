package gov.cdc.dataprocessing.service.implementation.material;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.MaterialContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.material.ManufacturedMaterialDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.ManufacturedMaterial;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.Material;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.ManufacturedMaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.MaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

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
public class MaterialService implements IMaterialService {
    private final MaterialRepository materialRepository;
    private final EntityIdRepository entityIdRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;
    private final ParticipationRepository participationRepository;
    private final ManufacturedMaterialRepository manufacturedMaterialRepository;
    private final EntityHelper entityHelper;

    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;
    private final EntityRepository entityRepository;
    private final IEntityLocatorParticipationService entityLocatorParticipationService;

    public MaterialService(MaterialRepository materialRepository,
                           EntityIdRepository entityIdRepository,
                           EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                           RoleRepository roleRepository,
                           ParticipationRepository participationRepository,
                           ManufacturedMaterialRepository manufacturedMaterialRepository,
                           EntityHelper entityHelper,
                           IOdseIdGeneratorWCacheService odseIdGeneratorService, EntityRepository entityRepository,
                           IEntityLocatorParticipationService entityLocatorParticipationService) {
        this.materialRepository = materialRepository;
        this.entityIdRepository = entityIdRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.roleRepository = roleRepository;
        this.participationRepository = participationRepository;
        this.manufacturedMaterialRepository = manufacturedMaterialRepository;
        this.entityHelper = entityHelper;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.entityRepository = entityRepository;
        this.entityLocatorParticipationService = entityLocatorParticipationService;
    }

    @SuppressWarnings("java:S3776")

    public MaterialContainer loadMaterialObject(Long materialUid) {
        MaterialContainer materialContainer = new MaterialContainer();

        var materialResult = materialRepository.findById(materialUid);
        MaterialDto materialDto = null;
        if (materialResult.isPresent()) {
            materialDto = new MaterialDto(materialResult.get());
            materialDto.setItNew(false);
            materialDto.setItDirty(false);
        }
        materialContainer.setTheMaterialDto(materialDto);

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
        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        if (participationResult.isPresent()) {
            for (var item : participationResult.get()) {
                var elem = new ParticipationDto(item);
                elem.setItNew(false);
                elem.setItDirty(false);
                participationDtoCollection.add(elem);
            }
        }
        materialContainer.setTheParticipationDtoCollection(participationDtoCollection);

        var manuMaterialResult = manufacturedMaterialRepository.findByParentUid(materialUid);
        Collection<ManufacturedMaterialDto> manufacturedMaterialDtoCollection = new ArrayList<>();
        if (manuMaterialResult.isPresent()) {
            for (var item : manuMaterialResult.get()) {
                var elem = new ManufacturedMaterialDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                manufacturedMaterialDtoCollection.add(elem);
            }
        }
        materialContainer.setTheManufacturedMaterialDtoCollection(manufacturedMaterialDtoCollection);


        materialContainer.setItNew(false);
        materialContainer.setItDirty(false);

        return materialContainer;
    }


    @Transactional
    public Long saveMaterial(MaterialContainer materialContainer) throws DataProcessingException {
        MaterialDto materialDto = materialContainer.getTheMaterialDto();
        Long lpk = materialDto.getMaterialUid();

        Collection<EntityLocatorParticipationDto> elpDTCol = materialContainer.getTheEntityLocatorParticipationDTCollection();
        Collection<RoleDto> rDTCol = materialContainer.getTheRoleDTCollection();
        Collection<ParticipationDto> pDTCol = materialContainer.getTheParticipationDtoCollection();
        Collection<EntityLocatorParticipationDto> colEntityLocatorParticipation;
        Collection<RoleDto> colRole;
        Collection<ParticipationDto> participationDtoCollection;


        if (elpDTCol != null) {
            colEntityLocatorParticipation = entityHelper.iterateELPDTForEntityLocatorParticipation(elpDTCol);
            materialContainer.setTheEntityLocatorParticipationDTCollection(colEntityLocatorParticipation);
        }
        if (rDTCol != null) {
            colRole = entityHelper.iterateRDT(rDTCol);
            materialContainer.setTheRoleDTCollection(colRole);
        }
        if (pDTCol != null) {
            participationDtoCollection = entityHelper.iteratePDTForParticipation(pDTCol);
            materialContainer.setTheParticipationDtoCollection(participationDtoCollection);
        }

        var result = materialRepository.findById(lpk);
        if (result.isPresent()) {
            lpk = updateMaterial(materialContainer);
        } else {
            // INSERT
            lpk = insertNewMaterial(materialContainer);
        }

        return lpk;

    }

    private Long updateMaterial(MaterialContainer materialContainer) throws  DataProcessingException{
        var timestamp = getCurrentTimeStamp();
        if (materialContainer.getTheMaterialDto() != null) {
            Material material = new Material(materialContainer.getTheMaterialDto());
            persistingMaterial(material, materialContainer.getTheMaterialDto().getMaterialUid(), timestamp);

            if (materialContainer.getTheEntityIdDtoCollection() != null) {
                persistingEntityId(materialContainer.getTheMaterialDto().getMaterialUid(), materialContainer.getTheEntityIdDtoCollection());
            }

            if (materialContainer.getTheEntityLocatorParticipationDTCollection() != null) {
                persistingEntityLocatorParticipation(materialContainer.getTheMaterialDto().getMaterialUid(), materialContainer.getTheEntityLocatorParticipationDTCollection(), true);
            }

            if (materialContainer.getTheManufacturedMaterialDtoCollection() != null) {
                persistingManufacturedMaterial(materialContainer.getTheMaterialDto().getMaterialUid(), materialContainer.getTheManufacturedMaterialDtoCollection());
            }
        }

        if (materialContainer.getTheMaterialDto() !=  null) {
            return materialContainer.getTheMaterialDto().getMaterialUid();
        }
        return null;
    }

    private Long insertNewMaterial(MaterialContainer materialContainer) throws DataProcessingException {
        var uid = odseIdGeneratorService.getValidLocalUid(LocalIdClass.MATERIAL, true);
        var timestamp = getCurrentTimeStamp();
        if (materialContainer.getTheMaterialDto() != null) {
            Material material = new Material(materialContainer.getTheMaterialDto());
            material.setMaterialUid(uid.getGaTypeUid().getSeedValueNbr());
            persistingMaterial(material, uid.getGaTypeUid().getSeedValueNbr(), timestamp);
            material.setLocalId(uid.getClassTypeUid().getUidPrefixCd() + uid.getClassTypeUid().getSeedValueNbr() + uid.getClassTypeUid().getUidSuffixCd());

            if (materialContainer.getTheEntityIdDtoCollection() != null) {
                persistingEntityId(uid.getGaTypeUid().getSeedValueNbr(), materialContainer.getTheEntityIdDtoCollection());
            }

            if (materialContainer.getTheEntityLocatorParticipationDTCollection() != null) {
                persistingEntityLocatorParticipation(uid.getGaTypeUid().getSeedValueNbr(), materialContainer.getTheEntityLocatorParticipationDTCollection(), false);
            }

            if (materialContainer.getTheManufacturedMaterialDtoCollection() != null) {
                persistingManufacturedMaterial(uid.getGaTypeUid().getSeedValueNbr(), materialContainer.getTheManufacturedMaterialDtoCollection());
            }
        }

        return uid.getGaTypeUid().getSeedValueNbr();
    }

    private void persistingEntityLocatorParticipation(
            Long uid,
            Collection<EntityLocatorParticipationDto> entityCollection,
            boolean updateApplied) throws DataProcessingException {
        if (updateApplied) {
            entityLocatorParticipationService.updateEntityLocatorParticipation(entityCollection, uid);
        } else {
            entityLocatorParticipationService.createEntityLocatorParticipation(entityCollection, uid);
        }
    }
    private void persistingManufacturedMaterial(Long uid, Collection<ManufacturedMaterialDto> manufacturedMaterialDtoCollection) throws DataProcessingException {
        ArrayList<ManufacturedMaterialDto> arr = new ArrayList<>(manufacturedMaterialDtoCollection);
        for(var item : arr) {
            item.setMaterialUid(uid);
            if (item.getManufacturedMaterialSeq() == null) {
                throw new DataProcessingException("Material Seq is Null");
            }
            ManufacturedMaterial data = new ManufacturedMaterial(item);
            manufacturedMaterialRepository.save(data);

            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
        }

    }
    private void persistingEntityId(Long uid, Collection<EntityIdDto> entityIdCollection ) throws DataProcessingException {
        try {
            Iterator<EntityIdDto> anIterator;
            ArrayList<EntityIdDto>  entityList = (ArrayList<EntityIdDto> )entityIdCollection;
            anIterator = entityList.iterator();
            int maxSeq = 0;
            while (anIterator.hasNext()) {
                EntityIdDto entityID = anIterator.next();
                if(maxSeq == 0) {
                    if(null == entityID.getEntityUid() || entityID.getEntityUid() < 0) {
                        entityID.setEntityUid(uid);
                    }
                    var result = entityIdRepository.findMaxEntityId(entityID.getEntityUid());

                    if (result.isPresent()) {
                        maxSeq = result.get();
                    }
                }

                entityID.setEntityIdSeq(maxSeq++);
                EntityId data = new EntityId(entityID);
                entityIdRepository.save(data);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }
    private void persistingMaterial(Material material, Long uid, Timestamp timestamp) {
        EntityODSE entityODSE = new EntityODSE();
        entityODSE.setEntityUid(uid);
        entityODSE.setClassCd(NEDSSConstant.MATERIAL);
        entityRepository.save(entityODSE);

        if (material.getAddReasonCd() == null) {
            material.setAddReasonCd("Add");
        }

        if (material.getAddTime() == null) {
            material.setAddTime(timestamp);
        }

        if (material.getAddUserId() == null) {
            material.setAddUserId(AuthUtil.authUser.getNedssEntryId());
        }


        if(material.getLastChgTime() == null) {
            material.setLastChgTime(timestamp);
        }

        if (material.getLastChgUserId() == null) {
            material.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        }

        if (material.getRecordStatusTime() == null) {
            material.setRecordStatusTime(timestamp);
        }

        if (material.getStatusTime() == null) {
            material.setStatusTime(timestamp);
        }

        if( material.getVersionCtrlNbr() == null){
            material.setVersionCtrlNbr(1);
        }

        materialRepository.save(material);
    }

}
