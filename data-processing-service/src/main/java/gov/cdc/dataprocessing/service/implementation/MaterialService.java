package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ManufacturedMaterialDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MaterialDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.MaterialVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.ManufacturedMaterial;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.Material;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.ManufacturedMaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.material.MaterialRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.implementation.core.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.interfaces.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.IMaterialService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Service
public class MaterialService implements IMaterialService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialService.class);
    private final MaterialRepository materialRepository;
    private final EntityIdRepository entityIdRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;
    private final ParticipationRepository participationRepository;
    private final ManufacturedMaterialRepository manufacturedMaterialRepository;
    private final EntityHelper entityHelper;

    private final OdseIdGeneratorService odseIdGeneratorService;
    private final EntityRepository entityRepository;
    private final IEntityLocatorParticipationService entityLocatorParticipationService;

    public MaterialService(MaterialRepository materialRepository,
                           EntityIdRepository entityIdRepository,
                           EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                           RoleRepository roleRepository,
                           ParticipationRepository participationRepository,
                           ManufacturedMaterialRepository manufacturedMaterialRepository,
                           EntityHelper entityHelper,
                           OdseIdGeneratorService odseIdGeneratorService,
                           EntityRepository entityRepository,
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


    @Transactional
    public Long saveMaterial(MaterialVO materialVO) throws DataProcessingException {
        MaterialDT materialDT = materialVO.getTheMaterialDT();
        Long lpk = materialDT.getMaterialUid();

        Collection<EntityLocatorParticipationDto> elpDTCol = materialVO.getTheEntityLocatorParticipationDTCollection();
        Collection<RoleDto> rDTCol = materialVO.getTheRoleDTCollection();
        Collection<ParticipationDT> pDTCol = materialVO.getTheParticipationDTCollection();
        Collection<EntityLocatorParticipationDto> colEntityLocatorParticipation;
        Collection<RoleDto> colRole;
        Collection<ParticipationDT> participationDTCollection;


        if (elpDTCol != null) {
            colEntityLocatorParticipation = entityHelper.iterateELPDTForEntityLocatorParticipation(elpDTCol);
            materialVO.setTheEntityLocatorParticipationDTCollection(colEntityLocatorParticipation);
        }
        if (rDTCol != null) {
            colRole = entityHelper.iterateRDT(rDTCol);
            materialVO.setTheRoleDTCollection(colRole);
        }
        if (pDTCol != null) {
            participationDTCollection = entityHelper.iteratePDTForParticipation(pDTCol);
            materialVO.setTheParticipationDTCollection(participationDTCollection);
        }

        var result = materialRepository.findById(lpk);
        if (result.isPresent()) {
            lpk = updateMaterial(materialVO);
        } else {
            // INSERT
            lpk = insertNewMaterial(materialVO);
        }

        return lpk;

    }

    private Long updateMaterial(MaterialVO materialVO) throws  DataProcessingException{
        var timestamp = getCurrentTimeStamp();
        if (materialVO.getTheMaterialDT() != null) {
            Material material = new Material(materialVO.getTheMaterialDT());
            persistingMaterial(material, materialVO.getTheMaterialDT().getMaterialUid(), timestamp);

            if (materialVO.getTheEntityIdDtoCollection() != null) {
                persistingEntityId(materialVO.getTheMaterialDT().getMaterialUid(), materialVO.getTheEntityIdDtoCollection());
            }

            if (materialVO.getTheEntityLocatorParticipationDTCollection() != null) {
                persistingEntityLocatorParticipation(materialVO.getTheMaterialDT().getMaterialUid(), materialVO.getTheEntityLocatorParticipationDTCollection(), true);
            }

            if (materialVO.getTheManufacturedMaterialDTCollection() != null) {
                persistingManufacturedMaterial(materialVO.getTheMaterialDT().getMaterialUid(), materialVO.getTheManufacturedMaterialDTCollection());
            }
        }

        return materialVO.getTheMaterialDT().getMaterialUid();
    }

    private Long insertNewMaterial(MaterialVO materialVO) throws DataProcessingException {
        var uid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.MATERIAL);
        var timestamp = getCurrentTimeStamp();
        if (materialVO.getTheMaterialDT() != null) {
            Material material = new Material(materialVO.getTheMaterialDT());
            material.setMaterialUid(uid.getSeedValueNbr());
            persistingMaterial(material, uid.getSeedValueNbr(), timestamp);

            if (materialVO.getTheEntityIdDtoCollection() != null) {
                persistingEntityId(uid.getSeedValueNbr(), materialVO.getTheEntityIdDtoCollection());
            }

            if (materialVO.getTheEntityLocatorParticipationDTCollection() != null) {
                persistingEntityLocatorParticipation(uid.getSeedValueNbr(), materialVO.getTheEntityLocatorParticipationDTCollection(), false);
            }

            if (materialVO.getTheManufacturedMaterialDTCollection() != null) {
                persistingManufacturedMaterial(uid.getSeedValueNbr(), materialVO.getTheManufacturedMaterialDTCollection());
            }
        }

        return uid.getSeedValueNbr();
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
    private void persistingManufacturedMaterial(Long uid, Collection<ManufacturedMaterialDT> manufacturedMaterialDTCollection) throws DataProcessingException {
        ArrayList<ManufacturedMaterialDT> arr = new ArrayList<>(manufacturedMaterialDTCollection);
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
            Iterator<EntityIdDto> anIterator = null;
            ArrayList<EntityIdDto>  entityList = (ArrayList<EntityIdDto> )entityIdCollection;
            anIterator = entityList.iterator();
            int maxSeq = 0;
            while (null != anIterator && anIterator.hasNext()) {
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
            material.setAddUserId(AuthUtil.authUser.getAuthUserUid());
        }


        if(material.getLastChgTime() == null) {
            material.setLastChgTime(timestamp);
        }

        if (material.getLastChgUserId() == null) {
            material.setLastChgUserId(AuthUtil.authUser.getLastChgUserId());
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
