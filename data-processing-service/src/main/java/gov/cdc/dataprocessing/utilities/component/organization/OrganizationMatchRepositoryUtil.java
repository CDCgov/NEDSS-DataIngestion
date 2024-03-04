package gov.cdc.dataprocessing.utilities.component.organization;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationNameDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;

public class OrganizationMatchRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationMatchRepositoryUtil.class);
    private final EntityHelper entityHelper;

    public OrganizationMatchRepositoryUtil(EntityHelper entityHelper) {
        this.entityHelper = entityHelper;
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
//    public Long setOrganization(OrganizationVO organizationVO,
//                                String businessTriggerCd)
//            throws java.rmi.RemoteException, NEDSSConcurrentDataException,
//            javax.ejb.EJBException {
//        Long organizationUID;
//        try {
//            organizationUID = setOrganizationInternal(organizationVO,
//                    NBSBOLookup.ORGANIZATION, businessTriggerCd, nbsSecurityObj);
//        } catch (NEDSSConcurrentDataException ex) {
//            logger.error("EntityControllerEJB.setOrganization: NEDSSConcurrentDataException: concurrent access is not allowed" + ex.getMessage(),ex);
//            throw new NEDSSConcurrentDataException(ex.getMessage(),ex);
//        } catch (Exception e) {
//            if (e.toString().indexOf("NEDSSConcurrentDataException") != -1) {
//                logger.error("EntityControllerEJB.setOrganization: NEDSSConcurrentDataException: " + e.getMessage(),e);
//                throw new NEDSSConcurrentDataException(e.getMessage(),e);
//            } else {
//                logger.fatal("EntityControllerEJB.setOrganization: Exception: " + e.getMessage(),e);
//                throw new EJBException(e.getMessage(),e);
//            }
//        }
//
//        return organizationUID;
//    }
    private Long setOrganizationInternal(OrganizationVO organizationVO,
                                         String businessObjLookupName, String businessTriggerCd) throws DataProcessingException {
        Long organizationUID = Long.valueOf(-1);
        try {
            logger.debug("\n\n Inside set");
            if (!(organizationVO.isItNew()) && !(organizationVO.isItDirty())) {
                return organizationVO.getTheOrganizationDT()
                        .getOrganizationUid();
            } else {
                PrepareVOUtils prepareVOUtils = new PrepareVOUtils();
                OrganizationDT newOrganizationDT = (OrganizationDT) prepareVOUtils
                        .prepareVO(organizationVO.getTheOrganizationDT(),
                                businessObjLookupName, businessTriggerCd,
                                DataTables.ORGANIZATION_TABLE,
                                NEDSSConstants.BASE, nbsSecurityObj);
                organizationVO.setTheOrganizationDT(newOrganizationDT);

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
                    organization = home.create(organizationVO);
                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Created");
                    logger.debug("EntityControllerEJB.setOrganization - organization.getOrganizationVO().getTheOrganizationDT().getOrganizationUid() =  "
                            + organization.getOrganizationVO()
                            .getTheOrganizationDT()
                            .getOrganizationUid());
                    organizationUID = organization.getOrganizationVO()
                            .getTheOrganizationDT().getOrganizationUid();
                } else {
                    organization = home.findByPrimaryKey(organizationVO
                            .getTheOrganizationDT().getOrganizationUid());
                    organization.setOrganizationVO(organizationVO);
                    logger.debug(" EntityControllerEJB.setOrganization -  Organization Updated");
                    organizationUID = organization.getOrganizationVO()
                            .getTheOrganizationDT().getOrganizationUid();
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