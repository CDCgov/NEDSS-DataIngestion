package gov.cdc.dataprocessing.utilities.component.organization;

import gov.cdc.dataprocessing.constant.elr.DataTables;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
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
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class OrganizationRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationRepositoryUtil.class);
    /**
     * Organization Entity Code
     */
    public static final String ORG = "ORG";
    private final OrganizationJdbcRepository organizationJdbcRepository;
    private final EntityJdbcRepository entityJdbcRepository;
    private final EntityIdJdbcRepository entityIdJdbcRepository;
    private final OrganizationNameJdbcRepository organizationNameJdbcRepository;
    private final EntityLocatorJdbcRepository entityLocatorJdbcRepository;
    private final RoleJdbcRepository roleJdbcRepository;
    private final EntityHelper entityHelper;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final ParticipationJdbcRepository participationJdbcRepository;
    private final UidPoolManager uidPoolManager;

    public OrganizationRepositoryUtil(
            OrganizationJdbcRepository organizationJdbcRepository,
            EntityJdbcRepository entityJdbcRepository,
            EntityIdJdbcRepository entityIdJdbcRepository,
            OrganizationNameJdbcRepository organizationNameJdbcRepository,
            EntityLocatorJdbcRepository entityLocatorJdbcRepository,
            RoleJdbcRepository roleJdbcRepository,
            EntityHelper entityHelper,
            PrepareAssocModelHelper prepareAssocModelHelper,
            PrepareEntityStoredProcRepository prepareEntityStoredProcRepository,
            ParticipationJdbcRepository participationJdbcRepository,
            @Lazy UidPoolManager uidPoolManager) {
        this.organizationJdbcRepository = organizationJdbcRepository;
        this.entityJdbcRepository = entityJdbcRepository;
        this.entityIdJdbcRepository = entityIdJdbcRepository;
        this.organizationNameJdbcRepository = organizationNameJdbcRepository;
        this.entityLocatorJdbcRepository = entityLocatorJdbcRepository;
        this.roleJdbcRepository = roleJdbcRepository;
        this.entityHelper = entityHelper;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.prepareEntityStoredProcRepository = prepareEntityStoredProcRepository;
        this.participationJdbcRepository = participationJdbcRepository;
        this.uidPoolManager = uidPoolManager;
    }

    public Organization findOrganizationByUid(Long orgUid) {
        var result = organizationJdbcRepository.findById(orgUid);
        return Objects.requireNonNullElseGet(result, Organization::new);
    }

    public long createOrganization(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        Long organizationUid ;
        long oldOrgUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();

        String localUid ;
        var localIdModel = uidPoolManager.getNextUid(LocalIdClass.ORGANIZATION, true);
        organizationUid = localIdModel.getGaTypeUid().getSeedValueNbr();
        localUid = localIdModel.getClassTypeUid().getUidPrefixCd() + localIdModel.getClassTypeUid().getSeedValueNbr() + localIdModel.getClassTypeUid().getUidSuffixCd();

        if (organizationContainer.getTheOrganizationDto().getLocalId() == null || organizationContainer.getTheOrganizationDto().getLocalId().trim().isEmpty()) {
            organizationContainer.getTheOrganizationDto().setLocalId(localUid);
        }
        /**
         * Starts inserting a new organization
         */
        // Upper stream require this id to not mutated (must be negative), so falseToNew Method can parse the id correctly
        organizationContainer.getTheOrganizationDto().setOrganizationUid(organizationUid);
        organizationContainer.getTheOrganizationDto().setLocalId(localUid);
        organizationContainer.getTheOrganizationDto().setVersionCtrlNbr(1);
        insertOrganization(organizationContainer, "CREATE");

        if (organizationContainer.getTheOrganizationNameDtoCollection() != null && !organizationContainer.getTheOrganizationNameDtoCollection().isEmpty()) {
            insertOrganizationNames(organizationContainer, "CREATE");
        }
        //NOTE: Upsert EntityID
        if (organizationContainer.getTheEntityIdDtoCollection() != null && !organizationContainer.getTheEntityIdDtoCollection().isEmpty()) {
            createEntityId(organizationContainer);
        }
        //NOTE: Create Entity Locator Participation
        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null && !organizationContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            createEntityLocatorParticipation(organizationContainer, "CREATE");
        }
        //NOTE: Create Role
        if (organizationContainer.getTheRoleDTCollection() != null && !organizationContainer.getTheRoleDTCollection().isEmpty()) {
            createRole(organizationContainer, "CREATE");
        }

        organizationContainer.getTheOrganizationDto().setOrganizationUid(oldOrgUid);

        return organizationUid;
    }

    public void updateOrganization(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        /**
         * Starts inserting a new organization
         */
        if (organizationContainer == null) {
            throw new DataProcessingException("Organization Container Is Null");
        }
        insertOrganization(organizationContainer, "UPDATE");

        if (organizationContainer.getTheOrganizationNameDtoCollection() != null && !organizationContainer.getTheOrganizationNameDtoCollection().isEmpty()) {
            insertOrganizationNames(organizationContainer, "UPDATE");
        }
        //NOTE: Upsert EntityID
        if (organizationContainer.getTheEntityIdDtoCollection() != null && !organizationContainer.getTheEntityIdDtoCollection().isEmpty()) {
            createEntityId(organizationContainer);
        }
        //NOTE: Create Entity Locator Participation
        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null && !organizationContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            createEntityLocatorParticipation(organizationContainer, "UPDATE");
        }
        //NOTE: Create Role
        if (organizationContainer.getTheRoleDTCollection() != null && !organizationContainer.getTheRoleDTCollection().isEmpty()) {
            createRole(organizationContainer, "UPDATE");
        }
    }

    private Long insertOrganization(OrganizationContainer organizationContainer, String operation)   {
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        Long organizationUid;
        /**
         * Inserts into entity table for organization
         */

        organizationUid = organizationDto.getOrganizationUid();
        EntityODSE entityModel = new EntityODSE();
        entityModel.setEntityUid(organizationUid);
        entityModel.setClassCd(ORG);
        if (operation.equalsIgnoreCase("CREATE")) {
            entityJdbcRepository.createEntity(entityModel);
        }
        else {
            entityJdbcRepository.updateEntity(entityModel);
        }

        organizationDto.setCd(entityModel.getClassCd());

        Organization organization = new Organization(organizationDto);
        if (operation.equalsIgnoreCase("CREATE")) {
            organizationJdbcRepository.insertOrganization(organization);
        }
        else {
            organizationJdbcRepository.updateOrganization(organization);
        }
        organizationDto.setItNew(false);
        organizationDto.setItDirty(false);

        return organizationUid;
    }

    private void insertOrganizationNames(OrganizationContainer organizationContainer,
                                         String operation)
              {
        logger.debug("insertOrganizationNames(long organizationUID, Collection<Object>  organizationNames)");
        Iterator<OrganizationNameDto> anIterator;
        long organizationUID = organizationContainer.getTheOrganizationDto().getOrganizationUid();
        Collection<OrganizationNameDto> organizationNames = organizationContainer.getTheOrganizationNameDtoCollection();
        /**
         * Inserts Organization names
         */
        anIterator = organizationNames.iterator();
        while (anIterator.hasNext()) {
            OrganizationNameDto orgNameDT = anIterator.next();

            if (orgNameDT.getOrganizationNameSeq() == null)
            {
                orgNameDT.setOrganizationNameSeq(3);
            }

            orgNameDT.setOrganizationUid(organizationUID);
            OrganizationName orgName = new OrganizationName(orgNameDT);
            // Save Organization Name records
            if (operation.equalsIgnoreCase("CREATE")) {
                organizationNameJdbcRepository.insertOrganizationName(orgName);
            }
            else {
                organizationNameJdbcRepository.updateOrganizationName(orgName);
            }

            orgNameDT.setOrganizationUid(organizationUID);
            orgNameDT.setItNew(false);
            orgNameDT.setItDirty(false);
        }
        logger.debug("OrganizationRepositoryUtil - Done inserting all Organization names");
    }//end of inserting Organization names

    private void createEntityId(OrganizationContainer organizationContainer)   {
        ArrayList<EntityIdDto> entityList = (ArrayList<EntityIdDto>) organizationContainer.getTheEntityIdDtoCollection();
        Long pUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();
        for (EntityIdDto entityIdDto : entityList) {
            entityIdDto.setEntityUid(pUid);
            var data = new EntityId(entityIdDto, tz);
            entityIdJdbcRepository.mergeEntityId(data);
        }
    }

    private void createEntityLocatorParticipation(OrganizationContainer ovo, String operation) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto> entityLocatorList = (ArrayList<EntityLocatorParticipationDto>) ovo.getTheEntityLocatorParticipationDtoCollection();
        for (EntityLocatorParticipationDto entityLocatorDT : entityLocatorList) {
            var localUid =  uidPoolManager.getNextUid(LocalIdClass.ORGANIZATION, true);
            if (entityLocatorDT.getClassCd().equals(NEDSSConstant.PHYSICAL) && entityLocatorDT.getThePhysicalLocatorDto() != null) {
                entityLocatorDT.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                var phy = new PhysicalLocator(entityLocatorDT.getThePhysicalLocatorDto());
                if (operation.equalsIgnoreCase("CREATE")) {
                    entityLocatorJdbcRepository.createPhysicalLocator(phy);
                }
                else {
                    entityLocatorJdbcRepository.updatePhysicalLocator(phy);
                }
            }
            if (entityLocatorDT.getClassCd().equals(NEDSSConstant.POSTAL) && entityLocatorDT.getThePostalLocatorDto() != null) {
                entityLocatorDT.getThePostalLocatorDto().setPostalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                var pos = new PostalLocator(entityLocatorDT.getThePostalLocatorDto());
                if (operation.equalsIgnoreCase("CREATE")) {
                    entityLocatorJdbcRepository.createPostalLocator(pos);
                }
                else {
                    entityLocatorJdbcRepository.updatePostalLocator(pos);
                }
            }
            if (entityLocatorDT.getClassCd().equals(NEDSSConstant.TELE) && entityLocatorDT.getTheTeleLocatorDto() != null) {
                entityLocatorDT.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                var tele = new TeleLocator(entityLocatorDT.getTheTeleLocatorDto());
                if (operation.equalsIgnoreCase("CREATE")) {
                    entityLocatorJdbcRepository.createTeleLocator(tele);
                }
                else {
                    entityLocatorJdbcRepository.updateTeleLocator(tele);
                }
            }
            entityLocatorDT.setEntityUid(ovo.getTheOrganizationDto().getOrganizationUid());
            entityLocatorDT.setLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());

            if (entityLocatorDT.getVersionCtrlNbr() == null) {
                entityLocatorDT.setVersionCtrlNbr(1);
            }
            var data = new EntityLocatorParticipation(entityLocatorDT, tz);
            if (operation.equalsIgnoreCase("CREATE")) {
                entityLocatorJdbcRepository.createEntityLocatorParticipation(data);
            }
            else {
                entityLocatorJdbcRepository.updateEntityLocatorParticipation(data);
            }
        }
    }

    private void createRole(OrganizationContainer ovo, String operation)   {
        ArrayList<RoleDto> roleList = (ArrayList<RoleDto>) ovo.getTheRoleDTCollection();
        for (RoleDto obj : roleList) {
            var rol = new Role(obj);
            if (operation.equalsIgnoreCase("CREATE"))
            {
                roleJdbcRepository.createRole(rol);
            }
            else
            {
                roleJdbcRepository.updateRole(rol);
            }
        }

    }

    /**
     * Sets the organization values in the databse based on the businessTrigger
     *
     * @param organizationContainer the OrganizationContainer
     * @param businessTriggerCd     the String
     * @return organizationUID the Long
     */
    public Long setOrganization(OrganizationContainer organizationContainer,
                                String businessTriggerCd)
            throws DataProcessingException {
        Long organizationUID;
        organizationUID = setOrganizationInternal(organizationContainer, businessTriggerCd);
        return organizationUID;
    }
    @SuppressWarnings("java:S3776")
    public Long setOrganizationInternal(OrganizationContainer organizationContainer, String businessTriggerCd) throws DataProcessingException {
        Long organizationUID;
        logger.debug("\n\n Inside set");
        if (!organizationContainer.isItNew() && !organizationContainer.isItDirty()) {
            return organizationContainer.getTheOrganizationDto()
                    .getOrganizationUid();
        } else {
            Integer existVer = null;
            if (organizationContainer.getTheOrganizationDto().getOrganizationUid() > 0) {
                var existObs = loadObject(organizationContainer.getTheOrganizationDto().getOrganizationUid(), null);
                if (existObs != null && existObs.getTheOrganizationDto() != null) {
                    existVer = existObs.getTheOrganizationDto().getVersionCtrlNbr();
                }
            }
            OrganizationDto newOrganizationDto = (OrganizationDto) prepareAssocModelHelper
                    .prepareVO(organizationContainer.getTheOrganizationDto(),
                            "ORGANIZATION", businessTriggerCd,
                            "ORGANIZATION",
                            NEDSSConstant.BASE,
                            existVer
                    );
            organizationContainer.setTheOrganizationDto(newOrganizationDto);

            prepareVO(organizationContainer.getTheOrganizationDto(), businessTriggerCd,
                    DataTables.ORGANIZATION_TABLE, NEDSSConstant.BASE);

            Collection<EntityLocatorParticipationDto> elpDTCol = organizationContainer
                    .getTheEntityLocatorParticipationDtoCollection();
            Collection<RoleDto> rDTCol = organizationContainer
                    .getTheRoleDTCollection();
            Collection<ParticipationDto> pDTCol = organizationContainer
                    .getTheParticipationDtoCollection();
            //Collection<EntityLocatorParticipationDto> col = null;
            if (elpDTCol != null) {
                Collection<EntityLocatorParticipationDto> col = entityHelper.iterateELPDTForEntityLocatorParticipation(elpDTCol);
                organizationContainer
                        .setTheEntityLocatorParticipationDtoCollection(col);
            }
            if (rDTCol != null) {
                Collection<RoleDto> col = entityHelper.iterateRDT(rDTCol);
                organizationContainer.setTheRoleDTCollection(col);
            }
            if (pDTCol != null) {
                Collection<ParticipationDto> col = entityHelper.iteratePDTForParticipation(pDTCol);
                organizationContainer.setTheParticipationDtoCollection(col);
            }
            /* Call the function to persist the OrganizationName */

            this.prepareOrganizationNameBeforePersistence(organizationContainer);

            if (organizationContainer.isItNew()) {
                organizationUID = this.createOrganization(organizationContainer);
                logger.debug(" OrganizationRepositoryUtil.setOrganization -  Organization Created");
                logger.debug("OrganizationRepositoryUtil.setOrganization {}", organizationUID);
            } else {
                this.updateOrganization(organizationContainer);
                organizationUID = organizationContainer
                        .getTheOrganizationDto().getOrganizationUid();
                logger.debug(" OrganizationRepositoryUtil.setOrganizationInternal -  Organization Updated");
            }
        }

        logger.debug("EntityControllerEJB.setOrganization - ouid  =  {}", organizationUID);
        return organizationUID;
    }

    private void prepareOrganizationNameBeforePersistence(
            OrganizationContainer organizationContainer)   {
        Collection<OrganizationNameDto> namesCollection;
        Iterator<OrganizationNameDto> anIterator;
        String selectedName = null;
        namesCollection = organizationContainer.getTheOrganizationNameDtoCollection();
        if (namesCollection != null) {
            try {
                for (anIterator = namesCollection.iterator(); anIterator
                        .hasNext(); ) {
                    OrganizationNameDto organizationNameDto = anIterator
                            .next();
                    if (organizationNameDto.getNmUseCd().equals("L")) {
                        selectedName = organizationNameDto.getNmTxt();
                        organizationContainer.getTheOrganizationDto().setDisplayNm(
                                selectedName);
                    }
                }
            } catch (Exception e) {
                logger.error("Exception setting the Organization Name: {}", selectedName);
            }
        }
    }

    /**
     * Specifically for loading the Reporting Lab or when logic requires the loading of a specific participation record.  See logic in method regarding
     * loading the ParticipationDTCollection  for this organization.  The elr can result in
     * the participation to have a substantial amount of Reporting labs with the same
     * subjectEntityUid, therefore need to select based on teh actUid for the observation also.
     */
    public OrganizationContainer loadObject(Long organizationUID, Long actUid) throws DataProcessingException {
        OrganizationContainer ovo = new OrganizationContainer();


        /**
         *  Selects OrganizationDto object
         */

        OrganizationDto pDT = selectOrganization(organizationUID);
        ovo.setTheOrganizationDto(pDT);

        /**
         * Selects OrganizationNameDto Collection
         */

        Collection<OrganizationNameDto> pnColl = selectOrganizationNames(organizationUID);
        ovo.setTheOrganizationNameDtoCollection(pnColl);

        /**
         * Selects EntityIdDT collection
         */

        Collection<EntityIdDto> idColl = selectEntityIDs(organizationUID);
        ovo.setTheEntityIdDtoCollection(idColl);

        /**
         * Selects EntityLocatorParticipationDT collection
         */

        Collection<EntityLocatorParticipationDto> elpColl = selectEntityLocatorParticipations(organizationUID);
        ovo.setTheEntityLocatorParticipationDtoCollection(elpColl);
        //Selects RoleDTcollection
        Collection<RoleDto> roleColl = selectRoleDTCollection(organizationUID);
        ovo.setTheRoleDTCollection(roleColl);



        //SelectsParticipationDTCollection
        Collection<ParticipationDto> parColl = selectParticipationDTCollection(organizationUID, actUid);
        ovo.setTheParticipationDtoCollection(parColl);


        ovo.setItNew(false);
        ovo.setItDirty(false);
        return ovo;
    }

    public OrganizationDto selectOrganization(long organizationUID)   {
        OrganizationDto organizationDto;
        /**
         * Selects organization from organization table
         */
        Organization organizatioModel = this.findOrganizationByUid(organizationUID);
        organizationDto = new OrganizationDto(organizatioModel);
        organizationDto.setItNew(false);
        organizationDto.setItDirty(false);

        logger.debug("return organization object");
        return organizationDto;
    }//end of selecting organization ethnic groups

    /**
     * Selects the  Names of the Organization
     */
    private Collection<OrganizationNameDto> selectOrganizationNames(long organizationUID)   {
        Collection<OrganizationNameDto> returnArrayList = new ArrayList<>();
        List<OrganizationName> listOptional = organizationNameJdbcRepository.findByOrganizationUid(organizationUID);
        List<OrganizationName> organizationNameList = new ArrayList<>();
        if(listOptional != null && !listOptional.isEmpty()) {
            organizationNameList = listOptional;
        }
        for (OrganizationName organizationNameModel : organizationNameList) {
            OrganizationNameDto organizationNameDto = new OrganizationNameDto(organizationNameModel, tz);
            organizationNameDto.setItNew(false);
            organizationNameDto.setItDirty(false);
            returnArrayList.add(organizationNameDto);
        }
        return returnArrayList;

    }

    /**
     * This method is used to retrieve entityID objects for a specific organization.
     **/
    private Collection<EntityIdDto> selectEntityIDs(long organizationUID)   {
        List<EntityId> idListOptional = entityIdJdbcRepository.findEntityIdsActive(organizationUID);
        List<EntityId> idList = new ArrayList<>();
        if (idListOptional != null && !idListOptional.isEmpty()) {
            idList = idListOptional;
        }


        Collection<EntityIdDto> entityIdList = new ArrayList<>();
        for (EntityId entityId : idList) {
            EntityIdDto entityIdDto = new EntityIdDto(entityId);
            entityIdDto.setItNew(false);
            entityIdDto.setItDirty(false);
            entityIdList.add(entityIdDto);
        }
        logger.debug("Return entity id collection");
        return entityIdList;

    }
    @SuppressWarnings("java:S3776")
    private Collection<EntityLocatorParticipationDto> selectEntityLocatorParticipations(long organizationUID)
              {
        Collection<EntityLocatorParticipationDto> entityLocatorParticipationList = new ArrayList<>();
        var res =  entityLocatorJdbcRepository.findByEntityUid(organizationUID);
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        if (res != null && !res.isEmpty()) {
            entityLocatorParticipations = res;
        }

        if (!entityLocatorParticipations.isEmpty()) {
            List<EntityLocatorParticipation> physicalLocators;
            List<EntityLocatorParticipation> postalLocators;
            List<EntityLocatorParticipation> teleLocators;

            physicalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.PHYSICAL))
                    .toList();
            postalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.POSTAL))
                    .toList();
            teleLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.TELE))
                    .toList();

            if (!physicalLocators.isEmpty()) {
                List<PhysicalLocator> existingLocator = entityLocatorJdbcRepository.findByPhysicalLocatorUids(
                        physicalLocators.stream()
                                .map(EntityLocatorParticipation::getLocatorUid)
                                .collect(Collectors.toList()));

                if (!existingLocator.isEmpty()) {
                    for (PhysicalLocator physicalLocator : existingLocator) {
                        EntityLocatorParticipationDto entityLocatorVO = new EntityLocatorParticipationDto();
                        entityLocatorVO.setItNew(false);
                        entityLocatorVO.setItDirty(false);
                        entityLocatorVO.setItDelete(false);
                        PhysicalLocatorDto physicalLocatorDT = new PhysicalLocatorDto(physicalLocator);
                        physicalLocatorDT.setImageTxt(null);
                        entityLocatorVO.setThePhysicalLocatorDto(physicalLocatorDT);
                        entityLocatorParticipationList.add(entityLocatorVO);
                    }
                }
            }
            if (!postalLocators.isEmpty()) {
                var existingLocator = entityLocatorJdbcRepository.findByPostalLocatorUids(
                        postalLocators.stream()
                                .map(EntityLocatorParticipation::getLocatorUid)
                                .collect(Collectors.toList()));
                if (!existingLocator.isEmpty()) {
                    for (PostalLocator postalLocator : existingLocator) {
                        EntityLocatorParticipationDto entityLocatorVO = new EntityLocatorParticipationDto();
                        entityLocatorVO.setItNew(false);
                        entityLocatorVO.setItDirty(false);
                        entityLocatorVO.setItDelete(false);
                        PostalLocatorDto postalLocatorDT = new PostalLocatorDto(postalLocator);
                        entityLocatorVO.setThePostalLocatorDto(postalLocatorDT);
                        entityLocatorParticipationList.add(entityLocatorVO);
                    }
                }
            }
            if (!teleLocators.isEmpty()) {
                var existingLocator = entityLocatorJdbcRepository.findByTeleLocatorUids(
                        teleLocators.stream()
                                .map(EntityLocatorParticipation::getLocatorUid)
                                .collect(Collectors.toList()));
                if (existingLocator != null && !existingLocator.isEmpty()) {
                    for (TeleLocator teleLocator : existingLocator) {
                        EntityLocatorParticipationDto entityLocatorVO = new EntityLocatorParticipationDto();
                        entityLocatorVO.setItNew(false);
                        entityLocatorVO.setItDirty(false);
                        entityLocatorVO.setItDelete(false);
                        TeleLocatorDto teleLocatorDto = new TeleLocatorDto(teleLocator);
                        entityLocatorVO.setTheTeleLocatorDto(teleLocatorDto);
                        entityLocatorParticipationList.add(entityLocatorVO);
                    }
                }
            }
        }

        return entityLocatorParticipationList;
    }


    private Collection<RoleDto> selectRoleDTCollection(long uid)   {
        Collection<RoleDto> retval = new ArrayList<>();
        var res= roleJdbcRepository.findActiveBySubjectEntityUid(uid);
        List<Role> roleList = new ArrayList<>();
        if (res != null && !res.isEmpty()) {
            roleList =res;
        }
        for (Role roleModel : roleList) {
            RoleDto newdt = new RoleDto(roleModel);
            newdt.setItNew(false);
            newdt.setItDirty(false);
            retval.add(newdt);
        }

        return retval;
    }

    /**
     * Loads a partcipation record based on the actUid and subjectEntityUid.
     */
    private Collection<ParticipationDto> selectParticipationDTCollection(Long uid, Long act_uid)
              {
        List<Participation> participationList = new ArrayList<>();

        if (act_uid != null) {
            var result = participationJdbcRepository.selectParticipationBySubjectAndActUid(uid, act_uid);
            if (result != null && !result.isEmpty()) {
                participationList = result;
            }
        } else {
            var result = participationJdbcRepository.selectParticipationBySubjectEntityUid(uid);
            if (result  != null && !result.isEmpty()) {
                participationList = result;
            }
        }

        ArrayList<ParticipationDto> retList = new ArrayList<>();

        for (Participation participation : participationList) {
            ParticipationDto participationDto = new ParticipationDto(participation);
            participationDto.setItNew(false);
            participationDto.setItDirty(false);
            retList.add(participationDto);
        }
        return retList;

    }


    public OrganizationDto prepareVO(OrganizationDto organizationDto, String businessTriggerCd, String tableName, String moduleCd) throws DataProcessingException {
        if (!organizationDto.isItNew() && !organizationDto.isItDirty() && !organizationDto.isItDelete()) {
            throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
        }
        logger.debug("(Boolean.FALSE).equals(new Boolean(theRootDTInterface.tableName)?: {}:theRootDTInterface.moduleCd: {}:businessTriggerCd: {}",tableName, moduleCd, businessTriggerCd);

        if (organizationDto.isItNew() || organizationDto.isItDirty()) {
            long userId = AuthUtil.authUser.getNedssEntryId();
            Timestamp time = TimeStampUtil.getCurrentTimeStamp(tz);
            logger.debug("new entity");
            PrepareEntity prepareEntity = this.getPrepareEntityForOrganization(businessTriggerCd, moduleCd, organizationDto.getOrganizationUid(), tableName);
            organizationDto.setLocalId(prepareEntity.getLocalId());
            organizationDto.setAddUserId(userId);
            organizationDto.setAddTime(time);
            organizationDto.setRecordStatusCd(prepareEntity.getRecordStatusState());
            organizationDto.setStatusCd(prepareEntity.getObjectStatusState());
            organizationDto.setRecordStatusTime(time);
            organizationDto.setStatusTime(time);
            organizationDto.setLastChgTime(time);
            organizationDto.setLastChgUserId(userId);
            organizationDto.setLastChgReasonCd(null);
        }
        return organizationDto;

    }

    public PrepareEntity getPrepareEntityForOrganization(String businessTriggerCd, String moduleCd, Long uid, String tableName) throws DataProcessingException {
        return prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
    }
}