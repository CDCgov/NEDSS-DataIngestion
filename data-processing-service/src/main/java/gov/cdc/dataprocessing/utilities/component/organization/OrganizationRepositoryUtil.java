package gov.cdc.dataprocessing.utilities.component.organization;

import gov.cdc.dataprocessing.constant.elr.DataTables;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationNameDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PrepareEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.organization.OrganizationNameRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.organization.OrganizationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.core.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
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
    private final LocalUidGeneratorRepository localUidGeneratorRepository;
    private final IOdseIdGeneratorService odseIdGeneratorService;
    private final EntityHelper entityHelper;
    private final ParticipationRepository participationRepository;
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
                                      LocalUidGeneratorRepository localUidGeneratorRepository,
                                      IOdseIdGeneratorService odseIdGeneratorService,
                                      EntityHelper entityHelper,
                                      ParticipationRepository participationRepository,
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
        this.localUidGeneratorRepository = localUidGeneratorRepository;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.entityHelper = entityHelper;
        this.participationRepository = participationRepository;
        this.prepareEntityStoredProcRepository = prepareEntityStoredProcRepository;
    }

    @Transactional
    public Organization findOrganizationByUid(Long orgUid) {
        var result = organizationRepository.findById(orgUid);
        return result.get();
    }

    @Transactional
    public long createOrganization(OrganizationVO organizationVO)
            throws DataProcessingException {
        Long organizationUid = 121212L;
        try {
            //TODO: Implement unique id generator here
            String localUid = "Unique Id here";
            LocalUidGenerator localIdModel = localUidGeneratorRepository.findById(ORGANIZATION).get();
            organizationUid = localIdModel.getSeedValueNbr();
            System.out.println("createOrganization organizationUid SeedValueNbr:" + organizationUid);
            localUid = localIdModel.getUidPrefixCd() + organizationUid + localIdModel.getUidSuffixCd();
            System.out.println("-----createOrganization localUid:" + localUid + " localIdModel obj:" + localIdModel);
            LocalUidGenerator newGen = new LocalUidGenerator();
            newGen.setClassNameCd(localIdModel.getClassNameCd());
            newGen.setTypeCd(localIdModel.getTypeCd());
            newGen.setSeedValueNbr(localIdModel.getSeedValueNbr() + 1);
            newGen.setUidPrefixCd(localIdModel.getUidPrefixCd());
            newGen.setUidSuffixCd(localIdModel.getUidSuffixCd());
            localUidGeneratorRepository.save(newGen);
            if (organizationVO.getTheOrganizationDT().getLocalId() == null || organizationVO.getTheOrganizationDT().getLocalId().trim().length() == 0) {
                organizationVO.getTheOrganizationDT().setLocalId(localUid);
            }
            /**
             * Starts inserting a new organization
             */
            if (organizationVO != null) {
                try {
                    organizationVO.getTheOrganizationDT().setOrganizationUid(Long.valueOf(organizationUid));
                    organizationVO.getTheOrganizationDT().setLocalId(localUid);
                    organizationVO.getTheOrganizationDT().setVersionCtrlNbr(Integer.valueOf(1));
                    insertOrganization(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            if (organizationVO.getTheOrganizationNameDTCollection() != null && !organizationVO.getTheOrganizationNameDTCollection().isEmpty()) {
                try {
                    insertOrganizationNames(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            //NOTE: Upsert EntityID
            if (organizationVO.getTheEntityIdDtoCollection() != null && !organizationVO.getTheEntityIdDtoCollection().isEmpty()) {
                try {
                    createEntityId(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            //NOTE: Create Entity Locator Participation
            if (organizationVO.getTheEntityLocatorParticipationDtoCollection() != null && !organizationVO.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
                try {
                    createEntityLocatorParticipation(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            //NOTE: Create Role
            if (organizationVO.getTheRoleDTCollection() != null && !organizationVO.getTheRoleDTCollection().isEmpty()) {
                try {
                    createRole(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
        } catch (Exception ex) {
            logger.error("Error while creating Organization", ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return organizationUid.longValue();
    }

    @Transactional
    public void updateOrganization(OrganizationVO organizationVO)
            throws DataProcessingException {
        try {
            /**
             * Starts inserting a new organization
             */
            if (organizationVO != null) {
                try {
                    insertOrganization(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            if (organizationVO.getTheOrganizationNameDTCollection() != null && !organizationVO.getTheOrganizationNameDTCollection().isEmpty()) {
                try {
                    insertOrganizationNames(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            //NOTE: Upsert EntityID
            if (organizationVO.getTheEntityIdDtoCollection() != null && !organizationVO.getTheEntityIdDtoCollection().isEmpty()) {
                try {
                    createEntityId(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            //NOTE: Create Entity Locator Participation
            if (organizationVO.getTheEntityLocatorParticipationDtoCollection() != null && !organizationVO.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
                try {
                    createEntityLocatorParticipation(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            //NOTE: Create Role
            if (organizationVO.getTheRoleDTCollection() != null && !organizationVO.getTheRoleDTCollection().isEmpty()) {
                try {
                    createRole(organizationVO);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
        } catch (Exception ex) {
            logger.error("Error while creating Organization", ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    private Long insertOrganization(OrganizationVO organizationVO) throws DataProcessingException {
        OrganizationDT organizationDT = organizationVO.getTheOrganizationDT();
        Long organizationUid = 0L;//new Long(0);
        try {
            /**
             * Inserts into entity table for organization
             */
            //uidGen = new UidGeneratorHelper();//TODO
            // new Organization Uid
            //uidGen.getNbsIDLong(UidClassCodes.NBS_CLASS_CODE);//TODO
            organizationUid = organizationDT.getOrganizationUid();
            System.out.println("-----insertOrganization organizationUid:" + organizationUid);
            EntityODSE entityModel = new EntityODSE();
            entityModel.setEntityUid(organizationUid);
            entityModel.setClassCd(ORG);
            //// New code
            entityRepository.save(entityModel);
            organizationDT.setCd(entityModel.getClassCd());

            logger.info("OrganizationDAOImpl - after save Entity");
            if (organizationDT != null) {
                Organization organization = new Organization(organizationDT);
                organizationRepository.save(organization);
                organizationDT.setItNew(false);
                organizationDT.setItDirty(false);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return organizationUid;
    }

    private void insertOrganizationNames(OrganizationVO organizationVO)
            throws DataProcessingException {
        logger.debug("insertOrganizationNames(long organizationUID, Collection<Object>  organizationNames)");
        Iterator<OrganizationNameDT> anIterator = null;
        try {
            long organizationUID = organizationVO.getTheOrganizationDT().getOrganizationUid().longValue();
            Collection<OrganizationNameDT> organizationNames = organizationVO.getTheOrganizationNameDTCollection();
            /**
             * Inserts Organization names
             */
            anIterator = organizationNames.iterator();
            while (anIterator.hasNext()) {
                OrganizationNameDT orgNameDT = anIterator.next();

                if (orgNameDT.getOrganizationNameSeq() == null)
                    orgNameDT.setOrganizationNameSeq(Integer.valueOf(3));

                System.out.println("-----insertOrganizationNames OrganizationNameSeq:" + orgNameDT.getOrganizationNameSeq());
                if (orgNameDT != null) {
                    //insertOrganizationName(organizationUID, organizationName);
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
//            logger.error("organizationUID: " + organizationUID + " Exception while inserting " +
//                    "Organization names into ORGINIZATION_NAME_TABLE: \n", ex);
            logger.error(" Exception while inserting " +
                    "Organization names into ORGINIZATION_NAME_TABLE: \n", ex);
            throw new DataProcessingException(ex.toString());
        }
        logger.debug("OrganizationRepositoryUtil - Done inserting all Organization names");
    }//end of inserting Organization names

    private void createEntityId(OrganizationVO organizationVO) throws DataProcessingException {
        ArrayList<EntityIdDto> entityList = (ArrayList<EntityIdDto>) organizationVO.getTheEntityIdDtoCollection();
        try {
            Long pUid = organizationVO.getTheOrganizationDT().getOrganizationUid();
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).setEntityUid(pUid);
                System.out.println("----createEntityId OrganizationUid:" + pUid);
                entityIdRepository.save(new EntityId(entityList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityLocatorParticipation(OrganizationVO ovo) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto> entityLocatorList = (ArrayList<EntityLocatorParticipationDto>) ovo.getTheEntityLocatorParticipationDtoCollection();
        try {
            for (int i = 0; i < entityLocatorList.size(); i++) {
                EntityLocatorParticipationDto entityLocatorDT = entityLocatorList.get(i);
                LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION);
                System.out.println("createEntityLocatorParticipation localUid:" + localUid.getSeedValueNbr());
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
                entityLocatorDT.setEntityUid(ovo.getTheOrganizationDT().getOrganizationUid());
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

    private void createRole(OrganizationVO ovo) throws DataProcessingException {
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
     * @param organizationVO    the OrganizationVO
     * @param businessTriggerCd the String
     * @return organizationUID the Long
     * @roseuid 3E6E4E05003E
     * @J2EE_METHOD -- setOrganization
     */
    @Transactional
    public Long setOrganization(OrganizationVO organizationVO,
                                String businessTriggerCd)
            throws DataProcessingException {
        Long organizationUID;
        try {
            organizationUID = setOrganizationInternal(organizationVO, businessTriggerCd);
        } catch (DataProcessingException ex) {
            logger.error("OrganizationRepositoryUtil.setOrganization: DataProcessingException:" + ex.getMessage(), ex);
            throw new DataProcessingException(ex.getMessage(), ex);
        } catch (Exception e) {
            logger.error("OrganizationRepositoryUtil.setOrganization: Exception: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        return organizationUID;
    }

    private Long setOrganizationInternal(OrganizationVO organizationVO, String businessTriggerCd) throws DataProcessingException {
        Long organizationUID = Long.valueOf(-1);
        try {
            logger.debug("\n\n Inside set");
            if (!organizationVO.isItNew() && !organizationVO.isItDirty()) {
                System.out.println("setOrganizationInternal not New and not Dirty.. OrganizationUid:" + organizationVO.getTheOrganizationDT()
                        .getOrganizationUid());
                return organizationVO.getTheOrganizationDT()
                        .getOrganizationUid();
            } else {
                //TODO Check the following commented to code again whether it to be implemented.
//                PrepareVOUtils prepareVOUtils = new PrepareVOUtils();
//                OrganizationDT newOrganizationDT = (OrganizationDT) prepareVOUtils
//                        .prepareVO(organizationVO.getTheOrganizationDT(),
//                                businessObjLookupName, businessTriggerCd,
//                                DataTables.ORGANIZATION_TABLE,
//                                NEDSSConstants.BASE, nbsSecurityObj);
//                organizationVO.setTheOrganizationDT(newOrganizationDT);

                prepareVO(organizationVO.getTheOrganizationDT(), businessTriggerCd,
                        DataTables.ORGANIZATION_TABLE, NEDSSConstant.BASE);

                Collection<EntityLocatorParticipationDto> elpDTCol = organizationVO
                        .getTheEntityLocatorParticipationDtoCollection();
                Collection<RoleDto> rDTCol = organizationVO
                        .getTheRoleDTCollection();
                Collection<ParticipationDT> pDTCol = organizationVO
                        .getTheParticipationDTCollection();
                //Collection<EntityLocatorParticipationDto> col = null;
                if (elpDTCol != null) {
                    Collection<EntityLocatorParticipationDto> col = entityHelper.iterateELPDTForEntityLocatorParticipation(elpDTCol);
                    organizationVO
                            .setTheEntityLocatorParticipationDtoCollection(col);
                }
                if (rDTCol != null) {
                    Collection<RoleDto> col = entityHelper.iterateRDT(rDTCol);
                    organizationVO.setTheRoleDTCollection(col);
                }
                if (pDTCol != null) {
                    Collection<ParticipationDT> col = entityHelper.iteratePDTForParticipation(pDTCol);
                    organizationVO.setTheParticipationDTCollection(col);
                }
                /* Call the function to persist the OrganizationName */

                this.prepareOrganizationNameBeforePersistence(organizationVO);

//                Organization organization = null;
//                NedssUtils nedssUtils = new NedssUtils();
//                Object obj = nedssUtils.lookupBean(JNDINames.ORGANIZATIONEJB);
//                logger.debug("EntityControllerEJB.setOrganization - lookup = "
//                        + obj.toString());
//                OrganizationHome home = (OrganizationHome) PortableRemoteObject
//                        .narrow(obj, OrganizationHome.class);
//                logger.debug("EntityControllerEJB.setOrganization - Found OrganizationHome: "
//                        + home);

                if (organizationVO.isItNew()) {
                    System.out.println("11111111 for createOrganization");
//                    organization = home.create(organizationVO);
                    organizationUID = createOrganization(organizationVO);
                    logger.debug(" OrganizationRepositoryUtil.setOrganization -  Organization Created");
//                    logger.debug("EntityControllerEJB.setOrganization - organization.getOrganizationVO().getTheOrganizationDT().getOrganizationUid() =  "
//                            + organization.getOrganizationVO()
//                            .getTheOrganizationDT()
//                            .getOrganizationUid());
                    logger.debug("OrganizationRepositoryUtil.setOrganization {}", organizationUID);
//                    organizationUID = organization.getOrganizationVO()
//                            .getTheOrganizationDT().getOrganizationUid();
                } else {
//                    organization = home.findByPrimaryKey(organizationVO
//                            .getTheOrganizationDT().getOrganizationUid());
//                    organization.setOrganizationVO(organizationVO);
//                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Updated");
//                    organizationUID = organization.getOrganizationVO()
//                            .getTheOrganizationDT().getOrganizationUid();
                    System.out.println("11111111 for updateOrganization");
                    updateOrganization(organizationVO);
                    organizationUID = organizationVO
                            .getTheOrganizationDT().getOrganizationUid();
                    logger.debug(" OrganizationRepositoryUtil.setOrganizationInternal -  Organization Updated");
                }
            }
        } catch (Exception e) {
            if (e.toString().indexOf("NEDSSConcurrentDataException") != -1) {
                logger.error("EntityControllerEJB.setOrganizationInternal: NEDSSConcurrentDataException: " + e.getMessage(), e);
                throw new DataProcessingException(e.getMessage(), e);
            } else {
                logger.error("EntityControllerEJB.setOrganizationInternal: Exception: " + e.getMessage(), e);
                throw new DataProcessingException(e.getMessage(), e);
            }
        }

        logger.debug("EntityControllerEJB.setOrganization - ouid  =  "
                + organizationUID);
        return organizationUID;
    }

    private void prepareOrganizationNameBeforePersistence(
            OrganizationVO organizationVO) throws DataProcessingException {
        try {
            Collection<OrganizationNameDT> namesCollection = null;
            Iterator<OrganizationNameDT> anIterator = null;
            String selectedName = null;
            namesCollection = organizationVO.getTheOrganizationNameDTCollection();
            if (namesCollection != null) {
                try {
                    for (anIterator = namesCollection.iterator(); anIterator
                            .hasNext(); ) {
                        OrganizationNameDT organizationNameDT = anIterator
                                .next();
                        if (organizationNameDT.getNmUseCd().equals("L")) {
                            selectedName = organizationNameDT.getNmTxt();
                            organizationVO.getTheOrganizationDT().setDisplayNm(
                                    selectedName);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception setting the Organization Name: " + selectedName);
                }
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.prepareOrganizationNameBeforePersistence: " + e.getMessage(), e);
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
    public OrganizationVO loadObject(long organizationUID, long actUid) throws DataProcessingException {
        OrganizationVO ovo = new OrganizationVO();


        /**
         *  Selects OrganizationDT object
         */

        OrganizationDT pDT = selectOrganization(organizationUID);
        ovo.setTheOrganizationDT(pDT);

        /**
         * Selects OrganizationNameDT Collection
         */

        Collection<OrganizationNameDT> pnColl = selectOrganizationNames(organizationUID);
        ovo.setTheOrganizationNameDTCollection(pnColl);

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
        Collection<ParticipationDT> parColl = selectParticipationDTCollection(organizationUID, actUid);
        ovo.setTheParticipationDTCollection(parColl);


        ovo.setItNew(false);
        ovo.setItDirty(false);
        return ovo;
    }

    private OrganizationDT selectOrganization(long organizationUID) throws DataProcessingException {
        OrganizationDT organizationDT = null;
        /**
         * Selects organization from organization table
         */
        try {
            Organization organizatioModel = findOrganizationByUid(Long.valueOf(organizationUID));
            organizationDT = new OrganizationDT(organizatioModel);
            organizationDT.setItNew(false);
            organizationDT.setItDirty(false);
        } catch (Exception ex) {
            logger.error("Exception while selecting " +
                    "organization vo; id = " + organizationUID, ex);
            throw new DataProcessingException(ex.toString());
        }
        logger.debug("return organization object");
        return organizationDT;
    }//end of selecting organization ethnic groups

    /**
     * Selects the  Names of the Organization
     *
     * @param organizationUID long   the OrganizationUID
     * @return Collection
     * @throws DataProcessingException
     * @throws DataProcessingException
     */
    private Collection<OrganizationNameDT> selectOrganizationNames(long organizationUID) throws DataProcessingException {
        Collection<OrganizationNameDT> returnArrayList = new ArrayList<>();
        try {
            Optional<List<OrganizationName>> listOptional = organizationNameRepository.findByOrganizationUid(organizationUID);
            List<OrganizationName> organizationNameList = listOptional.get();
            for (Iterator<OrganizationName> anIterator = organizationNameList.iterator(); anIterator.hasNext(); ) {
                OrganizationName organizationNameModel = anIterator.next();
                OrganizationNameDT organizationNameDT = new OrganizationNameDT(organizationNameModel);
                organizationNameDT.setItNew(false);
                organizationNameDT.setItDirty(false);
                returnArrayList.add(organizationNameDT);
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
            Optional<List<EntityId>> idListOptional = this.entityIdRepository.findByEntityUid(organizationUID);
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
    private Collection<ParticipationDT> selectParticipationDTCollection(long uid, long act_uid)
            throws DataProcessingException {
        try {
            List<Participation> participationList = participationRepository.
                    findBySubjectEntityUidAndActUid(uid, act_uid).get();

            ArrayList<ParticipationDT> retList = new ArrayList<>();

            for (Iterator<Participation> anIterator = participationList.iterator();
                 anIterator.hasNext(); ) {
                ParticipationDT participationDT = new ParticipationDT(anIterator.next());
                participationDT.setItNew(false);
                participationDT.setItDirty(false);
                retList.add(participationDT);
            }
            return retList;
        } catch (Exception se) {
            logger.error("Exception selectParticipation = " + se.getMessage(), se);
            throw new DataProcessingException("Error: Exception while selecting \n" +
                    se.getMessage());
        }
    }

    private OrganizationDT prepareOrganizationDT() {

        return null;
    }

    /**
     * This method is used to prepare Dirty Acts,Dirty Entities,New Acts And New Entities depending
     * you want to edit,delete or create records
     *
     * @param organizationDT    -- The DT to be prepared
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface -- the prepared DT(System attribute Set)
     * @throws DataProcessingException
     */
    public OrganizationDT prepareVO(OrganizationDT organizationDT, String businessTriggerCd, String tableName, String moduleCd) throws DataProcessingException {
        try {
            if (organizationDT.isItNew() == false && organizationDT.isItDirty() == false && organizationDT.isItDelete() == false) {
                throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
            }
            logger.debug("(Boolean.FALSE).equals(new Boolean(theRootDTInterface.tableName)?:" + tableName + ":theRootDTInterface.moduleCd:" + moduleCd + ":businessTriggerCd:" + businessTriggerCd);
            //Boolean testNewForRootDTInterface = theRootDTInterface.isItNew();
//            if(theRootDTInterface.isItDirty() && (Boolean.FALSE).equals(new Boolean(theRootDTInterface.isItNew())))
//            {
//                logger.debug("!test1. theRootDTInterface isItNEW?:" + !theRootDTInterface.isItNew() +":theRootDTInterface.IsItDirty:" +!theRootDTInterface.isItDirty() );
//                boolean result = dataConcurrenceCheck(theRootDTInterface, tableName, nbsSecurityObj);
//                if(result)
//                {
//                    logger.debug("result in prepareVOUtil is :" + result);
//                    //no concurrent dataAccess has occured, hence can continue!
//                }
//                else
//                    throw new NEDSSConcurrentDataException("NEDSSConcurrentDataException occurred in PrepareVOUtils.Person");
//            }

            if (organizationDT.isItNew() || organizationDT.isItDirty()) {
                long userId = AuthUtil.authUser.getAuthUserUid();
                ;
                Timestamp time = new Timestamp(new Date().getTime());
                logger.debug("new entity");
                PrepareEntity prepareEntity = this.getPrepareEntityForOrganization(businessTriggerCd, moduleCd, organizationDT.getOrganizationUid(), tableName);
                organizationDT.setLocalId(prepareEntity.getLocalId());
                organizationDT.setAddUserId(userId);
                organizationDT.setAddTime(time);
                organizationDT.setRecordStatusCd(prepareEntity.getRecordStatusState());
                organizationDT.setStatusCd(prepareEntity.getObjectStatusState());
                organizationDT.setRecordStatusTime(time);
                organizationDT.setStatusTime(time);
                organizationDT.setLastChgTime(time);
                organizationDT.setLastChgUserId(userId);
                organizationDT.setLastChgReasonCd(null);
            }
            return organizationDT;
        } catch (Exception e) {
            logger.error("Exception in PrepareVOUtils.prepareVO: LocalID: " + organizationDT.getLocalId() + ", businessTriggerCd: " + businessTriggerCd + ", tableName: " + tableName + ", " + e.getMessage(), e);
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