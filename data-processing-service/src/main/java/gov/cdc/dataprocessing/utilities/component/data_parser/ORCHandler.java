package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.phdc.HL7ORCType;
import gov.cdc.dataprocessing.model.phdc.HL7XADType;
import gov.cdc.dataprocessing.model.phdc.HL7XONType;
import gov.cdc.dataprocessing.model.phdc.HL7XTNType;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component

public class ORCHandler {
    private static final Logger logger = LoggerFactory.getLogger(ORCHandler.class);
    @Value("${service.timezone}")
    private String tz = "UTC";
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
                        nbsObjectConverter.processHL7TSType(
                                hl7ORCType.getOrderEffectiveDateTime(), EdxELRConstant.DATE_VALIDATION_ORC_ORDER_EFFECTIVE_TIME_MSG));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * Get Ordering Provider
     * */
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
                personContainer.getThePersonDto().setAddUserId(AuthUtil.authUser.getNedssEntryId());
                //Only need first index
                address = addressArray.getFirst();
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
            throw new DataProcessingException(e.getMessage(), e);
        }

        return labResultProxyContainer;

    }

    private OrganizationContainer getOrderingFacility(HL7ORCType hl7ORCType,
                                                      LabResultProxyContainer labResultProxyContainer,
                                                      EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        try {
            List<HL7XADType> addressArray = hl7ORCType.getOrderingFacilityAddress();
            if(addressArray!=null && !addressArray.isEmpty()){
                OrganizationDto organizationDto = new OrganizationDto();
                organizationContainer.setItNew(true);
                organizationContainer.setItDirty(false);
                organizationContainer.setRole(EdxELRConstant.ELR_OP_CD);
                organizationDto.setOrganizationUid((long) edxLabInformationDto.getNextUid());
                organizationDto.setCd(EdxELRConstant.ELR_OTHER_CD);
                organizationDto.setCdDescTxt(EdxELRConstant.ELR_OTHER_DESC);
                organizationDto.setStandardIndustryClassCd(EdxELRConstant.ELR_RECEIVING_STANDARD_INDUSTRY_CLASS_CD);
                organizationDto.setStandardIndustryDescTxt(EdxELRConstant.ELR_RECEIVING_STANDARD_INDUSTRY_CLASS_DESC);
                organizationDto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
                organizationDto.setItNew(true);
                organizationDto.setItDirty(false);
                organizationContainer.setTheOrganizationDto(organizationDto);
                organizationDto.setAddUserId(edxLabInformationDto.getUserId());

                ParticipationDto participationDto = new ParticipationDto();
                participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
                participationDto.setCd(EdxELRConstant.ELR_OP_CD);
                participationDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
                participationDto.setActUid(edxLabInformationDto.getRootObserbationUid());
                participationDto.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
                nbsObjectConverter.defaultParticipationDT(participationDto, edxLabInformationDto);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_ORDERER_DESC);
                participationDto.setSubjectClassCd(EdxELRConstant.ELR_ORG);
                participationDto.setSubjectEntityUid(organizationDto.getOrganizationUid());
                labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);
                labResultProxyContainer.getTheOrganizationContainerCollection().add(organizationContainer);

                Collection<RoleDto> roleDtoColl = new ArrayList<>();
                RoleDto roleDto = new RoleDto();
                roleDto.setCd(EdxELRConstant.ELR_OP_CD);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_OP_DESC);
                roleDto.setScopingClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
                roleDto.setRoleSeq(1L);
                roleDto.setAddReasonCd("");
                roleDto.setAddTime(organizationContainer.getTheOrganizationDto().getAddTime());
                roleDto.setAddUserId(edxLabInformationDto.getUserId());
                roleDto.setItNew(true);
                roleDto.setItDirty(false);
                roleDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
                roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                roleDto.setLastChgTime(organizationContainer.getTheOrganizationDto().getAddTime());
                roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                roleDto.setSubjectEntityUid(organizationContainer.getTheOrganizationDto().getOrganizationUid());
                roleDtoColl.add(roleDto);
                labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);

                Collection<EntityLocatorParticipationDto> addressCollection = new ArrayList<>();
                if (!addressArray.isEmpty()) {
                    HL7XADType addressType = addressArray.getFirst();
                    EntityLocatorParticipationDto elpDT = nbsObjectConverter.organizationAddressType(addressType, EdxELRConstant.ELR_OP_CD, organizationContainer);
                    addressCollection.add(elpDT);
                }


                List<HL7XTNType> phoneArray = hl7ORCType.getOrderingFacilityPhoneNumber();
                if (!phoneArray.isEmpty()) {
                    HL7XTNType phone = phoneArray.getFirst();
                    if (phone != null) {
                        EntityLocatorParticipationDto elpdt = nbsObjectConverter.orgTelePhoneType(phone, EdxELRConstant.ELR_OP_CD, organizationContainer);
                        elpdt.setUseCd(EdxELRConstant.ELR_WORKPLACE_CD);
                        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(elpdt);
                    }
                }

                Collection<OrganizationNameDto> orgNameColl = new ArrayList<>();
                List<HL7XONType> nameArray = hl7ORCType.getOrderingFacilityName();
                if (nameArray != null && !nameArray.isEmpty()) {
                    HL7XONType orgName = nameArray.getFirst();
                    OrganizationNameDto organizationNameDto = new OrganizationNameDto(tz);
                    organizationNameDto.setNmTxt(orgName.getHL7OrganizationName());
                    organizationNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
                    organizationNameDto.setOrganizationNameSeq(0);
                    organizationDto.setDisplayNm(organizationNameDto.getNmTxt());
                    orgNameColl.add(organizationNameDto);
                }
                organizationContainer.setTheOrganizationNameDtoCollection(orgNameColl);
                edxLabInformationDto.setMissingOrderingFacility(false);
            }else{
                edxLabInformationDto.setMissingOrderingFacility(true);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        edxLabInformationDto.setMultipleOrderingFacility(false);

        return organizationContainer;
    }

}
