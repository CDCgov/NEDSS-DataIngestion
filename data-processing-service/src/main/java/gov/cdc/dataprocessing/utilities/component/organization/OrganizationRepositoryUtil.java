package gov.cdc.dataprocessing.utilities.component.organization;

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
import gov.cdc.dataprocessing.service.interfaces.core.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
                                      EntityHelper entityHelper) {
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
        this.odseIdGeneratorService=odseIdGeneratorService;
        this.entityHelper=entityHelper;
    }

    @Transactional
    public Organization findOrganizationByUid(Long personUid) {
        var result = organizationRepository.findById(personUid);
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
            localUid = localIdModel.getUidPrefixCd() + organizationUid + localIdModel.getUidSuffixCd();

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
        Long organizationUid = Long.valueOf(0);//new Long(0);
        try {
            /**
             * Inserts into entity table for organization
             */
            //uidGen = new UidGeneratorHelper();//TODO
            // new Organization Uid
            //uidGen.getNbsIDLong(UidClassCodes.NBS_CLASS_CODE);//TODO
            organizationUid = organizationDT.getOrganizationUid();

            EntityODSE entityModel = new EntityODSE();
            entityModel.setEntityUid(organizationUid);
            entityModel.setClassCd(ORG);
            //// New code
            entityRepository.save(entityModel);
            organizationDT.setCd(entityModel.getClassCd());

            logger.info("OrganizationDAOImpl - after save Entity");
            if (organizationDT != null) {
                Organization organization = convertOrganizationDtToOrganizationModel(organizationDT);
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
                    orgNameDT.setOrganizationNameSeq(Integer.valueOf(3));//TODO

                if (orgNameDT != null) {
                    //insertOrganizationName(organizationUID, organizationName);
                    OrganizationName orgName = convertOrgNameDtToOrgNameModel(organizationUID, orgNameDT);
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

    private Organization convertOrganizationDtToOrganizationModel(OrganizationDT organizationDT) {
        Organization orgModel = new Organization();
        if (organizationDT != null) {

            orgModel.setAddReasonCode(organizationDT.getAddReasonCd()); // 1
            orgModel.setAddTime(organizationDT.getAddTime());  // 2
            orgModel.setAddUserId(organizationDT.getAddUserId()); // 3
            orgModel.setCode(organizationDT.getCd()); // 4
            orgModel.setCodeDescTxt(organizationDT.getCdDescTxt()); // 5
            orgModel.setDescription(organizationDT.getDescription()); // 6
            orgModel.setDurationAmt(organizationDT.getDurationAmt()); // 7
            orgModel.setDurationUnitCd(organizationDT.getDurationUnitCd()); // 8
            orgModel.setFromTime(organizationDT.getFromTime()); //9
            orgModel.setLastChgReasonCd(organizationDT.getLastChgReasonCd()); // 10
            orgModel.setLastChgTime(organizationDT.getLastChgTime()); // 11
            orgModel.setLastChgUserId(organizationDT.getLastChgUserId()); // 12
            orgModel.setRecordStatusCd(organizationDT.getRecordStatusCd()); // 13
            orgModel.setRecordStatusTime(organizationDT.getRecordStatusTime()); // 14
            orgModel.setStandardIndustryClassCd(organizationDT.getStandardIndustryClassCd()); // 15
            orgModel.setStandardIndustryDescTxt(organizationDT.getStandardIndustryDescTxt()); // 16
            orgModel.setStatusCd(organizationDT.getStatusCd()); // 17
            orgModel.setStatusTime(organizationDT.getStatusTime()); // 18
            orgModel.setToTime(organizationDT.getToTime()); // 19
            orgModel.setUserAffiliationTxt(organizationDT.getUserAffiliationTxt()); // 20
            orgModel.setDisplayNm(organizationDT.getDisplayNm()); // 21
            orgModel.setStreetAddr1(organizationDT.getStreetAddr1()); // 22
            orgModel.setStreetAddr2(organizationDT.getStreetAddr2()); // 23
            orgModel.setCityCd(organizationDT.getCityCd()); // 24
            orgModel.setCityDescTxt(organizationDT.getCityDescTxt()); // 25
            orgModel.setStateCd(organizationDT.getStateCd()); // 26
            orgModel.setCntyCd(organizationDT.getCntyCd()); // 27
            orgModel.setCntryCd(organizationDT.getCntryCd()); // 28
            orgModel.setZipCd(organizationDT.getZipCd()); // 29
            orgModel.setPhoneNbr(organizationDT.getPhoneNbr()); // 30
            orgModel.setPhoneCntryCd(organizationDT.getPhoneCntryCd()); // 31
            orgModel.setOrganizationUid(organizationDT.getOrganizationUid()); // 32
            orgModel.setLocalId(organizationDT.getLocalId()); // 33
            orgModel.setEdxInd(organizationDT.getEdxInd());// 34 - Added new on Jan 18th 2011 Release 411_INV_AutoCreate
            orgModel.setVersionCtrlNbr(organizationDT.getVersionCtrlNbr()); // 35
            orgModel.setElectronicInd(organizationDT.getElectronicInd()); // 36
        }
        return orgModel;
    }

    private OrganizationName convertOrgNameDtToOrgNameModel(long organizationUID, OrganizationNameDT organizationNameDt) {
        OrganizationName organizationName = new OrganizationName();
        if (organizationNameDt != null) {
            organizationName.setOrganizationUid(organizationUID);
            organizationName.setOrganizationNameSeq(organizationNameDt.getOrganizationNameSeq());
            organizationName.setNameText(organizationNameDt.getNmTxt());
            organizationName.setNameUseCode(organizationNameDt.getNmUseCd());
            organizationName.setRecordStatusCode(organizationNameDt.getRecordStatusCd());
            organizationName.setDefaultNameIndicator(organizationNameDt.getDefaultNmInd());
        }
        return organizationName;
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
    public Long setOrganization(OrganizationVO organizationVO,
                                String businessTriggerCd)
            throws DataProcessingException{
        Long organizationUID;
        try {
            organizationUID = setOrganizationInternal(organizationVO, businessTriggerCd);
        } catch (DataProcessingException ex) {
            logger.error("EntityControllerEJB.setOrganization: NEDSSConcurrentDataException: concurrent access is not allowed" + ex.getMessage(),ex);
            throw new DataProcessingException(ex.getMessage(),ex);
        } catch (Exception e) {
                logger.error("EntityControllerEJB.setOrganization: Exception: " + e.getMessage(),e);
                throw new DataProcessingException(e.getMessage(),e);
        }

        return organizationUID;
    }
    private Long setOrganizationInternal(OrganizationVO organizationVO, String businessTriggerCd) throws DataProcessingException {
        Long organizationUID = Long.valueOf(-1);
        try {
            logger.debug("\n\n Inside set");
            if (!(organizationVO.isItNew()) && !(organizationVO.isItDirty())) {
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

                Organization organization = null;
//                NedssUtils nedssUtils = new NedssUtils();
//                Object obj = nedssUtils.lookupBean(JNDINames.ORGANIZATIONEJB);
//                logger.debug("EntityControllerEJB.setOrganization - lookup = "
//                        + obj.toString());
//                OrganizationHome home = (OrganizationHome) PortableRemoteObject
//                        .narrow(obj, OrganizationHome.class);
//                logger.debug("EntityControllerEJB.setOrganization - Found OrganizationHome: "
//                        + home);

                if (organizationVO.isItNew()) {
//                    organization = home.create(organizationVO);
                    organizationUID=createOrganization(organizationVO);
                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Created");
//                    logger.debug("EntityControllerEJB.setOrganization - organization.getOrganizationVO().getTheOrganizationDT().getOrganizationUid() =  "
//                            + organization.getOrganizationVO()
//                            .getTheOrganizationDT()
//                            .getOrganizationUid());
                    logger.debug("EntityControllerEJB.setOrganization {}",organizationUID);
//                    organizationUID = organization.getOrganizationVO()
//                            .getTheOrganizationDT().getOrganizationUid();
                } else {
//                    organization = home.findByPrimaryKey(organizationVO
//                            .getTheOrganizationDT().getOrganizationUid());
//                    organization.setOrganizationVO(organizationVO);
//                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Updated");
//                    organizationUID = organization.getOrganizationVO()
//                            .getTheOrganizationDT().getOrganizationUid();

                    updateOrganization(organizationVO);
                    organizationUID=organizationVO
                            .getTheOrganizationDT().getOrganizationUid();
                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Updated");
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
}