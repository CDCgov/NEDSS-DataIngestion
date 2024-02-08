package gov.cdc.dataprocessing.utilities;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxELRLabMapDT;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.phdc.HL7HDType;
import gov.cdc.dataprocessing.model.phdc.HL7MSHType;

import java.util.ArrayList;
import java.util.Collection;

public class LabResultHandler {
    public static LabResultProxyVO getLabResultMessage(HL7MSHType hl7MSHType, EdxLabInformationDT edxLabInformationDT) {
        LabResultProxyVO labResultProxy  = new LabResultProxyVO();
        HL7HDType sendingFacility = hl7MSHType.getSendingFacility();

        EdxELRLabMapDT edxELRLabMapDT = processingHL7SendingFacility(sendingFacility, edxLabInformationDT);
        creatingOrganization( labResultProxy,  edxELRLabMapDT,  edxLabInformationDT);
        edxLabInformationDT.setMessageControlID(hl7MSHType.getMessageControlID());

        return labResultProxy;
    }

    /**
     * This method processing and parse data into Object
     * - Sending Facility Name and CLIA
     * - Role
     * - Entity Id
     * - Participation
     * */
    public static EdxELRLabMapDT processingHL7SendingFacility(HL7HDType sendingFacility, EdxLabInformationDT edxLabInformationDT) {
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
        
        edxLabInformationDT.setSendingFacilityName(sendingFacility.getHL7NamespaceID());
        edxLabInformationDT.setSendingFacilityClia(sendingFacility.getHL7UniversalID());
        
        edxELRLabMapDT.setEntityUid((long) edxLabInformationDT.getNextUid());
        
        edxLabInformationDT.setUniversalIdType(sendingFacility.getHL7UniversalIDType());

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
        edxELRLabMapDT.setParticipationActUid(edxLabInformationDT.getRootObserbationUid());
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
    public static LabResultProxyVO creatingOrganization(LabResultProxyVO labResultProxy, EdxELRLabMapDT edxELRLabMap, EdxLabInformationDT edxLabInformation) {
        OrganizationVO organizationVO = new OrganizationVO();


        // ROLE
        organizationVO.setRole(edxELRLabMap.getRoleCd());
        RoleDT role = new RoleDT();
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
        labResultProxy.getTheRoleDTCollection().add(role);

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
            Collection<EntityIdDT> entityIdDTCollection = new ArrayList<>();
            EntityIdDT entityIdDT = new EntityIdDT();
            entityIdDT.setEntityIdSeq(1);

            if(edxELRLabMap.getEntityIdRootExtensionTxt()!=null && edxELRLabMap.getEntityIdRootExtensionTxt().trim().length()>0){
                entityIdDT.setRootExtensionTxt(edxELRLabMap.getEntityIdRootExtensionTxt());
                edxLabInformation.setSendingFacilityClia(edxELRLabMap.getEntityIdRootExtensionTxt());
            }
            else {
                entityIdDT.setRootExtensionTxt(EdxELRConstant.ELR_DEFAULT_CLIA);
                edxLabInformation.setSendingFacilityClia(EdxELRConstant.ELR_DEFAULT_CLIA);
            }
            entityIdDT.setAssigningAuthorityCd(edxELRLabMap.getEntityIdAssigningAuthorityCd());

            if(entityIdDT.getAssigningAuthorityCd().equalsIgnoreCase(EdxELRConstant.ELR_CLIA_CD)) {
                entityIdDT.setAssigningAuthorityDescTxt(EdxELRConstant.ELR_CLIA_DESC);
            }
            entityIdDT.setTypeCd(edxELRLabMap.getEntityIdTypeCd());
            entityIdDT.setTypeDescTxt(edxELRLabMap.getEntityIdTypeDescTxt());
            entityIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDT.setAddTime(edxELRLabMap.getAddTime());
            entityIdDT.setAsOfDate(edxELRLabMap.getAsOfDate());
            entityIdDTCollection.add(entityIdDT);
            organizationVO.setTheEntityIdDTCollection(entityIdDTCollection);
        }


        labResultProxy.getTheOrganizationVOCollection().add(organizationVO);

        return labResultProxy;
    }


}
