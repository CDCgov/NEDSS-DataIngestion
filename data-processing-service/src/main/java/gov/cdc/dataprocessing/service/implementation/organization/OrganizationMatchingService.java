package gov.cdc.dataprocessing.service.implementation.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.MsgType;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationMatchingService;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_ERROR_MATCHING_ORG;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PHCR_IMPORT_SRT;

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
public class OrganizationMatchingService implements IOrganizationMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationMatchingService.class);
    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;

    public OrganizationMatchingService(EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
                                       OrganizationRepositoryUtil organizationRepositoryUtil) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.organizationRepositoryUtil=organizationRepositoryUtil;
    }

    @SuppressWarnings("java:S3776")

    @Transactional
    public EDXActivityDetailLogDto getMatchingOrganization(
            OrganizationContainer organizationContainer)
            throws DataProcessingException {
        {
            Long entityUid = organizationContainer.getTheOrganizationDto()
                    .getOrganizationUid();
            //	String orgRole = organizationContainer.getRole();
            Collection<EdxEntityMatchDto> coll = new ArrayList<>();
//            EdxEntityMatchDAO edxDao1 = new EdxEntityMatchDAO();
            EDXActivityDetailLogDto edxActivityDetailLogDto = new EDXActivityDetailLogDto();
            String DET_MSG_ENTITY_EXISTS_SUCCESS = "Organization entity found with entity uid : ";
            String DET_MSG_ENTITY_EXISTS_FAIL_NEW = "Organization not found. New Organization created with organization uid: ";
            /*
             * Creating new DT for localID for // local identifier
             */
            EdxEntityMatchDto localEdxEntityMatchDT = null;
            String localId;
            int localIdhshCd = 0;
            localId = getLocalId(organizationContainer);
            if (localId != null) {
                localId = localId.toUpperCase();
                localIdhshCd = localId.hashCode();
            }
            if (localId != null) {
                try {
                    // Try to get the matching with the match string and type (was hash code)
//                    EdxEntityMatchDto edxEntityMatchingDT = edxDao
//                            .getEdxEntityMatch(NEDSSConstant.ORGANIZATION_CLASS_CODE, localId);
                    EdxEntityMatchDto edxEntityMatchingDT =
                            edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, localId);
                    if (edxEntityMatchingDT != null
                            && edxEntityMatchingDT.getEntityUid() != null) {
                        edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                        edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS
                                + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDto
                                .setRecordType(String.valueOf(MsgType.Organization));
                        edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                        return edxActivityDetailLogDto;
                    }
                } catch (Exception ex) {
                    logger.error("Error in getEdxEntityMatchOnMatchString in the  matching Organization");
                    throw new DataProcessingException(
                            "Error in getEdxEntityMatchOnMatchString " + ex.getMessage(),
                            ex);
                }
            }  //localId != null
            if (localId != null) {
                localEdxEntityMatchDT = new EdxEntityMatchDto();
                localEdxEntityMatchDT.setTypeCd(NEDSSConstant.ORGANIZATION);
                localEdxEntityMatchDT.setMatchString(localId);
                localEdxEntityMatchDT.setMatchStringHashCode((long) localIdhshCd);
            }

            // Matching the Identifier (CLIA)
            String identifier;
            int identifierHshCd = 0;
            List identifierList;
            identifierList = getIdentifier(organizationContainer);
            if (!identifierList.isEmpty()) {
                for (Object o : identifierList) {
                    identifier = (String) o;
                    if (identifier != null) {
                        identifier = identifier.toUpperCase();
                        identifierHshCd = identifier.hashCode();
                    }
                    try {
                        // Try to get the matching with the type and match string
//                        EdxEntityMatchDto edxEntityMatchingDT = edxDao
//                                .getEdxEntityMatch(NEDSSConstant.ORGANIZATION_CLASS_CODE, identifier);
                        EdxEntityMatchDto edxEntityMatchingDT =
                                edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, identifier);
                        if (edxEntityMatchingDT != null
                                && edxEntityMatchingDT.getEntityUid() != null) {
                            if (localEdxEntityMatchDT != null) {
                                localEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT
                                        .getEntityUid());
//                                edxDao.setEdxEntityMatchDT(localEdxEntityMatchDT);
                                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(localEdxEntityMatchDT);
                            }
                            edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                            edxActivityDetailLogDto
                                    .setComment(DET_MSG_ENTITY_EXISTS_SUCCESS
                                            + edxEntityMatchingDT.getEntityUid());
                            edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
                            edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                            edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                            return edxActivityDetailLogDto;
                        }
                    } catch (Exception ex) {
                        logger.error("Error in saveEdxEntityMatch in the matching Organization");
                        throw new DataProcessingException(
                                "Error in saveEdxEntityMatch matching Organization"
                                        + ex.getMessage(), ex);
                    }
                    if (identifier != null) {
                        EdxEntityMatchDto edxEntityMatchDT = new EdxEntityMatchDto();
                        edxEntityMatchDT.setTypeCd(NEDSSConstant.ORGANIZATION);
                        edxEntityMatchDT.setMatchString(identifier);
                        edxEntityMatchDT.setMatchStringHashCode((long) identifierHshCd);
                        coll.add(edxEntityMatchDT);
                    }

                }
            }
            // Matching with name and address with street address1 alone
            String nameAddStrSt1;
            int nameAddStrSt1hshCd = 0;
            nameAddStrSt1 = nameAddressStreetOne(organizationContainer);
            if (nameAddStrSt1 != null) {
                nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
            }
            if (nameAddStrSt1 != null) {

                try {
                    // Try to get the matching with the type and match string
//                    EdxEntityMatchDto edxEntityMatchingDT = edxDao
//                            .getEdxEntityMatch(NEDSSConstant.ORGANIZATION_CLASS_CODE, nameAddStrSt1);
                    EdxEntityMatchDto edxEntityMatchingDT =
                            edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, nameAddStrSt1);
                    if (edxEntityMatchingDT != null
                            && edxEntityMatchingDT.getEntityUid() != null) {
                        if (localEdxEntityMatchDT != null) {
                            localEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT
                                    .getEntityUid());
//                            edxDao.setEdxEntityMatchDT(localEdxEntityMatchDT);
                            edxPatientMatchRepositoryUtil.saveEdxEntityMatch(localEdxEntityMatchDT);
                        }
                        edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                        edxActivityDetailLogDto
                                .setComment(DET_MSG_ENTITY_EXISTS_SUCCESS
                                        + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
                        edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                        return edxActivityDetailLogDto;
                    }
                } catch (Exception ex) {
                    logger.error(LOG_ERROR_MATCHING_ORG);
                    throw new DataProcessingException(
                            LOG_ERROR_MATCHING_ORG
                                    + ex.getMessage(), ex);
                }

            }
            // Continue for name Telephone with no extension
            String nameTelePhone;
            int nameTelePhonehshCd = 0;
            nameTelePhone = telePhoneTxt(organizationContainer);
            if (nameTelePhone != null) {
                nameTelePhone = nameTelePhone.toUpperCase();
                nameTelePhonehshCd = nameTelePhone.hashCode();
            }
            if (nameTelePhone != null) {
                try {
//                    EdxEntityMatchDto edxEntityMatchingDT = edxDao
//                            .getEdxEntityMatch(NEDSSConstant.ORGANIZATION_CLASS_CODE, nameTelePhone);
                    EdxEntityMatchDto edxEntityMatchingDT =
                            edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, nameTelePhone);
                    if (edxEntityMatchingDT != null
                            && edxEntityMatchingDT.getEntityUid() != null) {
                        if (localEdxEntityMatchDT != null) {
                            localEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT
                                    .getEntityUid());
//                            edxDao.setEdxEntityMatchDT(localEdxEntityMatchDT);
                            edxPatientMatchRepositoryUtil.saveEdxEntityMatch(localEdxEntityMatchDT);
                        }
                        edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                        edxActivityDetailLogDto
                                .setComment(DET_MSG_ENTITY_EXISTS_SUCCESS
                                        + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
                        edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                        return edxActivityDetailLogDto;
                    }
                } catch (Exception ex) {
                    logger.error(LOG_ERROR_MATCHING_ORG);
                    throw new DataProcessingException(
                            LOG_ERROR_MATCHING_ORG
                                    + ex.getMessage(), ex);
                }

            }
            // Create the provider in case if the provider is not there in the DB
            try {
                //Legacy code
                //EntityController.java
                //public Long setOrganization    (OrganizationContainer organizationContainer, String businessTriggerCd, NBSSecurityObj nbsSecurityObj)
//                EntityController entityController = getEntityController();
//                String businessTriggerCd = NEDSSConstant.ORG_CR;
//                entityUid = entityController.setOrganization(organizationContainer,
//                        businessTriggerCd, nbsSecurityObj);
                String businessTriggerCd = NEDSSConstant.ORG_CR;
                entityUid=organizationRepositoryUtil.setOrganization(organizationContainer,
                        businessTriggerCd);
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or setting the Organization");
                throw new DataProcessingException(e.getMessage(), e);
            }
            // Create the name and address with no street 2(only street1)
            if (nameAddStrSt1 != null) {
                EdxEntityMatchDto edxEntityMatchDT = new EdxEntityMatchDto();
                edxEntityMatchDT.setEntityUid(entityUid);
                edxEntityMatchDT.setTypeCd(NEDSSConstant.ORGANIZATION);
                edxEntityMatchDT.setMatchString(nameAddStrSt1);
                edxEntityMatchDT.setMatchStringHashCode((long) nameAddStrSt1hshCd);
                try {
//                    edxDao.setEdxEntityMatchDT(edxEntityMatchDT);
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                } catch (Exception e) {
                    logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:"
                            + nameAddStrSt1 + " " + e.getMessage());
                    throw new DataProcessingException(e.getMessage(), e);
                }

            }

            // Create the name and address with nameTelePhone
            if (nameTelePhone != null) {
                EdxEntityMatchDto edxEntityMatchDT = new EdxEntityMatchDto();
                edxEntityMatchDT.setEntityUid(entityUid);
                edxEntityMatchDT.setTypeCd(NEDSSConstant.ORGANIZATION);
                edxEntityMatchDT.setMatchString(nameTelePhone);
                edxEntityMatchDT.setMatchStringHashCode((long) nameTelePhonehshCd);
                try {
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
            if (localEdxEntityMatchDT != null)
            {
                coll.add(localEdxEntityMatchDT);
            }
            if (coll != null) {
                for (EdxEntityMatchDto edxEntityMatchDT : coll) {
                    edxEntityMatchDT.setEntityUid(entityUid);
//                    edxDao.setEdxEntityMatchDT(edxEntityMatchDT);
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                }
            }
            edxActivityDetailLogDto.setRecordId(String.valueOf(entityUid));
            edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_FAIL_NEW
                    + edxActivityDetailLogDto.getRecordId());
            edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
            edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
            edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
            return edxActivityDetailLogDto;
        }
    }

    private String getLocalId(OrganizationContainer organizationContainer) {
        String localId = null;
        if (organizationContainer.getLocalIdentifier() != null) {
            localId = organizationContainer.getLocalIdentifier();
        }
        return localId;
    }

    @SuppressWarnings("java:S3776")

    // getting identifiers for PHCR Organizations
    private List<String> getIdentifier(OrganizationContainer organizationContainer) throws DataProcessingException {
        String carrot = "^";
        List<String> identifierList = new ArrayList<>();
        String identifier = null;
        Collection<EntityIdDto> newEntityIdDTColl = new ArrayList<>();
        try {
            if (organizationContainer.getTheEntityIdDtoCollection() != null
                    && organizationContainer.getTheEntityIdDtoCollection().size() > 0) {
                Collection<EntityIdDto> entityIdDTColl = organizationContainer
                        .getTheEntityIdDtoCollection();
                for (EntityIdDto entityIdDT : entityIdDTColl) {
                    if ((entityIdDT.getStatusCd()
                            .equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))) {

                        if ((entityIdDT.getRootExtensionTxt() != null)
                                && (entityIdDT.getTypeCd() != null)
                                && (entityIdDT.getAssigningAuthorityCd() != null)
                                && (entityIdDT.getAssigningAuthorityDescTxt() != null)
                                && (entityIdDT.getAssigningAuthorityIdType() != null)) {
                            identifier = entityIdDT.getRootExtensionTxt()
                                    + carrot + entityIdDT.getTypeCd() + carrot
                                    + entityIdDT.getAssigningAuthorityCd()
                                    + carrot
                                    + entityIdDT.getAssigningAuthorityDescTxt()
                                    + carrot + entityIdDT.getAssigningAuthorityIdType();
                        } else {
                            try {
                                Coded coded = new Coded();
                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH_ORG);
                                coded.setCodesetTableName("CODE_VALUE_GENERAL");//DataTables.CODE_VALUE_GENERAL

                                //TODO: This call out to code value general Repos and Caching the recrod
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

                                if (entityIdDT.getRootExtensionTxt() != null
                                        && entityIdDT.getTypeCd() != null
                                        && coded.getCode() != null
                                        && coded.getCodeDescription() != null
                                        && coded.getCodeSystemCd() != null) {
                                    identifier = entityIdDT.getRootExtensionTxt()
                                            + carrot + entityIdDT.getTypeCd() + carrot
                                            + coded.getCode() + carrot
                                            + coded.getCodeDescription() + carrot
                                            + coded.getCodeSystemCd();
                                }
                            } catch (Exception ex) {
                                String errorMessage = "The assigning authority "
                                        + entityIdDT.getAssigningAuthorityCd()
                                        + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }
                        if (entityIdDT.getTypeCd() != null && !entityIdDT.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDTColl.add(entityIdDT);
                        }
                        if (identifier != null) {
                            identifierList.add(identifier);
                        }

                    }
                }
            }
            organizationContainer.setTheEntityIdDtoCollection(newEntityIdDTColl);
        } catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for organization entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return identifierList;
    }
    @SuppressWarnings("java:S3776")
    public String nameAddressStreetOne(OrganizationContainer organizationContainer) {
        String nameAddStr = null;
        String carrot = "^";
        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null
                && organizationContainer
                .getTheEntityLocatorParticipationDtoCollection().size() > 0) {
            for (EntityLocatorParticipationDto entLocPartDT : organizationContainer
                    .getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null
                        && entLocPartDT.getClassCd().equals(
                        NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null
                            && entLocPartDT.getCd().equals(
                            NEDSSConstant.OFFICE_CD)
                            && entLocPartDT.getUseCd() != null
                            && entLocPartDT.getUseCd().equals(
                            NEDSSConstant.WORK_PLACE)) {
                        if (entLocPartDT.getThePostalLocatorDto() != null) {
                            PostalLocatorDto postLocDT = entLocPartDT
                                    .getThePostalLocatorDto();
                            if ((postLocDT.getStreetAddr1() != null && !postLocDT
                                    .getStreetAddr1().equals(""))
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT
                                    .getCityDescTxt().equals(""))
                                    && (postLocDT.getStateCd() != null && !postLocDT
                                    .getStateCd().equals(""))
                                    && (postLocDT.getZipCd() != null && !postLocDT
                                    .getZipCd().equals(""))) {
                                nameAddStr = carrot
                                        + postLocDT.getStreetAddr1() + carrot
                                        + postLocDT.getCityDescTxt() + carrot
                                        + postLocDT.getStateCd() + carrot
                                        + postLocDT.getZipCd();
                            }
                        }
                    }
                }
            }
        }
        if (nameAddStr != null)
            nameAddStr = getNameString(organizationContainer) + nameAddStr;
        return nameAddStr;
    }

    private String getNameString(OrganizationContainer organizationContainer) {
        String nameStr = null;
        if (organizationContainer.getTheOrganizationNameDtoCollection() != null
                && organizationContainer.getTheOrganizationNameDtoCollection().size() > 0) {
            Collection<OrganizationNameDto> organizationNameDtoColl = organizationContainer
                    .getTheOrganizationNameDtoCollection();
            for (OrganizationNameDto organizationNameDto : organizationNameDtoColl) {
                if (organizationNameDto.getNmUseCd() != null
                        && organizationNameDto.getNmUseCd().equals(
                        NEDSSConstant.LEGAL)) {
                    if (organizationNameDto.getNmTxt() != null)
                    {
                        nameStr = organizationNameDto.getNmTxt();
                    }
                }
            }
        }
        return nameStr;
    }
    @SuppressWarnings("java:S3776")
    private String telePhoneTxt(OrganizationContainer organizationContainer) {
        String nameTeleStr = null;
        String carrot = "^";

        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null
                && organizationContainer
                .getTheEntityLocatorParticipationDtoCollection().size() > 0) {
            for (EntityLocatorParticipationDto entLocPartDT : organizationContainer
                    .getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null
                        && entLocPartDT.getClassCd()
                        .equals(NEDSSConstant.TELE)) {
                    if (entLocPartDT.getCd() != null
                            && entLocPartDT.getCd()
                            .equals(NEDSSConstant.PHONE)) {
                        if (entLocPartDT.getTheTeleLocatorDto() != null) {
                            TeleLocatorDto teleLocDT = entLocPartDT
                                    .getTheTeleLocatorDto();
                            if (teleLocDT.getPhoneNbrTxt() != null
                                    && !teleLocDT.getPhoneNbrTxt().equals(""))
                                nameTeleStr = carrot
                                        + teleLocDT.getPhoneNbrTxt();

                        }
                    }
                }
            }
        }
        if (nameTeleStr != null) {
            nameTeleStr = getNameString(organizationContainer) + nameTeleStr;
        }
        return nameTeleStr;
    }
}
