package gov.cdc.dataprocessing.utilities.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.MaterialVO;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.phdc.HL7PatientResultSPMType;
import gov.cdc.dataprocessing.model.phdc.HL7SPECIMENType;
import gov.cdc.dataprocessing.model.phdc.HL7SPMType;
import gov.cdc.dataprocessing.utilities.component.data_parser.NBSObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HL7SpecimenUtil {
    private static final Logger logger = LoggerFactory.getLogger(HL7SpecimenUtil.class);

    public static void process251Specimen(HL7PatientResultSPMType hL7PatientResultSPMType, LabResultProxyContainer labResultProxyContainer,
                                          ObservationDT observationDT, PersonContainer collectorVO, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            List<HL7SPECIMENType> hl7SPECIMENTypeArray =hL7PatientResultSPMType.getSPECIMEN();
            if(hl7SPECIMENTypeArray!=null && hl7SPECIMENTypeArray.size()>1)
                edxLabInformationDto.setMultipleSpecimen(true);
            if(hl7SPECIMENTypeArray!=null && !hl7SPECIMENTypeArray.isEmpty()){

                if(hl7SPECIMENTypeArray.size()>1) {
                    edxLabInformationDto.setMultipleSpecimen(true);
                }
                HL7SPECIMENType hl7SPECIMENType = hl7SPECIMENTypeArray.get(0);
                if(hl7SPECIMENType!=null && hl7SPECIMENType.getSPECIMEN()!=null){
                    HL7SPMType hl7SPMType  =hl7SPECIMENType.getSPECIMEN();
                    MaterialVO materialVO = new MaterialVO();
                    MaterialDT materialDT = new MaterialDT();
                    materialVO.setTheMaterialDT(materialDT);
                    materialDT.setMaterialUid((long)(edxLabInformationDto.getNextUid()));
                    materialDT.setRiskCd(edxLabInformationDto.getDangerCode());

                    if(hl7SPMType.getSpecimenCollectionAmount()!=null && hl7SPMType.getSpecimenCollectionAmount().getHL7Quantity()!=null){
                        materialDT.setQty(String.valueOf(hl7SPMType.getSpecimenCollectionAmount().getHL7Quantity().getHL7Numeric()));
                        if(hl7SPMType.getSpecimenCollectionAmount().getHL7Units()!=null)
                            materialDT.setQtyUnitCd(hl7SPMType.getSpecimenCollectionAmount().getHL7Units().getHL7Identifier());
                    }
                    if(hl7SPMType.getSpecimenType()!=null){
                        materialDT.setCd(hl7SPMType.getSpecimenType().getHL7Identifier());
                        materialDT.setCdDescTxt(hl7SPMType.getSpecimenType().getHL7Text());
                    }

                    List<String> specimenDec = hl7SPMType.getSpecimenDescription();
                    if (specimenDec!=null && specimenDec.size()>0) {
                        materialDT.setDescription(specimenDec.get(0));
                    }
                    if(hl7SPMType.getSpecimenSourceSite()!=null){
                        observationDT.setTargetSiteCd(hl7SPMType.getSpecimenSourceSite().getHL7Identifier());
                        observationDT.setTargetSiteDescTxt(hl7SPMType.getSpecimenSourceSite().getHL7Text());
                    }
                    if(hl7SPMType.getSpecimenCollectionDateTime()!=null) {
                        observationDT.setEffectiveFromTime(NBSObjectConverter.processHL7TSTypeWithMillis(hl7SPMType.getSpecimenCollectionDateTime().getHL7RangeStartDateTime(), EdxELRConstant.DATE_VALIDATION_SPM_SPECIMEN_COLLECTION_DATE_MSG));
                    }
                    processMaterialVO(labResultProxyContainer,collectorVO, materialVO, edxLabInformationDto);
                    //use  Filler Specimen ID (SPM.2.2.1) is present for specimen ID - Defect #14343 Jira
                    if (hl7SPMType.getSpecimenID() != null
                            && hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier() != null
                            && hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier() != null) {
                        String specimenID = hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier();
                        materialVO.getTheEntityIdDtoCollection().get(0).setRootExtensionTxt(specimenID);
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7UniversalID() != null) {
                            materialVO.getTheEntityIdDtoCollection().get(0).setAssigningAuthorityCd(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7UniversalID());
                        }
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7NamespaceID() != null) {
                            materialVO.getTheEntityIdDtoCollection().get(0).setAssigningAuthorityDescTxt(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7NamespaceID());
                        }
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier()
                                .getHL7UniversalIDType() != null) {
                            materialVO.getTheEntityIdDtoCollection().get(0).setAssigningAuthorityIdType(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7UniversalIDType());
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error("HL7SpecimenProcessor.process251Specimen error thrown "+ e.getMessage(), e);
            throw new DataProcessingException( "HL7SpecimenProcessor.process251Specimen error thrown "+ e.getMessage() + e);
        }
    }


    private static void processMaterialVO(LabResultProxyContainer labResultProxyContainer, PersonContainer collectorVO, MaterialVO materialVO,
                                          EdxLabInformationDto edxLabInformationDto) throws DataProcessingException{
        try {
            EntityIdDto matEntityIdDto = new EntityIdDto();
            matEntityIdDto.setAssigningAuthorityIdType(edxLabInformationDto.getUniversalIdType());
            matEntityIdDto.setEntityUid((long)(edxLabInformationDto.getNextUid()));
            matEntityIdDto.setAddTime(edxLabInformationDto.getAddTime());
            matEntityIdDto.setRootExtensionTxt(edxLabInformationDto.getFillerNumber());
            matEntityIdDto.setTypeCd(EdxELRConstant.ELR_SPECIMEN_CD);
            matEntityIdDto.setTypeDescTxt(EdxELRConstant.ELR_SPECIMEN_DESC);
            matEntityIdDto.setEntityIdSeq(1);
            matEntityIdDto.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            matEntityIdDto.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            matEntityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            matEntityIdDto.setAsOfDate(edxLabInformationDto.getAddTime());
            matEntityIdDto.setItNew(true);
            matEntityIdDto.setItDirty(false);
            materialVO.getTheEntityIdDtoCollection().add(matEntityIdDto);
            edxLabInformationDto.setRole(EdxELRConstant.ELR_PROVIDER_CD);



            RoleDto roleDto = new RoleDto();
            roleDto.setSubjectEntityUid(materialVO.getTheMaterialDT().getMaterialUid());
            roleDto.setCd(EdxELRConstant.ELR_NO_INFO_CD);
            roleDto.setCdDescTxt(EdxELRConstant.ELR_NO_INFO_DESC);
            roleDto.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
            roleDto.setRoleSeq((long)(1));
            roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
            roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT);
            roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT);
            roleDto.setScopingRoleSeq(1);
            roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);

            if(collectorVO!=null){
                RoleDto role2DT = new RoleDto();
                role2DT.setSubjectEntityUid(materialVO.getTheMaterialDT().getMaterialUid());
                role2DT.setItNew(true);
                role2DT.setItDirty(false);
                role2DT.setCd(EdxELRConstant.ELR_NO_INFO_CD);
                role2DT.setCdDescTxt(EdxELRConstant.ELR_NO_INFO_DESC);
                role2DT.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
                role2DT.setRoleSeq((long)(2));
                role2DT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                role2DT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                role2DT.setScopingEntityUid(collectorVO.getThePersonDto().getPersonUid());
                role2DT.setScopingClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                role2DT.setScopingRoleCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
                role2DT.setScopingRoleSeq(1);
                labResultProxyContainer.getTheRoleDtoCollection().add(role2DT);
            }


            ParticipationDT participationDT = new ParticipationDT();
            participationDT.setSubjectEntityUid(materialVO.getTheMaterialDT().getMaterialUid());
            participationDT.setCd(EdxELRConstant.ELR_NO_INFO_CD);
            participationDT.setTypeCd(EdxELRConstant.ELR_SPECIMEN_CD);
            participationDT.setTypeDescTxt(EdxELRConstant.ELR_SPECIMEN_DESC);
            participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDT.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
            participationDT.setActUid(edxLabInformationDto.getRootObserbationUid());
            participationDT = NBSObjectConverter.defaultParticipationDT(participationDT, edxLabInformationDto);

            labResultProxyContainer.getTheParticipationDTCollection().add(participationDT);
            labResultProxyContainer.getTheMaterialVOCollection().add(materialVO);
        } catch (Exception e) {
            logger.error("HL7SpecimenProcessor.processSpecimen error thrown "+ e.getMessage(), e);
            throw new DataProcessingException("HL7SpecimenProcessor.processSpecimen error thrown "+ e);
        }

    }



}
