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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PHCR_IMPORT_SRT;

@Service

public class OrganizationMatchingService implements IOrganizationMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationMatchingService.class);
    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;

    public OrganizationMatchingService(EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
                                       OrganizationRepositoryUtil organizationRepositoryUtil) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.organizationRepositoryUtil=organizationRepositoryUtil;
    }

    @SuppressWarnings({"java:S3776", "java:S6541"})
    public EDXActivityDetailLogDto getMatchingOrganization(
            OrganizationContainer organizationContainer)
            throws DataProcessingException
    {

            Long entityUid;
            Collection<EdxEntityMatchDto> coll = new ArrayList<>();
            EDXActivityDetailLogDto edxActivityDetailLogDto = new EDXActivityDetailLogDto();
            String entityExistSuccess = "Organization entity found with entity uid : ";
            String entityExistFail = "Organization not found. New Organization created with organization uid: ";

            EdxEntityMatchDto localEdxEntityMatchDT = null;
            String localId;
            int localIdhshCd = 0;
            localId = getLocalId(organizationContainer);
            if (localId != null) {
                localId = localId.toUpperCase();
                localIdhshCd = localId.hashCode();
            }
            if (localId != null) {
                EdxEntityMatchDto edxEntityMatchingDT =
                        edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, localId);
                if (edxEntityMatchingDT != null
                        && edxEntityMatchingDT.getEntityUid() != null) {
                    edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                    edxActivityDetailLogDto.setComment(entityExistSuccess
                            + edxEntityMatchingDT.getEntityUid());
                    edxActivityDetailLogDto
                            .setRecordType(String.valueOf(MsgType.Organization));
                    edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                    edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                    return edxActivityDetailLogDto;
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
            List<String> identifierList;
            identifierList = getIdentifier(organizationContainer);
            if (!identifierList.isEmpty()) {
                for (String o : identifierList) {
                    identifier = o;
                    if (identifier != null) {
                        identifier = identifier.toUpperCase();
                        identifierHshCd = identifier.hashCode();
                    }
                    // Try to get the matching with the type and match string
                    EdxEntityMatchDto edxEntityMatchingDT =
                            edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, identifier);
                    if (edxEntityMatchingDT != null
                            && edxEntityMatchingDT.getEntityUid() != null) {
                        if (localEdxEntityMatchDT != null) {
                            localEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT
                                    .getEntityUid());
                            edxPatientMatchRepositoryUtil.saveEdxEntityMatch(localEdxEntityMatchDT);
                        }
                        edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                        edxActivityDetailLogDto
                                .setComment(entityExistSuccess
                                        + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
                        edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                        return edxActivityDetailLogDto;
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
                // Try to get the matching with the type and match string
                EdxEntityMatchDto edxEntityMatchingDT =
                        edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, nameAddStrSt1);
                if (edxEntityMatchingDT != null
                        && edxEntityMatchingDT.getEntityUid() != null) {
                    if (localEdxEntityMatchDT != null) {
                        localEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT
                                .getEntityUid());
                        edxPatientMatchRepositoryUtil.saveEdxEntityMatch(localEdxEntityMatchDT);
                    }
                    edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                    edxActivityDetailLogDto
                            .setComment(entityExistSuccess
                                    + edxEntityMatchingDT.getEntityUid());
                    edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
                    edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                    edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                    return edxActivityDetailLogDto;
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
                EdxEntityMatchDto edxEntityMatchingDT =
                        edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.ORGANIZATION_CLASS_CODE, nameTelePhone);
                if (edxEntityMatchingDT != null
                        && edxEntityMatchingDT.getEntityUid() != null) {
                    if (localEdxEntityMatchDT != null) {
                        localEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT
                                .getEntityUid());
                        edxPatientMatchRepositoryUtil.saveEdxEntityMatch(localEdxEntityMatchDT);
                    }
                    edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                    edxActivityDetailLogDto
                            .setComment(entityExistSuccess
                                    + edxEntityMatchingDT.getEntityUid());
                    edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
                    edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
                    edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                    return edxActivityDetailLogDto;
                }

            }

            String businessTriggerCd = NEDSSConstant.ORG_CR;
            entityUid=organizationRepositoryUtil.setOrganization(organizationContainer,
                    businessTriggerCd);
            // Create the name and address with no street 2(only street1)
            if (nameAddStrSt1 != null) {
                EdxEntityMatchDto edxEntityMatchDT = new EdxEntityMatchDto();
                edxEntityMatchDT.setEntityUid(entityUid);
                edxEntityMatchDT.setTypeCd(NEDSSConstant.ORGANIZATION);
                edxEntityMatchDT.setMatchString(nameAddStrSt1);
                edxEntityMatchDT.setMatchStringHashCode((long) nameAddStrSt1hshCd);
                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
            }

            // Create the name and address with nameTelePhone
            if (nameTelePhone != null) {
                EdxEntityMatchDto edxEntityMatchDT = new EdxEntityMatchDto();
                edxEntityMatchDT.setEntityUid(entityUid);
                edxEntityMatchDT.setTypeCd(NEDSSConstant.ORGANIZATION);
                edxEntityMatchDT.setMatchString(nameTelePhone);
                edxEntityMatchDT.setMatchStringHashCode((long) nameTelePhonehshCd);
                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
            }
            if (localEdxEntityMatchDT != null)
            {
                coll.add(localEdxEntityMatchDT);
            }
            if (coll != null) {
                for (EdxEntityMatchDto edxEntityMatchDT : coll) {
                    edxEntityMatchDT.setEntityUid(entityUid);
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                }
            }
            edxActivityDetailLogDto.setRecordId(String.valueOf(entityUid));
            edxActivityDetailLogDto.setComment(entityExistFail
                    + edxActivityDetailLogDto.getRecordId());
            edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Organization));
            edxActivityDetailLogDto.setRecordName(PHCR_IMPORT_SRT);
            edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
            return edxActivityDetailLogDto;
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
    private List<String> getIdentifier(OrganizationContainer organizationContainer)  {
        String carrot = "^";
        List<String> identifierList = new ArrayList<>();
        String identifier = null;
        Collection<EntityIdDto> newEntityIdDTColl = new ArrayList<>();
        if (organizationContainer.getTheEntityIdDtoCollection() != null
                && !organizationContainer.getTheEntityIdDtoCollection().isEmpty()) {
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
                            logger.error("{} {}", ex.getMessage(), errorMessage);
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
        return identifierList;
    }
    @SuppressWarnings({"java:S3776", "java:S1066"})
    public String nameAddressStreetOne(OrganizationContainer organizationContainer) {
        String nameAddStr = null;
        String carrot = "^";
        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null
                && !organizationContainer
                .getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
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
                                    .getStreetAddr1().isEmpty())
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT
                                    .getCityDescTxt().isEmpty())
                                    && (postLocDT.getStateCd() != null && !postLocDT
                                    .getStateCd().isEmpty())
                                    && (postLocDT.getZipCd() != null && !postLocDT
                                    .getZipCd().isEmpty())) {
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

    @SuppressWarnings("java:S1066")
    private String getNameString(OrganizationContainer organizationContainer) {
        String nameStr = null;
        if (organizationContainer.getTheOrganizationNameDtoCollection() != null
                && !organizationContainer.getTheOrganizationNameDtoCollection().isEmpty()) {
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
    @SuppressWarnings({"java:S3776", "java:S1066"})
    private String telePhoneTxt(OrganizationContainer organizationContainer) {
        String nameTeleStr = null;
        String carrot = "^";

        if (organizationContainer.getTheEntityLocatorParticipationDtoCollection() != null
                && !organizationContainer
                .getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
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
                                    && !teleLocDT.getPhoneNbrTxt().isEmpty())
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
