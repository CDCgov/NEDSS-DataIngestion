package gov.cdc.dataprocessing.utilities;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.model.phdc.HL7ORCType;
import gov.cdc.dataprocessing.model.phdc.HL7XADType;
import gov.cdc.dataprocessing.model.phdc.HL7XONType;
import gov.cdc.dataprocessing.model.phdc.HL7XTNType;
import gov.cdc.dataprocessing.utilities.component.NBSObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ORCHandler {
    private static final Logger logger = LoggerFactory.getLogger(ORCHandler.class);

    public static void getORCProcessing(HL7ORCType hl7ORCType,
                                 LabResultProxyVO labResultProxyVO,
                                 EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {
            getOrderingProvider(hl7ORCType, labResultProxyVO, edxLabInformationDT);
            getOrderingFacility(hl7ORCType, labResultProxyVO, edxLabInformationDT);
            if (hl7ORCType.getOrderEffectiveDateTime() != null) {
                edxLabInformationDT.setOrderEffectiveDate(
                        NBSObjectConverter.processHL7TSType(
                                hl7ORCType.getOrderEffectiveDateTime(), EdxELRConstant.DATE_VALIDATION_ORC_ORDER_EFFECTIVE_TIME_MSG));
            }
        } catch (Exception e) {
            logger.error("Exception thrown at HL7ORCProcessorget.getORCProcessing:"+ e.getMessage() ,e);
            throw new DataProcessingException("Exception thrown at HL7ORCProcessorget.getORCProcessing:"+ e);
        }
    }

    private static LabResultProxyVO getOrderingProvider(HL7ORCType hl7ORCType,
                                                 LabResultProxyVO labResultProxyVO,
                                                 EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {
            HL7XADType address = null;
            List<HL7XADType> addressArray = hl7ORCType
                    .getOrderingProviderAddress();
            if(addressArray!=null && addressArray.size()!=0){
                edxLabInformationDT.setRole(EdxELRConstant.ELR_OP_CD);
                edxLabInformationDT.setOrderingProvider(true);
                PersonVO personVO = new PersonVO();
                personVO.getThePersonDT().setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);
                for (int i = 0; i < addressArray.size(); ) {
                    address = addressArray.get(i);
                    if (address != null) {
                        NBSObjectConverter.personAddressType(address,
                                EdxELRConstant.ELR_OP_CD, personVO);
                        break;
                    }
                }
                personVO.setRole(EdxELRConstant.ELR_OP_CD);
                edxLabInformationDT.setOrderingProviderVO(personVO);
                labResultProxyVO.getThePersonVOCollection().add(personVO);
                edxLabInformationDT.setMissingOrderingProvider(false);
            }else{
                edxLabInformationDT.setMissingOrderingProvider(true);
            }
        } catch (Exception e) {
            logger.error("Exception thrown by HL7ORCProcessor.getOrderingProvider " + e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at HL7ORCProcessor.getOrderingProvider:"+ e);
        }

        return labResultProxyVO;

    }

    private static OrganizationVO getOrderingFacility(HL7ORCType hl7ORCType,
                                                      LabResultProxyVO labResultProxyVO,
                                                      EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        OrganizationVO organizationVO = new OrganizationVO();
        try {
            List<HL7XADType> addressArray = hl7ORCType.getOrderingFacilityAddress();
            if(addressArray!=null && addressArray.size() !=0){
                OrganizationDT organizationDT = new OrganizationDT();
                organizationVO.setItNew(true);
                organizationVO.setItDirty(false);
                organizationVO.setRole(EdxELRConstant.ELR_OP_CD);
                organizationDT.setOrganizationUid((long)edxLabInformationDT.getNextUid());
                organizationDT.setCd(EdxELRConstant.ELR_OTHER_CD);
                organizationDT.setCdDescTxt(EdxELRConstant.ELR_OTHER_DESC);
                organizationDT.setStandardIndustryClassCd(EdxELRConstant.ELR_RECEIVING_STANDARD_INDUSTRY_CLASS_CD);
                organizationDT.setStandardIndustryDescTxt(EdxELRConstant.ELR_RECEIVING_STANDARD_INDUSTRY_CLASS_DESC);
                organizationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
                organizationDT.setItNew(true);
                organizationDT.setItDirty(false);
                organizationVO.setTheOrganizationDT(organizationDT);
                organizationDT.setAddUserId(edxLabInformationDT.getUserId());

                ParticipationDT participationDT = new ParticipationDT();
                participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
                participationDT.setCd(EdxELRConstant.ELR_OP_CD);
                participationDT.setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);
                participationDT.setActUid(edxLabInformationDT.getRootObserbationUid());
                participationDT.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
                participationDT = NBSObjectConverter.defaultParticipationDT(participationDT,edxLabInformationDT);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_ORDERER_DESC);
                participationDT.setSubjectClassCd(EdxELRConstant.ELR_ORG);
                participationDT.setSubjectEntityUid(organizationDT.getOrganizationUid());
                labResultProxyVO.getTheParticipationDTCollection().add(participationDT);
                labResultProxyVO.getTheOrganizationVOCollection().add(organizationVO);

                Collection<RoleDT> roleDTColl = new ArrayList<>();
                RoleDT roleDT = new RoleDT();
                roleDT.setCd(EdxELRConstant.ELR_OP_CD);
                roleDT.setCdDescTxt(EdxELRConstant.ELR_OP_DESC);
                roleDT.setScopingClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
                roleDT.setRoleSeq(1L);
                roleDT.setAddReasonCd("");
                roleDT.setAddTime(organizationVO.getTheOrganizationDT().getAddTime());
                roleDT.setAddUserId(edxLabInformationDT.getUserId());
                roleDT.setItNew(true);
                roleDT.setItDirty(false);
                roleDT.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
                roleDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                roleDT.setLastChgTime(organizationVO.getTheOrganizationDT().getAddTime());
                roleDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                roleDT.setSubjectEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
                roleDTColl.add(roleDT);
                labResultProxyVO.getTheRoleDTCollection().add(roleDT);

                Collection<EntityLocatorParticipationDT> addressCollection = new ArrayList<>();
                if (addressArray != null) {
                    for (int i = 0; i < addressArray.size(); ) {
                        HL7XADType addressType = addressArray.get(i);
                        EntityLocatorParticipationDT elpDT = NBSObjectConverter.organizationAddressType(addressType, EdxELRConstant.ELR_OP_CD, organizationVO);
                        addressCollection.add(elpDT);
                        break;
                    }
                }


                List<HL7XTNType> phoneArray = hl7ORCType.getOrderingFacilityPhoneNumber();
                for (int i = 0; i < phoneArray.size(); ) {
                    HL7XTNType phone = phoneArray.get(i);
                    if (phone != null) {
                        EntityLocatorParticipationDT elpdt = NBSObjectConverter.orgTelePhoneType(phone, EdxELRConstant.ELR_OP_CD, organizationVO);
                        elpdt.setUseCd(EdxELRConstant.ELR_WORKPLACE_CD);
                        organizationVO.getTheEntityLocatorParticipationDTCollection().add(elpdt);
                        break;
                    }
                }

                Collection<OrganizationNameDT> orgNameColl = new ArrayList<>();
                List<HL7XONType> nameArray = hl7ORCType.getOrderingFacilityName();
                if (nameArray != null) {
                    for (int i = 0; i < nameArray.size();) {
                        HL7XONType orgName = nameArray.get(i);
                        OrganizationNameDT organizationNameDT = new OrganizationNameDT();
                        organizationNameDT.setNmTxt(orgName.getHL7OrganizationName());
                        organizationNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
                        organizationNameDT.setOrganizationNameSeq(i);
                        organizationDT.setDisplayNm(organizationNameDT.getNmTxt());
                        orgNameColl.add(organizationNameDT);
                        break;
                    }
                }
                organizationVO.setTheOrganizationNameDTCollection(orgNameColl);
                edxLabInformationDT.setMissingOrderingFacility(false);
            }else{
                edxLabInformationDT.setMissingOrderingFacility(true);
            }

        } catch (Exception e) {
            logger.error("Exception thrown by HL7ORCProcessorget.getOrderingFacility " + e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at HL7ORCProcessorget.getOrderingFacility:"+ e);
        }
        if(organizationVO!=null) {
            edxLabInformationDT.setMultipleOrderingFacility(false);
        }

        return organizationVO;
    }

}
