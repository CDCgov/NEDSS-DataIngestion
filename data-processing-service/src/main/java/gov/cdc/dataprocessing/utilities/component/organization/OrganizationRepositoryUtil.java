package gov.cdc.dataprocessing.utilities.component.organization;

import gov.cdc.dataprocessing.constant.elr.DataTables;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
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
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.other.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrganizationRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationRepositoryUtil.class);
    private final static String ORGANIZATION = "ORGANIZATION";
    /**
     * Organization Entity Code
     */
    public final static String ORG = "ORG";
    private final OrganizationRepository organizationRepository;
    private final OrganizationNameRepository organizationNameRepository;
    private final EntityRepository entityRepository;
    private final EntityIdRepository entityIdRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;
    private final TeleLocatorRepository teleLocatorRepository;
    private final PostalLocatorRepository postalLocatorRepository;
    private final PhysicalLocatorRepository physicalLocatorRepository;
    private final IOdseIdGeneratorService odseIdGeneratorService;
    private final EntityHelper entityHelper;
    private final ParticipationRepository participationRepository;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;

    public OrganizationRepositoryUtil(OrganizationRepository organizationRepository,
                                      OrganizationNameRepository organizationNameRepository,
                                      EntityRepository entityRepository,
                                      EntityIdRepository entityIdRepository,
                                      EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                                      RoleRepository roleRepository,
                                      TeleLocatorRepository teleLocatorRepository,
                                      PostalLocatorRepository postalLocatorRepository,
                                      PhysicalLocatorRepository physicalLocatorRepository,
                                      IOdseIdGeneratorService odseIdGeneratorService,
                                      EntityHelper entityHelper,
                                      ParticipationRepository participationRepository,
                                      PrepareAssocModelHelper prepareAssocModelHelper,
                                      PrepareEntityStoredProcRepository prepareEntityStoredProcRepository) {
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
        this.participationRepository = participationRepository;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.prepareEntityStoredProcRepository = prepareEntityStoredProcRepository;
    }

    @Transactional
    public Organization findOrganizationByUid(Long orgUid) {
        var result = organizationRepository.findById(orgUid);
        return result.get();
    }

    @Transactional
    public long createOrganization(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        Long organizationUid = 121212L;
        long oldOrgUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();
        try {
            String localUid = "";
            LocalUidGenerator localIdModel = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION);
            organizationUid = localIdModel.getSeedValueNbr();
            logger.debug("createOrganization organizationUid SeedValueNbr: {}",organizationUid);
            localUid = localIdModel.getUidPrefixCd() + organizationUid + localIdModel.getUidSuffixCd();

            if (organizationContainer.getTheOrganizationDto().getLocalId() == null || organizationContainer.getTheOrganizationDto().getLocalId().trim().length() == 0) {
                organizationContainer.getTheOrganizationDto().setLocalId(localUid);
            }
            /**
             * Starts inserting a new organization
             */
            if (organizationContainer != null) {
                // Upper stream require this id to not mutated (must be negative), so falseToNew Method can parse the id correctly
                organizationContainer.getTheOrganizationDto().setOrganizationUid(Long.valueOf(organizationUid));
                organizationContainer.getTheOrganizationDto().setLocalId(localUid);
                organizationContainer.getTheOrganizationDto().setVersionCtrlNbr(Integer.valueOf(1));
                insertOrganization(organizationContainer);
            }
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

        } catch (Exception ex) {
            logger.error("Error while creating Organization", ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return organizationUid.longValue();
    }

    @Transactional
    public void updateOrganization(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        try {
            /**
             * Starts inserting a new organization
             */
            if (organizationContainer != null) {
                insertOrganization(organizationContainer);
            }
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
            logger.error("Error while creating Organization", ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    private Long insertOrganization(OrganizationContainer organizationContainer) throws DataProcessingException {
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        Long organizationUid = 0L;//new Long(0);
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

            logger.info("OrganizationDAOImpl - after save Entity");
            if (organizationDto != null) {
                Organization organization = new Organization(organizationDto);
                organizationRepository.save(organization);
                organizationDto.setItNew(false);
                organizationDto.setItDirty(false);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return organizationUid;
    }

    private void insertOrganizationNames(OrganizationContainer organizationContainer)
            throws DataProcessingException {
        logger.debug("insertOrganizationNames(long organizationUID, Collection<Object>  organizationNames)");
        Iterator<OrganizationNameDto> anIterator = null;
        try {
            long organizationUID = organizationContainer.getTheOrganizationDto().getOrganizationUid().longValue();
            Collection<OrganizationNameDto> organizationNames = organizationContainer.getTheOrganizationNameDtoCollection();
            /**
             * Inserts Organization names
             */
            anIterator = organizationNames.iterator();
            while (anIterator.hasNext()) {
                OrganizationNameDto orgNameDT = anIterator.next();

                if (orgNameDT.getOrganizationNameSeq() == null)
                    orgNameDT.setOrganizationNameSeq(Integer.valueOf(3));

                if (orgNameDT != null) {
                    orgNameDT.setOrganizationUid(organizationUID);
                    OrganizationName orgName = new OrganizationName(orgNameDT);
                    // Save Organization Name records
                    organizationNameRepository.save(orgName);

                    orgNameDT.setOrganizationUid(Long.valueOf(organizationUID));
                    orgNameDT.setItNew(false);
                    orgNameDT.setItDirty(false);
                }
            }
        } catch (Exception ex) {
            logger.error(" Exception while inserting " +
                    "Organization names into ORGINIZATION_NAME_TABLE: \n", ex);
            throw new DataProcessingException(ex.toString());
        }
        logger.debug("OrganizationRepositoryUtil - Done inserting all Organization names");
    }//end of inserting Organization names

    private void createEntityId(OrganizationContainer organizationContainer) throws DataProcessingException {
        ArrayList<EntityIdDto> entityList = (ArrayList<EntityIdDto>) organizationContainer.getTheEntityIdDtoCollection();
        try {
            Long pUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).setEntityUid(pUid);
                entityIdRepository.save(new EntityId(entityList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityLocatorParticipation(OrganizationContainer ovo) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto> entityLocatorList = (ArrayList<EntityLocatorParticipationDto>) ovo.getTheEntityLocatorParticipationDtoCollection();
        try {
            for (int i = 0; i < entityLocatorList.size(); i++) {
                EntityLocatorParticipationDto entityLocatorDT = entityLocatorList.get(i);
                LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION);
//                Long uniqueId = UniqueIdGenerator.generateUniqueId();
                if (entityLocatorDT.getClassCd().equals(NEDSSConstant.PHYSICAL) && entityLocatorDT.getThePhysicalLocatorDto() != null) {
                    entityLocatorDT.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                    physicalLocatorRepository.save(new PhysicalLocator(entityLocatorDT.getThePhysicalLocatorDto()));
                }
                if (entityLocatorDT.getClassCd().equals(NEDSSConstant.POSTAL) && entityLocatorDT.getThePostalLocatorDto() != null) {
                    entityLocatorDT.getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                    postalLocatorRepository.save(new PostalLocator(entityLocatorDT.getThePostalLocatorDto()));
                }
                if (entityLocatorDT.getClassCd().equals(NEDSSConstant.TELE) && entityLocatorDT.getTheTeleLocatorDto() != null) {
                    entityLocatorDT.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                    teleLocatorRepository.save(new TeleLocator(entityLocatorDT.getTheTeleLocatorDto()));
                }
                entityLocatorDT.setEntityUid(ovo.getTheOrganizationDto().getOrganizationUid());
                entityLocatorDT.setLocatorUid(localUid.getSeedValueNbr());

                if (entityLocatorDT.getVersionCtrlNbr() == null) {
                    entityLocatorDT.setVersionCtrlNbr(1);
                }
                entityLocatorParticipationRepository.save(new EntityLocatorParticipation(entityLocatorDT));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createRole(OrganizationContainer ovo) throws DataProcessingException {
        ArrayList<RoleDto> roleList = (ArrayList<RoleDto>) ovo.getTheRoleDTCollection();
        try {
            for (int i = 0; i < roleList.size(); i++) {
                RoleDto obj = roleList.get(i);
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
     * @roseuid 3E6E4E05003E
     * @J2EE_METHOD -- setOrganization
     */
    @Transactional
    public Long setOrganization(OrganizationContainer organizationContainer,
                                String businessTriggerCd)
            throws DataProcessingException {
        Long organizationUID;
        try {
            organizationUID = setOrganizationInternal(organizationContainer, businessTriggerCd);
        } catch (Exception e) {
            logger.error("OrganizationRepositoryUtil.setOrganization: Exception: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        return organizationUID;
    }

    private Long setOrganizationInternal(OrganizationContainer organizationContainer, String businessTriggerCd) throws DataProcessingException {
        Long organizationUID = Long.valueOf(-1);
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

//                Organization organization = null;
//                NedssUtils nedssUtils = new NedssUtils();
//                Object obj = nedssUtils.lookupBean(JNDINames.ORGANIZATIONEJB);
//                logger.debug("EntityControllerEJB.setOrganization - lookup = "
//                        + obj.toString());
//                OrganizationHome home = (OrganizationHome) PortableRemoteObject
//                        .narrow(obj, OrganizationHome.class);
//                logger.debug("EntityControllerEJB.setOrganization - Found OrganizationHome: "
//                        + home);

                if (organizationContainer.isItNew()) {
//                    organization = home.create(organizationContainer);
                    organizationUID = createOrganization(organizationContainer);
                    logger.debug(" OrganizationRepositoryUtil.setOrganization -  Organization Created");
                    logger.debug("OrganizationRepositoryUtil.setOrganization {}", organizationUID);
//                    organizationUID = organization.getOrganizationVO()
//                            .getTheOrganizationDto().getOrganizationUid();
                } else {
//                    organization = home.findByPrimaryKey(organizationContainer
//                            .getTheOrganizationDto().getOrganizationUid());
//                    organization.setOrganizationVO(organizationContainer);
//                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Updated");
//                    organizationUID = organization.getOrganizationVO()
//                            .getTheOrganizationDto().getOrganizationUid();
                    updateOrganization(organizationContainer);
                    organizationUID = organizationContainer
                            .getTheOrganizationDto().getOrganizationUid();
                    logger.debug(" OrganizationRepositoryUtil.setOrganizationInternal -  Organization Updated");
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }

        logger.debug("EntityControllerEJB.setOrganization - ouid  =  "
                + organizationUID);
        return organizationUID;
    }

    private void prepareOrganizationNameBeforePersistence(
            OrganizationContainer organizationContainer) throws DataProcessingException {
        try {
            Collection<OrganizationNameDto> namesCollection = null;
            Iterator<OrganizationNameDto> anIterator = null;
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
                    logger.error("Exception setting the Organization Name: " + selectedName);
                }
            }
        } catch (Exception e) {
            logger.error("prepareOrganizationNameBeforePersistence: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * Specifically for loading the Reporting Lab or when logic requires the loading of a specific participation record.  See logic in method regarding
     * loading the ParticipationDTCollection  for this organization.  The elr can result in
     * the participation to have a substantial amount of Reporting labs with the same
     * subjectEntityUid, therefore need to select based on teh actUid for the observation also.
     *
     * @param organizationUID
     * @param actUid
     * @return
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


       // if (actUid != null) {
            //SelectsParticipationDTCollection
            Collection<ParticipationDto> parColl = selectParticipationDTCollection(organizationUID, actUid);
            ovo.setTheParticipationDtoCollection(parColl);
      //  }

        ovo.setItNew(false);
        ovo.setItDirty(false);
        return ovo;
    }

    private OrganizationDto selectOrganization(long organizationUID) throws DataProcessingException {
        OrganizationDto organizationDto = null;
        /**
         * Selects organization from organization table
         */
        try {
            Organization organizatioModel = findOrganizationByUid(Long.valueOf(organizationUID));
            organizationDto = new OrganizationDto(organizatioModel);
            organizationDto.setItNew(false);
            organizationDto.setItDirty(false);
        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }
        logger.debug("return organization object");
        return organizationDto;
    }//end of selecting organization ethnic groups

    /**
     * Selects the  Names of the Organization
     *
     * @param organizationUID long   the OrganizationUID
     * @return Collection
     * @throws DataProcessingException
     * @throws DataProcessingException
     */
    private Collection<OrganizationNameDto> selectOrganizationNames(long organizationUID) throws DataProcessingException {
        Collection<OrganizationNameDto> returnArrayList = new ArrayList<>();
        try {
            Optional<List<OrganizationName>> listOptional = organizationNameRepository.findByOrganizationUid(organizationUID);
            List<OrganizationName> organizationNameList = listOptional.get();
            for (Iterator<OrganizationName> anIterator = organizationNameList.iterator(); anIterator.hasNext(); ) {
                OrganizationName organizationNameModel = anIterator.next();
                OrganizationNameDto organizationNameDto = new OrganizationNameDto(organizationNameModel);
                organizationNameDto.setItNew(false);
                organizationNameDto.setItDirty(false);
                returnArrayList.add(organizationNameDto);
            }
            return returnArrayList;
        } catch (Exception ex) {
            logger.error("Exception while selection " +
                    "Organization names; uid = " + organizationUID, ex);
            throw new DataProcessingException(ex.toString());
        }
    }

    /**
     * This method is used to retrieve entityID objects for a specific organization.
     *
     * @param organizationUID the long
     * @throws DataProcessingException
     * @J2EE_METHOD --  selectEntityIDs
     **/
    private Collection<EntityIdDto> selectEntityIDs(long organizationUID) throws DataProcessingException {
        try {
            Optional<List<EntityId>> idListOptional = entityIdRepository.findByEntityUid(organizationUID);
            List<EntityId> idList = idListOptional.get();

            Collection<EntityIdDto> entityIdList = new ArrayList<>();
            for (Iterator<EntityId> anIterator = idList.iterator(); anIterator.hasNext(); ) {
                EntityId entityId = anIterator.next();
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
            throw new DataProcessingException(ex.toString());
        }
    }

    private Collection<EntityLocatorParticipationDto> selectEntityLocatorParticipations(long organizationUID)
            throws DataProcessingException {
        Collection<EntityLocatorParticipationDto> entityLocatorParticipationList = new ArrayList<>();
        try {
            List<EntityLocatorParticipation> entityLocatorParticipations = entityLocatorParticipationRepository.findByParentUid(organizationUID).get();

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
                                    .map(x -> x.getLocatorUid())
                                    .collect(Collectors.toList()));

                    if (existingLocator.isPresent()) {
                        List<PhysicalLocator> physicalLocatorList = existingLocator.get();
                        for (int j = 0; j < physicalLocatorList.size(); j++) {
                            EntityLocatorParticipationDto entityLocatorVO = new EntityLocatorParticipationDto();
                            entityLocatorVO.setItNew(false);
                            entityLocatorVO.setItDirty(false);
                            entityLocatorVO.setItDelete(false);
                            PhysicalLocatorDto physicalLocatorDT = new PhysicalLocatorDto(physicalLocatorList.get(j));
                            physicalLocatorDT.setImageTxt(null);
                            entityLocatorVO.setThePhysicalLocatorDto(physicalLocatorDT);
                            entityLocatorParticipationList.add(entityLocatorVO);
                        }
                    }
                }
                if (!postalLocators.isEmpty()) {
                    var existingLocator = postalLocatorRepository.findByPostalLocatorUids(
                            postalLocators.stream()
                                    .map(x -> x.getLocatorUid())
                                    .collect(Collectors.toList()));
                    if (existingLocator.isPresent()) {
                        List<PostalLocator> postalLocatorList = existingLocator.get();
                        for (int j = 0; j < postalLocatorList.size(); j++) {
                            EntityLocatorParticipationDto entityLocatorVO = new EntityLocatorParticipationDto();
                            entityLocatorVO.setItNew(false);
                            entityLocatorVO.setItDirty(false);
                            entityLocatorVO.setItDelete(false);
                            PostalLocatorDto postalLocatorDT = new PostalLocatorDto(postalLocatorList.get(j));
                            entityLocatorVO.setThePostalLocatorDto(postalLocatorDT);
                            entityLocatorParticipationList.add(entityLocatorVO);
                        }
                    }
                }
                if (!teleLocators.isEmpty()) {
                    var existingLocator = teleLocatorRepository.findByTeleLocatorUids(
                            teleLocators.stream()
                                    .map(x -> x.getLocatorUid())
                                    .collect(Collectors.toList()));
                    if (existingLocator.isPresent()) {
                        List<TeleLocator> teleLocatorList = existingLocator.get();
                        for (int j = 0; j < teleLocatorList.size(); j++) {
                            EntityLocatorParticipationDto entityLocatorVO = new EntityLocatorParticipationDto();
                            entityLocatorVO.setItNew(false);
                            entityLocatorVO.setItDirty(false);
                            entityLocatorVO.setItDelete(false);
                            TeleLocatorDto teleLocatorDto = new TeleLocatorDto(teleLocatorList.get(j));
                            entityLocatorVO.setTheTeleLocatorDto(teleLocatorDto);
                            entityLocatorParticipationList.add(entityLocatorVO);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Exception selectEntityLocatorParticipations " +
                    "entity id; uid = " + organizationUID, ex);
            throw new DataProcessingException(ex.toString());
        }
        return entityLocatorParticipationList;
    }

    /**
     * @param uid, sqlStatement
     * @return java.util.Collection<Object>
     * @throws DataProcessingException
     * @roseuid 3C434E6C0115
     */
    private Collection<RoleDto> selectRoleDTCollection(long uid) throws DataProcessingException {
        Collection<RoleDto> retval = new ArrayList<>();
        try {
            List<Role> roleList = roleRepository.findBySubjectEntityUid(Long.valueOf(uid)).get();
            for (Iterator<Role> anIterator = roleList.iterator(); anIterator.hasNext(); ) {
                Role roleModel = anIterator.next();
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
     *
     * @param uid
     * @param act_uid
     * @return
     * @throws DataProcessingException
     */
    private Collection<ParticipationDto> selectParticipationDTCollection(Long uid, Long act_uid)
            throws DataProcessingException {
        try {
            List<Participation> participationList = new ArrayList<>();

            if (act_uid != null) {
                var result = participationRepository.findBySubjectEntityUidAndActUid(uid, act_uid);
                if (result.isPresent() && !result.get().isEmpty()) {
                    participationList = result.get();
                }
            } else {
                var result = participationRepository.findBySubjectEntityUid(uid);
                if (result.isPresent() && !result.get().isEmpty()) {
                    participationList = result.get();
                }
            }

            ArrayList<ParticipationDto> retList = new ArrayList<>();

            for (Iterator<Participation> anIterator = participationList.iterator();
                 anIterator.hasNext(); ) {
                ParticipationDto participationDto = new ParticipationDto(anIterator.next());
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
            if (organizationDto.isItNew() == false && organizationDto.isItDirty() == false && organizationDto.isItDelete() == false) {
                throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
            }
            logger.debug("(Boolean.FALSE).equals(new Boolean(theRootDTInterface.tableName)?:" + tableName + ":theRootDTInterface.moduleCd:" + moduleCd + ":businessTriggerCd:" + businessTriggerCd);

            if (organizationDto.isItNew() || organizationDto.isItDirty()) {
                long userId = AuthUtil.authUser.getAuthUserUid();
                Timestamp time = new Timestamp(new Date().getTime());
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

    @Transactional
    public PrepareEntity getPrepareEntityForOrganization(String businessTriggerCd, String moduleCd, Long uid, String tableName) throws DataProcessingException {
        try {
            return prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
        } catch (Exception ex) {
            logger.error("Exception in getPrepareEntityForOrganization for businessTriggerCd=" + businessTriggerCd + " moduleCd=" + moduleCd + " uid:" + uid + " tableName=" + tableName + ": ERROR = " + ex);
            throw new DataProcessingException(ex.toString(), ex);
        }
    }
}