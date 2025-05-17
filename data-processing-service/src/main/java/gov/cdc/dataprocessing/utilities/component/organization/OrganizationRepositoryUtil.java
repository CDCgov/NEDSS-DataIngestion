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
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ParticipationJdbcRepository;
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
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.organization.OrganizationNameRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.organization.OrganizationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
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
    private final OrganizationRepository organizationRepository;
    private final OrganizationNameRepository organizationNameRepository;
    private final EntityRepository entityRepository;
    private final EntityIdRepository entityIdRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;
    private final TeleLocatorRepository teleLocatorRepository;
    private final PostalLocatorRepository postalLocatorRepository;
    private final PhysicalLocatorRepository physicalLocatorRepository;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;
    private final EntityHelper entityHelper;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final ParticipationJdbcRepository participationJdbcRepository;
    private final UidPoolManager uidPoolManager;

    public OrganizationRepositoryUtil(OrganizationRepository organizationRepository,
                                      OrganizationNameRepository organizationNameRepository,
                                      EntityRepository entityRepository,
                                      EntityIdRepository entityIdRepository,
                                      EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                                      RoleRepository roleRepository,
                                      TeleLocatorRepository teleLocatorRepository,
                                      PostalLocatorRepository postalLocatorRepository,
                                      PhysicalLocatorRepository physicalLocatorRepository,
                                      IOdseIdGeneratorWCacheService odseIdGeneratorService, EntityHelper entityHelper,
//                                      ParticipationRepository participationRepository,
                                      PrepareAssocModelHelper prepareAssocModelHelper,
                                      PrepareEntityStoredProcRepository prepareEntityStoredProcRepository,
                                      ParticipationJdbcRepository participationJdbcRepository,
                                      @Lazy UidPoolManager uidPoolManager) {
        this.organizationRepository = organizationRepository;
        this.organizationNameRepository = organizationNameRepository;
        this.entityRepository = entityRepository;
        this.entityIdRepository = entityIdRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.roleRepository = roleRepository;
        this.teleLocatorRepository = teleLocatorRepository;
        this.postalLocatorRepository = postalLocatorRepository;
        this.physicalLocatorRepository = physicalLocatorRepository;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.entityHelper = entityHelper;
//        this.participationRepository = participationRepository;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.prepareEntityStoredProcRepository = prepareEntityStoredProcRepository;
        this.participationJdbcRepository = participationJdbcRepository;
        this.uidPoolManager = uidPoolManager;
    }

    public Organization findOrganizationByUid(Long orgUid) {
        var result = organizationRepository.findById(orgUid);
        return result.orElseGet(Organization::new);

    }

    public long createOrganization(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        Long organizationUid ;
        long oldOrgUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();

        String localUid ;
        var localIdModel = uidPoolManager.getNextUid(LocalIdClass.ORGANIZATION, true);
        organizationUid = localIdModel.getGaTypeUid().getSeedValueNbr();
        localUid = localIdModel.getClassTypeUid().getUidPrefixCd() + localIdModel.getClassTypeUid().getSeedValueNbr() + localIdModel.getClassTypeUid().getUidSuffixCd();

        if (organizationContainer.getTheOrganizationDto().getLocalId() == null || organizationContainer.getTheOrganizationDto().getLocalId().trim().length() == 0) {
            organizationContainer.getTheOrganizationDto().setLocalId(localUid);
        }
        /**
         * Starts inserting a new organization
         */
        // Upper stream require this id to not mutated (must be negative), so falseToNew Method can parse the id correctly
        organizationContainer.getTheOrganizationDto().setOrganizationUid(organizationUid);
        organizationContainer.getTheOrganizationDto().setLocalId(localUid);
        organizationContainer.getTheOrganizationDto().setVersionCtrlNbr(1);
        insertOrganization(organizationContainer);

        if (organizationContainer.getTheOrganizationNameDtoCollection() != null && !organizationContainer.getTheOrganizationNameDtoCollection().isEmpty()) {
            insertOrganizationNames(organizationContainer);
        }
        //NOTE: Upsert EntityID
        if (organizationContainer.getTheEntityIdDtoCollection() != null && !organizationContainer.getTheEntityIdDtoCollection().isEmpty()) {
            createEntityId(organizationContainer);
        }
        //NOTE: Create Entity Locator Participation
        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null && !organizationContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            createEntityLocatorParticipation(organizationContainer);
        }
        //NOTE: Create Role
        if (organizationContainer.getTheRoleDTCollection() != null && !organizationContainer.getTheRoleDTCollection().isEmpty()) {
            createRole(organizationContainer);
        }

        organizationContainer.getTheOrganizationDto().setOrganizationUid(oldOrgUid);

        return organizationUid;
    }

    public void updateOrganization(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        try {
            /**
             * Starts inserting a new organization
             */
            if (organizationContainer == null) {
                throw new DataProcessingException("Organization Container Is Null");
            }
            insertOrganization(organizationContainer);

            if (organizationContainer.getTheOrganizationNameDtoCollection() != null && !organizationContainer.getTheOrganizationNameDtoCollection().isEmpty()) {
                insertOrganizationNames(organizationContainer);
            }
            //NOTE: Upsert EntityID
            if (organizationContainer.getTheEntityIdDtoCollection() != null && !organizationContainer.getTheEntityIdDtoCollection().isEmpty()) {
                createEntityId(organizationContainer);
            }
            //NOTE: Create Entity Locator Participation
            if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null && !organizationContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
                createEntityLocatorParticipation(organizationContainer);
            }
            //NOTE: Create Role
            if (organizationContainer.getTheRoleDTCollection() != null && !organizationContainer.getTheRoleDTCollection().isEmpty()) {
                createRole(organizationContainer);
            }

        } catch (Exception ex) {
            logger.error("Error while creating Organization {}", ex.getMessage());
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    private Long insertOrganization(OrganizationContainer organizationContainer) throws DataProcessingException {
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        Long organizationUid;
        try {
            /**
             * Inserts into entity table for organization
             */

            organizationUid = organizationDto.getOrganizationUid();
            EntityODSE entityModel = new EntityODSE();
            entityModel.setEntityUid(organizationUid);
            entityModel.setClassCd(ORG);
            //// New code
            entityRepository.save(entityModel);
            organizationDto.setCd(entityModel.getClassCd());

            Organization organization = new Organization(organizationDto);
            organizationRepository.save(organization);
            organizationDto.setItNew(false);
            organizationDto.setItDirty(false);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return organizationUid;
    }

    private void insertOrganizationNames(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        logger.debug("insertOrganizationNames(long organizationUID, Collection<Object>  organizationNames)");
        Iterator<OrganizationNameDto> anIterator;
        try {
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
                organizationNameRepository.save(orgName);

                orgNameDT.setOrganizationUid(organizationUID);
                orgNameDT.setItNew(false);
                orgNameDT.setItDirty(false);
            }
        } catch (Exception ex) {
            logger.error(" Exception while inserting Organization names into ORGINIZATION_NAME_TABLE: {}", ex.getMessage());
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        logger.debug("OrganizationRepositoryUtil - Done inserting all Organization names");
    }//end of inserting Organization names

    private void createEntityId(OrganizationContainer organizationContainer) throws DataProcessingException {
        ArrayList<EntityIdDto> entityList = (ArrayList<EntityIdDto>) organizationContainer.getTheEntityIdDtoCollection();
        try {
            Long pUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();
            for (EntityIdDto entityIdDto : entityList) {
                entityIdDto.setEntityUid(pUid);
                entityIdRepository.save(new EntityId(entityIdDto, tz));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityLocatorParticipation(OrganizationContainer ovo) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto> entityLocatorList = (ArrayList<EntityLocatorParticipationDto>) ovo.getTheEntityLocatorParticipationDtoCollection();
        try {
            for (EntityLocatorParticipationDto entityLocatorDT : entityLocatorList) {
                var localUid =  uidPoolManager.getNextUid(LocalIdClass.ORGANIZATION, true);
                if (entityLocatorDT.getClassCd().equals(NEDSSConstant.PHYSICAL) && entityLocatorDT.getThePhysicalLocatorDto() != null) {
                    entityLocatorDT.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                    physicalLocatorRepository.save(new PhysicalLocator(entityLocatorDT.getThePhysicalLocatorDto()));
                }
                if (entityLocatorDT.getClassCd().equals(NEDSSConstant.POSTAL) && entityLocatorDT.getThePostalLocatorDto() != null) {
                    entityLocatorDT.getThePostalLocatorDto().setPostalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                    postalLocatorRepository.save(new PostalLocator(entityLocatorDT.getThePostalLocatorDto()));
                }
                if (entityLocatorDT.getClassCd().equals(NEDSSConstant.TELE) && entityLocatorDT.getTheTeleLocatorDto() != null) {
                    entityLocatorDT.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                    teleLocatorRepository.save(new TeleLocator(entityLocatorDT.getTheTeleLocatorDto()));
                }
                entityLocatorDT.setEntityUid(ovo.getTheOrganizationDto().getOrganizationUid());
                entityLocatorDT.setLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());

                if (entityLocatorDT.getVersionCtrlNbr() == null) {
                    entityLocatorDT.setVersionCtrlNbr(1);
                }
                entityLocatorParticipationRepository.save(new EntityLocatorParticipation(entityLocatorDT, tz));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createRole(OrganizationContainer ovo) throws DataProcessingException {
        ArrayList<RoleDto> roleList = (ArrayList<RoleDto>) ovo.getTheRoleDTCollection();
        try {
            for (RoleDto obj : roleList) {
                roleRepository.save(new Role(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
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
        try {
            organizationUID = setOrganizationInternal(organizationContainer, businessTriggerCd);
        } catch (Exception e) {
            logger.error("OrganizationRepositoryUtil.setOrganization: Exception: {}", e.getMessage());
            throw new DataProcessingException(e.getMessage(), e);
        }
        return organizationUID;
    }
    @SuppressWarnings("java:S3776")
    public Long setOrganizationInternal(OrganizationContainer organizationContainer, String businessTriggerCd) throws DataProcessingException {
        Long organizationUID;
        try {
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
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }

        logger.debug("EntityControllerEJB.setOrganization - ouid  =  {}", organizationUID);
        return organizationUID;
    }

    private void prepareOrganizationNameBeforePersistence(
            OrganizationContainer organizationContainer) throws DataProcessingException {
        try {
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
        } catch (Exception e) {
            logger.error("prepareOrganizationNameBeforePersistence: {}", e.getMessage());
            throw new DataProcessingException(e.getMessage(), e);
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

    public OrganizationDto selectOrganization(long organizationUID) throws DataProcessingException {
        OrganizationDto organizationDto;
        /**
         * Selects organization from organization table
         */
        try {
            Organization organizatioModel = this.findOrganizationByUid(organizationUID);
            organizationDto = new OrganizationDto(organizatioModel);
            organizationDto.setItNew(false);
            organizationDto.setItDirty(false);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        logger.debug("return organization object");
        return organizationDto;
    }//end of selecting organization ethnic groups

    /**
     * Selects the  Names of the Organization
     */
    private Collection<OrganizationNameDto> selectOrganizationNames(long organizationUID) throws DataProcessingException {
        Collection<OrganizationNameDto> returnArrayList = new ArrayList<>();
        try {
            Optional<List<OrganizationName>> listOptional = organizationNameRepository.findByOrganizationUid(organizationUID);
            List<OrganizationName> organizationNameList = new ArrayList<>();
            if(listOptional.isPresent()) {
                organizationNameList = listOptional.get();
            }
            for (OrganizationName organizationNameModel : organizationNameList) {
                OrganizationNameDto organizationNameDto = new OrganizationNameDto(organizationNameModel, tz);
                organizationNameDto.setItNew(false);
                organizationNameDto.setItDirty(false);
                returnArrayList.add(organizationNameDto);
            }
            return returnArrayList;
        } catch (Exception ex) {
            logger.error("Exception while selection " +
                    "Organization names; uid = " + organizationUID, ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    /**
     * This method is used to retrieve entityID objects for a specific organization.
     **/
    private Collection<EntityIdDto> selectEntityIDs(long organizationUID) throws DataProcessingException {
        try {
            Optional<List<EntityId>> idListOptional = this.entityIdRepository.findByEntityUid(organizationUID);
            List<EntityId> idList = new ArrayList<>();
            if (idListOptional.isPresent()) {
                idList = idListOptional.get();
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
        } catch (Exception ex) {
            logger.error("Exception while selection " +
                    "entity ids; uid = " + organizationUID, ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }
    @SuppressWarnings("java:S3776")
    private Collection<EntityLocatorParticipationDto> selectEntityLocatorParticipations(long organizationUID)
            throws DataProcessingException {
        Collection<EntityLocatorParticipationDto> entityLocatorParticipationList = new ArrayList<>();
        try {
            var res =  entityLocatorParticipationRepository.findByParentUid(organizationUID);
            List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
            if (res.isPresent()) {
                entityLocatorParticipations = res.get();
            }

            if (!entityLocatorParticipations.isEmpty()) {
                List<EntityLocatorParticipation> physicalLocators;
                List<EntityLocatorParticipation> postalLocators;
                List<EntityLocatorParticipation> teleLocators;

                physicalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                                .equalsIgnoreCase(NEDSSConstant.PHYSICAL))
                        .collect(Collectors.toList());
                postalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                                .equalsIgnoreCase(NEDSSConstant.POSTAL))
                        .collect(Collectors.toList());
                teleLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                                .equalsIgnoreCase(NEDSSConstant.TELE))
                        .collect(Collectors.toList());

                if (!physicalLocators.isEmpty()) {
                    Optional<List<PhysicalLocator>> existingLocator = physicalLocatorRepository.findByPhysicalLocatorUids(
                            physicalLocators.stream()
                                    .map(EntityLocatorParticipation::getLocatorUid)
                                    .collect(Collectors.toList()));

                    if (existingLocator.isPresent()) {
                        List<PhysicalLocator> physicalLocatorList = existingLocator.get();
                        for (PhysicalLocator physicalLocator : physicalLocatorList) {
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
                    var existingLocator = postalLocatorRepository.findByPostalLocatorUids(
                            postalLocators.stream()
                                    .map(EntityLocatorParticipation::getLocatorUid)
                                    .collect(Collectors.toList()));
                    if (existingLocator.isPresent()) {
                        List<PostalLocator> postalLocatorList = existingLocator.get();
                        for (PostalLocator postalLocator : postalLocatorList) {
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
                    var existingLocator = teleLocatorRepository.findByTeleLocatorUids(
                            teleLocators.stream()
                                    .map(EntityLocatorParticipation::getLocatorUid)
                                    .collect(Collectors.toList()));
                    if (existingLocator.isPresent()) {
                        List<TeleLocator> teleLocatorList = existingLocator.get();
                        for (TeleLocator teleLocator : teleLocatorList) {
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
        } catch (Exception ex) {
            logger.error("Exception selectEntityLocatorParticipations " +
                    "entity id; uid = " + organizationUID, ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return entityLocatorParticipationList;
    }


    private Collection<RoleDto> selectRoleDTCollection(long uid) throws DataProcessingException {
        Collection<RoleDto> retval = new ArrayList<>();
        try {
            var res =  roleRepository.findBySubjectEntityUid(uid);
            List<Role> roleList = new ArrayList<>();
            if (res.isPresent()) {
                roleList =res.get();
            }
            for (Role roleModel : roleList) {
                RoleDto newdt = new RoleDto(roleModel);
                newdt.setItNew(false);
                newdt.setItDirty(false);
                retval.add(newdt);
            }
        } catch (Exception se) {
            logger.error("Error: selectRoleDTCollection while selecting role \n" + se.getMessage(), se);
            throw new DataProcessingException(se.getMessage());
        }
        return retval;
    }

    /**
     * Loads a partcipation record based on the actUid and subjectEntityUid.
     */
    private Collection<ParticipationDto> selectParticipationDTCollection(Long uid, Long act_uid)
            throws DataProcessingException {
        try {
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
        } catch (Exception se) {
            logger.error("Exception selectParticipation = " + se.getMessage(), se);
            throw new DataProcessingException("Error: Exception while selecting \n" +
                    se.getMessage());
        }
    }

    /**
     * This method is used to prepare Dirty Acts,Dirty Entities,New Acts And New Entities depending
     * you want to edit,delete or create records
     *
     * @param organizationDto   -- The DT to be prepared
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface -- the prepared DT(System attribute Set)
     * @throws DataProcessingException
     */
    public OrganizationDto prepareVO(OrganizationDto organizationDto, String businessTriggerCd, String tableName, String moduleCd) throws DataProcessingException {
        try {
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
        } catch (Exception e) {
            logger.error("Exception in PrepareVOUtils.prepareVO: LocalID: " + organizationDto.getLocalId() + ", businessTriggerCd: " + businessTriggerCd + ", tableName: " + tableName + ", " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public PrepareEntity getPrepareEntityForOrganization(String businessTriggerCd, String moduleCd, Long uid, String tableName) throws DataProcessingException {
        try {
            return prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
        } catch (Exception ex) {
            logger.error("Exception in getPrepareEntityForOrganization for businessTriggerCd={} moduleCd={} uid:{} tableName={}: ERROR = {}",businessTriggerCd,moduleCd, uid,tableName, ex.getMessage());
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }
}