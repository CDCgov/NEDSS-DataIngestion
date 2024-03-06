package gov.cdc.dataprocessing.utilities.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EdxELRLabMapDT;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.phdc.HL7HDType;
import gov.cdc.dataprocessing.model.phdc.HL7MSHType;

import java.util.ArrayList;
import java.util.Collection;

public class LabResultUtil {
    public static LabResultProxyContainer getLabResultMessage(HL7MSHType hl7MSHType, EdxLabInformationDto edxLabInformationDto) {
        LabResultProxyContainer labResultProxy  = new LabResultProxyContainer();
        HL7HDType sendingFacility = hl7MSHType.getSendingFacility();

        EdxELRLabMapDT edxELRLabMapDT = processingHL7SendingFacility(sendingFacility, edxLabInformationDto);
        creatingOrganization( labResultProxy,  edxELRLabMapDT, edxLabInformationDto);
        edxLabInformationDto.setMessageControlID(hl7MSHType.getMessageControlID());

        return labResultProxy;
    }

    /**
     * This method processing and parse data into Object
     * - Sending Facility Name and CLIA
     * - Role
     * - Entity Id
     * - Participation
     * */
    public static EdxELRLabMapDT processingHL7SendingFacility(HL7HDType sendingFacility, EdxLabInformationDto edxLabInformationDto) {
        //ROLE, Sending Facility
        EdxELRLabMapDT edxELRLabMapDT = new EdxELRLabMapDT();
        edxELRLabMapDT.setRoleCd(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        edxELRLabMapDT.setRoleCdDescTxt(EdxELRConstant.ELR_SENDING_FACILITY_DESC);
        edxELRLabMapDT.setRoleSubjectClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
        edxELRLabMapDT.setEntityCd(EdxELRConstant.ELR_SENDING_LAB_CD);
        edxELRLabMapDT.setEntityCdDescTxt(EdxELRConstant.ELR_LABORATORY_DESC);
        edxELRLabMapDT.setEntityStandardIndustryClassCd(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
        edxELRLabMapDT.setEntityStandardIndustryDescTxt(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
        edxELRLabMapDT.setEntityDisplayNm(sendingFacility.getHL7NamespaceID());
        
        edxLabInformationDto.setSendingFacilityName(sendingFacility.getHL7NamespaceID());
        edxLabInformationDto.setSendingFacilityClia(sendingFacility.getHL7UniversalID());
        
        edxELRLabMapDT.setEntityUid((long) edxLabInformationDto.getNextUid());
        
        edxLabInformationDto.setUniversalIdType(sendingFacility.getHL7UniversalIDType());

        // ENTITY ID
        edxELRLabMapDT.setEntityIdAssigningAuthorityCd(sendingFacility.getHL7UniversalIDType());
        edxELRLabMapDT.setEntityIdAssigningAuthorityDescTxt(sendingFacility.getHL7NamespaceID());
        edxELRLabMapDT.setEntityIdRootExtensionTxt(sendingFacility.getHL7UniversalID());
        edxELRLabMapDT.setEntityIdTypeCd(EdxELRConstant.ELR_FACILITY_CD);
        edxELRLabMapDT.setEntityIdTypeDescTxt(EdxELRConstant.ELR_FACILITY_DESC);

        // PARTICIPATION
        edxELRLabMapDT.setParticipationActClassCd(EdxELRConstant.ELR_OBS);
        edxELRLabMapDT.setParticipationCd(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        edxELRLabMapDT.setParticipationSubjectClassCd(EdxELRConstant.ELR_ORG);
        edxELRLabMapDT.setParticipationTypeCd(EdxELRConstant.ELR_AUTHOR_CD);
        edxELRLabMapDT.setParticipationTypeDescTxt(EdxELRConstant.ELR_AUTHOR_DESC);
        edxELRLabMapDT.setParticipationActUid(edxLabInformationDto.getRootObserbationUid());
        edxELRLabMapDT.setParticipationEntityUid(edxELRLabMapDT.getEntityUid());
        return edxELRLabMapDT;
    }

    /**
     * This method processing and parse data into Object
     * - Organization
     * - Organization Name
     * - Role
     * - Participation
     * - Entity ID
     * */
    public static LabResultProxyContainer creatingOrganization(LabResultProxyContainer labResultProxy, EdxELRLabMapDT edxELRLabMap, EdxLabInformationDto edxLabInformation) {
        OrganizationVO organizationVO = new OrganizationVO();


        // ROLE
        organizationVO.setRole(edxELRLabMap.getRoleCd());
        RoleDto role = new RoleDto();
        role.setSubjectEntityUid(edxELRLabMap.getEntityUid());
        role.setRoleSeq( 1L);
        role.setCd(edxELRLabMap.getRoleCd());
        role.setAddTime(edxELRLabMap.getAddTime());
        role.setLastChgTime(edxELRLabMap.getAddTime());
        role.setCdDescTxt(edxELRLabMap.getRoleCdDescTxt());
        role.setSubjectClassCd(edxELRLabMap.getRoleSubjectClassCd());
        role.setSubjectEntityUid(edxELRLabMap.getEntityUid());
        role.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        role.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        role.setItNew(true);
        role.setItDirty(false);
        labResultProxy.getTheRoleDtoCollection().add(role);

        //PARTICIPANT
        Collection<ParticipationDT> participationDTCollection = new ArrayList<>();
        ParticipationDT participationDT = new ParticipationDT();
        participationDT.setActClassCd( edxELRLabMap.getParticipationActClassCd());
        participationDT.setCd(edxELRLabMap.getParticipationCd());
        participationDT.setSubjectClassCd(edxELRLabMap.getParticipationSubjectClassCd());
        participationDT.setTypeCd(edxELRLabMap.getParticipationTypeCd());
        participationDT.setTypeDescTxt(edxELRLabMap.getParticipationTypeDescTxt());
        participationDT.setActUid(edxELRLabMap.getParticipationActUid());
        participationDT.setSubjectEntityUid(edxELRLabMap.getParticipationEntityUid());
        participationDT.setAddTime(edxLabInformation.getAddTime());
        participationDT.setLastChgTime(edxLabInformation.getAddTime());
        participationDT.setAddUserId(edxLabInformation.getUserId());
        participationDT.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
        participationDT.setAddTime(edxLabInformation.getAddTime());
        participationDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        participationDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        participationDT.setItDirty(false);
        participationDT.setItNew(true);
        edxLabInformation.setAddReasonCd(participationDT.getAddReasonCd());
        participationDTCollection.add(participationDT);
        labResultProxy.getTheParticipationDTCollection().add(participationDT);


        //Organization
        OrganizationDT organizationDT = new OrganizationDT();
        organizationDT.setOrganizationUid(edxELRLabMap.getEntityUid());
        organizationDT.setCd(edxELRLabMap.getEntityCd());
        organizationDT.setAddTime(edxELRLabMap.getAddTime());
        organizationDT.setCdDescTxt(edxELRLabMap.getEntityCdDescTxt());
        organizationDT.setStandardIndustryClassCd(edxELRLabMap.getEntityIdAssigningAuthorityCd());
        organizationDT.setStandardIndustryDescTxt(edxELRLabMap.getEntityIdAssigningAuthorityDescTxt());
        organizationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
        organizationDT.setDisplayNm(edxELRLabMap.getEntityDisplayNm());
        organizationDT.setOrganizationUid(edxELRLabMap.getEntityUid());
        organizationVO.setTheOrganizationDT(organizationDT);

        Collection<OrganizationNameDT> organizationNameDTColl = new ArrayList<>();
        OrganizationNameDT organizationNameDT = new OrganizationNameDT();
        organizationNameDT.setOrganizationNameSeq(1);
        organizationNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
        organizationNameDT.setNmTxt(edxELRLabMap.getEntityDisplayNm());
        organizationNameDT.setOrganizationUid(organizationDT.getOrganizationUid());
        organizationNameDTColl.add(organizationNameDT);
        organizationVO.setTheOrganizationNameDTCollection(organizationNameDTColl);

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
            organizationVO.setTheEntityIdDtoCollection(entityIdDtoCollection);
        }


        labResultProxy.getTheOrganizationVOCollection().add(organizationVO);

        return labResultProxy;
    }


}
