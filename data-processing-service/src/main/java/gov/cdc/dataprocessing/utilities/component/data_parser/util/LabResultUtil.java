package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxELRLabMapDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.phdc.HL7HDType;
import gov.cdc.dataprocessing.model.phdc.HL7MSHType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class LabResultUtil {


    /**
     * Description: Update Lab Result Container.
     * This method create these object in order. given value from edxELRLabMap.
     * These objects tightly associated with order
     * 1 - Role Dto
     * 2 - Participation Dto
     * 3 - Organization Dto
     * 4 - Organization Name Dto
     * 5 - Entity Id Dto
     * */
    public LabResultProxyContainer getLabResultMessage(HL7MSHType hl7MSHType, EdxLabInformationDto edxLabInformationDto) {
        LabResultProxyContainer labResultProxy  = new LabResultProxyContainer();
        HL7HDType sendingFacility = hl7MSHType.getSendingFacility();

        EdxELRLabMapDto edxELRLabMapDto = processingHL7SendingFacility(sendingFacility, edxLabInformationDto);
        creatingOrganization( labResultProxy, edxELRLabMapDto, edxLabInformationDto);
        edxLabInformationDto.setMessageControlID(hl7MSHType.getMessageControlID());

        return labResultProxy;
    }

    /**
     * Description: Update Lab Result Container.
     * This method create these object in order. given value from edxELRLabMap.
     * These objects tightly associated with order
     * 1 - Role Dto
     * 2 - Participation Dto
     * 3 - Organization Dto
     * 4 - Organization Name Dto
     * 5 - Entity Id Dto
     * */
    public EdxELRLabMapDto processingHL7SendingFacility(HL7HDType sendingFacility, EdxLabInformationDto edxLabInformationDto) {
        //ROLE, Sending Facility
        EdxELRLabMapDto edxELRLabMapDto = new EdxELRLabMapDto();
        edxELRLabMapDto.setRoleCd(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        edxELRLabMapDto.setRoleCdDescTxt(EdxELRConstant.ELR_SENDING_FACILITY_DESC);
        edxELRLabMapDto.setRoleSubjectClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
        edxELRLabMapDto.setEntityCd(EdxELRConstant.ELR_SENDING_LAB_CD);
        edxELRLabMapDto.setEntityCdDescTxt(EdxELRConstant.ELR_LABORATORY_DESC);
        edxELRLabMapDto.setEntityStandardIndustryClassCd(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
        edxELRLabMapDto.setEntityStandardIndustryDescTxt(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
        edxELRLabMapDto.setEntityDisplayNm(sendingFacility.getHL7NamespaceID());
        
        edxLabInformationDto.setSendingFacilityName(sendingFacility.getHL7NamespaceID());
        edxLabInformationDto.setSendingFacilityClia(sendingFacility.getHL7UniversalID());
        
        edxELRLabMapDto.setEntityUid((long) edxLabInformationDto.getNextUid());
        
        edxLabInformationDto.setUniversalIdType(sendingFacility.getHL7UniversalIDType());

        // ENTITY ID
        edxELRLabMapDto.setEntityIdAssigningAuthorityCd(sendingFacility.getHL7UniversalIDType());
        edxELRLabMapDto.setEntityIdAssigningAuthorityDescTxt(sendingFacility.getHL7NamespaceID());
        edxELRLabMapDto.setEntityIdRootExtensionTxt(sendingFacility.getHL7UniversalID());
        edxELRLabMapDto.setEntityIdTypeCd(EdxELRConstant.ELR_FACILITY_CD);
        edxELRLabMapDto.setEntityIdTypeDescTxt(EdxELRConstant.ELR_FACILITY_DESC);

        // PARTICIPATION
        edxELRLabMapDto.setParticipationActClassCd(EdxELRConstant.ELR_OBS);
        edxELRLabMapDto.setParticipationCd(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        edxELRLabMapDto.setParticipationSubjectClassCd(EdxELRConstant.ELR_ORG);
        edxELRLabMapDto.setParticipationTypeCd(EdxELRConstant.ELR_AUTHOR_CD);
        edxELRLabMapDto.setParticipationTypeDescTxt(EdxELRConstant.ELR_AUTHOR_DESC);
        edxELRLabMapDto.setParticipationActUid(edxLabInformationDto.getRootObserbationUid());
        edxELRLabMapDto.setParticipationEntityUid(edxELRLabMapDto.getEntityUid());
        return edxELRLabMapDto;
    }

    /**
     * Description: Update Lab Result Container.
     * This method create these object in order. given value from edxELRLabMap.
     * These objects tightly associated with order
     * 1 - Role Dto
     * 2 - Participation Dto
     * 3 - Organization Dto
     * 4 - Organization Name Dto
     * 5 - Entity Id Dto
     * */
    public LabResultProxyContainer creatingOrganization(LabResultProxyContainer labResultProxy, EdxELRLabMapDto edxELRLabMap, EdxLabInformationDto edxLabInformation) {
        OrganizationContainer organizationContainer = new OrganizationContainer();


        // ROLE
        organizationContainer.setRole(edxELRLabMap.getRoleCd());
        RoleDto role = new RoleDto();
        role.setSubjectEntityUid(edxELRLabMap.getEntityUid());
        role.setRoleSeq( 1L);
        role.setCd(edxELRLabMap.getRoleCd());
        role.setAddTime(edxELRLabMap.getAddTime());
        role.setLastChgTime(edxELRLabMap.getAddTime());
        role.setCdDescTxt(edxELRLabMap.getRoleCdDescTxt());
        role.setSubjectClassCd(edxELRLabMap.getRoleSubjectClassCd());
        //role.setSubjectEntityUid(edxELRLabMap.getEntityUid());
        role.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        role.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        role.setItNew(true);
        role.setItDirty(false);
        labResultProxy.getTheRoleDtoCollection().add(role);

        //PARTICIPANT
        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setActClassCd( edxELRLabMap.getParticipationActClassCd());
        participationDto.setCd(edxELRLabMap.getParticipationCd());
        participationDto.setSubjectClassCd(edxELRLabMap.getParticipationSubjectClassCd());
        participationDto.setTypeCd(edxELRLabMap.getParticipationTypeCd());
        participationDto.setTypeDescTxt(edxELRLabMap.getParticipationTypeDescTxt());
        participationDto.setActUid(edxELRLabMap.getParticipationActUid());
        participationDto.setSubjectEntityUid(edxELRLabMap.getParticipationEntityUid());
        participationDto.setAddTime(edxLabInformation.getAddTime());
        participationDto.setLastChgTime(edxLabInformation.getAddTime());
        participationDto.setAddUserId(edxLabInformation.getUserId());
        participationDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
        participationDto.setAddTime(edxLabInformation.getAddTime());
        participationDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        participationDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        participationDto.setItDirty(false);
        participationDto.setItNew(true);
        edxLabInformation.setAddReasonCd(participationDto.getAddReasonCd());
        participationDtoCollection.add(participationDto);
        labResultProxy.getTheParticipationDtoCollection().add(participationDto);

        //Organization
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setOrganizationUid(edxELRLabMap.getEntityUid());
        organizationDto.setCd(edxELRLabMap.getEntityCd());
        organizationDto.setAddTime(edxELRLabMap.getAddTime());
        organizationDto.setCdDescTxt(edxELRLabMap.getEntityCdDescTxt());
        organizationDto.setStandardIndustryClassCd(edxELRLabMap.getEntityIdAssigningAuthorityCd());
        organizationDto.setStandardIndustryDescTxt(edxELRLabMap.getEntityIdAssigningAuthorityDescTxt());
        organizationDto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
        organizationDto.setDisplayNm(edxELRLabMap.getEntityDisplayNm());
        organizationDto.setOrganizationUid(edxELRLabMap.getEntityUid());
        organizationContainer.setTheOrganizationDto(organizationDto);

        Collection<OrganizationNameDto> organizationNameDtoColl = new ArrayList<>();
        OrganizationNameDto organizationNameDto = new OrganizationNameDto();
        organizationNameDto.setOrganizationNameSeq(1);
        organizationNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
        organizationNameDto.setNmTxt(edxELRLabMap.getEntityDisplayNm());
        organizationNameDto.setOrganizationUid(organizationDto.getOrganizationUid());
        organizationNameDtoColl.add(organizationNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(organizationNameDtoColl);

        if (edxELRLabMap.getEntityIdTypeCd() != null && edxELRLabMap.getEntityIdTypeDescTxt() != null) {
            Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
            EntityIdDto entityIdDto = new EntityIdDto();
            entityIdDto.setEntityIdSeq(1);

            if(edxELRLabMap.getEntityIdRootExtensionTxt()!=null && edxELRLabMap.getEntityIdRootExtensionTxt().trim().length()>0){
                entityIdDto.setRootExtensionTxt(edxELRLabMap.getEntityIdRootExtensionTxt());
                edxLabInformation.setSendingFacilityClia(edxELRLabMap.getEntityIdRootExtensionTxt());
            }
            else {
                entityIdDto.setRootExtensionTxt(EdxELRConstant.ELR_DEFAULT_CLIA);
                edxLabInformation.setSendingFacilityClia(EdxELRConstant.ELR_DEFAULT_CLIA);
            }
            entityIdDto.setAssigningAuthorityCd(edxELRLabMap.getEntityIdAssigningAuthorityCd());

            if(entityIdDto.getAssigningAuthorityCd().equalsIgnoreCase(EdxELRConstant.ELR_CLIA_CD)) {
                entityIdDto.setAssigningAuthorityDescTxt(EdxELRConstant.ELR_CLIA_DESC);
            }
            entityIdDto.setTypeCd(edxELRLabMap.getEntityIdTypeCd());
            entityIdDto.setTypeDescTxt(edxELRLabMap.getEntityIdTypeDescTxt());
            entityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDto.setAddTime(edxELRLabMap.getAddTime());
            entityIdDto.setAsOfDate(edxELRLabMap.getAsOfDate());
            entityIdDtoCollection.add(entityIdDto);
            organizationContainer.setTheEntityIdDtoCollection(entityIdDtoCollection);
        }
        organizationContainer.setItNew(true);

        labResultProxy.getTheOrganizationContainerCollection().add(organizationContainer);
        return labResultProxy;
    }
}
