package gov.cdc.dataprocessing.utilities.component.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.phdc.HL7ORCType;
import gov.cdc.dataprocessing.model.phdc.HL7XADType;
import gov.cdc.dataprocessing.model.phdc.HL7XONType;
import gov.cdc.dataprocessing.model.phdc.HL7XTNType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ORCHandler {
    private static final Logger logger = LoggerFactory.getLogger(ORCHandler.class);

    private final NBSObjectConverter nbsObjectConverter;

    public ORCHandler(NBSObjectConverter nbsObjectConverter) {
        this.nbsObjectConverter = nbsObjectConverter;
    }


    public void getORCProcessing(HL7ORCType hl7ORCType,
                                 LabResultProxyContainer labResultProxyContainer,
                                 EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            getOrderingProvider(hl7ORCType, labResultProxyContainer, edxLabInformationDto);
            getOrderingFacility(hl7ORCType, labResultProxyContainer, edxLabInformationDto);
            if (hl7ORCType.getOrderEffectiveDateTime() != null) {
                edxLabInformationDto.setOrderEffectiveDate(
                        NBSObjectConverter.processHL7TSType(
                                hl7ORCType.getOrderEffectiveDateTime(), EdxELRConstant.DATE_VALIDATION_ORC_ORDER_EFFECTIVE_TIME_MSG));
            }
        } catch (Exception e) {
            logger.error("Exception thrown at HL7ORCProcessorget.getORCProcessing:"+ e.getMessage() ,e);
            throw new DataProcessingException("Exception thrown at HL7ORCProcessorget.getORCProcessing:"+ e);
        }
    }

    private LabResultProxyContainer getOrderingProvider(HL7ORCType hl7ORCType,
                                                        LabResultProxyContainer labResultProxyContainer,
                                                        EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            HL7XADType address;
            List<HL7XADType> addressArray = hl7ORCType
                    .getOrderingProviderAddress();
            if(addressArray!=null && !addressArray.isEmpty()){
                edxLabInformationDto.setRole(EdxELRConstant.ELR_OP_CD);
                edxLabInformationDto.setOrderingProvider(true);
                PersonContainer personContainer = new PersonContainer();
                personContainer.getThePersonDto().setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);
                //Only need first index
                address = addressArray.get(0);
                if (address != null) {
                    nbsObjectConverter.personAddressType(address, EdxELRConstant.ELR_OP_CD, personContainer);
                }

                personContainer.setRole(EdxELRConstant.ELR_OP_CD);
                edxLabInformationDto.setOrderingProviderVO(personContainer);
                labResultProxyContainer.getThePersonContainerCollection().add(personContainer);
                edxLabInformationDto.setMissingOrderingProvider(false);
            }else{
                edxLabInformationDto.setMissingOrderingProvider(true);
            }
        } catch (Exception e) {
            logger.error("Exception thrown by HL7ORCProcessor.getOrderingProvider " + e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at HL7ORCProcessor.getOrderingProvider:"+ e);
        }

        return labResultProxyContainer;

    }

    private OrganizationVO getOrderingFacility(HL7ORCType hl7ORCType,
                                                      LabResultProxyContainer labResultProxyContainer,
                                                      EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        OrganizationVO organizationVO = new OrganizationVO();
        try {
            List<HL7XADType> addressArray = hl7ORCType.getOrderingFacilityAddress();
            if(addressArray!=null && addressArray.size() !=0){
                OrganizationDT organizationDT = new OrganizationDT();
                organizationVO.setItNew(true);
                organizationVO.setItDirty(false);
                organizationVO.setRole(EdxELRConstant.ELR_OP_CD);
                organizationDT.setOrganizationUid((long) edxLabInformationDto.getNextUid());
                organizationDT.setCd(EdxELRConstant.ELR_OTHER_CD);
                organizationDT.setCdDescTxt(EdxELRConstant.ELR_OTHER_DESC);
                organizationDT.setStandardIndustryClassCd(EdxELRConstant.ELR_RECEIVING_STANDARD_INDUSTRY_CLASS_CD);
                organizationDT.setStandardIndustryDescTxt(EdxELRConstant.ELR_RECEIVING_STANDARD_INDUSTRY_CLASS_DESC);
                organizationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
                organizationDT.setItNew(true);
                organizationDT.setItDirty(false);
                organizationVO.setTheOrganizationDT(organizationDT);
                organizationDT.setAddUserId(edxLabInformationDto.getUserId());

                ParticipationDT participationDT = new ParticipationDT();
                participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
                participationDT.setCd(EdxELRConstant.ELR_OP_CD);
                participationDT.setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);
                participationDT.setActUid(edxLabInformationDto.getRootObserbationUid());
                participationDT.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
                NBSObjectConverter.defaultParticipationDT(participationDT, edxLabInformationDto);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_ORDERER_DESC);
                participationDT.setSubjectClassCd(EdxELRConstant.ELR_ORG);
                participationDT.setSubjectEntityUid(organizationDT.getOrganizationUid());
                labResultProxyContainer.getTheParticipationDTCollection().add(participationDT);
                labResultProxyContainer.getTheOrganizationVOCollection().add(organizationVO);

                Collection<RoleDto> roleDtoColl = new ArrayList<>();
                RoleDto roleDto = new RoleDto();
                roleDto.setCd(EdxELRConstant.ELR_OP_CD);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_OP_DESC);
                roleDto.setScopingClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
                roleDto.setRoleSeq(1L);
                roleDto.setAddReasonCd("");
                roleDto.setAddTime(organizationVO.getTheOrganizationDT().getAddTime());
                roleDto.setAddUserId(edxLabInformationDto.getUserId());
                roleDto.setItNew(true);
                roleDto.setItDirty(false);
                roleDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
                roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                roleDto.setLastChgTime(organizationVO.getTheOrganizationDT().getAddTime());
                roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                roleDto.setSubjectEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
                roleDtoColl.add(roleDto);
                labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);

                Collection<EntityLocatorParticipationDto> addressCollection = new ArrayList<>();
                if (!addressArray.isEmpty()) {
                    HL7XADType addressType = addressArray.get(0);
                    EntityLocatorParticipationDto elpDT = nbsObjectConverter.organizationAddressType(addressType, EdxELRConstant.ELR_OP_CD, organizationVO);
                    addressCollection.add(elpDT);
                }


                List<HL7XTNType> phoneArray = hl7ORCType.getOrderingFacilityPhoneNumber();
                if (!phoneArray.isEmpty()) {
                    HL7XTNType phone = phoneArray.get(0);
                    if (phone != null) {
                        EntityLocatorParticipationDto elpdt = NBSObjectConverter.orgTelePhoneType(phone, EdxELRConstant.ELR_OP_CD, organizationVO);
                        elpdt.setUseCd(EdxELRConstant.ELR_WORKPLACE_CD);
                        organizationVO.getTheEntityLocatorParticipationDtoCollection().add(elpdt);
                    }
                }

                Collection<OrganizationNameDT> orgNameColl = new ArrayList<>();
                List<HL7XONType> nameArray = hl7ORCType.getOrderingFacilityName();
                if (nameArray != null && !nameArray.isEmpty()) {
                    HL7XONType orgName = nameArray.get(0);
                    OrganizationNameDT organizationNameDT = new OrganizationNameDT();
                    organizationNameDT.setNmTxt(orgName.getHL7OrganizationName());
                    organizationNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
                    organizationNameDT.setOrganizationNameSeq(0);
                    organizationDT.setDisplayNm(organizationNameDT.getNmTxt());
                    orgNameColl.add(organizationNameDT);
                }
                organizationVO.setTheOrganizationNameDTCollection(orgNameColl);
                edxLabInformationDto.setMissingOrderingFacility(false);
            }else{
                edxLabInformationDto.setMissingOrderingFacility(true);
            }

        } catch (Exception e) {
            logger.error("Exception thrown by HL7ORCProcessorget.getOrderingFacility " + e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at HL7ORCProcessorget.getOrderingFacility:"+ e);
        }
        edxLabInformationDto.setMultipleOrderingFacility(false);

        return organizationVO;
    }

}
