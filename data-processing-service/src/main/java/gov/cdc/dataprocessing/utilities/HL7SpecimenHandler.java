package gov.cdc.dataprocessing.utilities;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.MaterialVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.model.phdc.HL7PatientResultSPMType;
import gov.cdc.dataprocessing.model.phdc.HL7SPECIMENType;
import gov.cdc.dataprocessing.model.phdc.HL7SPMType;
import gov.cdc.dataprocessing.utilities.component.NBSObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HL7SpecimenHandler {
    private static final Logger logger = LoggerFactory.getLogger(HL7SpecimenHandler.class);

    public static void process251Specimen(HL7PatientResultSPMType hL7PatientResultSPMType, LabResultProxyVO labResultProxyVO,
                                          ObservationDT observationDT, PersonVO collectorVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {
            List<HL7SPECIMENType> hl7SPECIMENTypeArray =hL7PatientResultSPMType.getSPECIMEN();
            if(hl7SPECIMENTypeArray!=null && hl7SPECIMENTypeArray.size()>1)
                edxLabInformationDT.setMultipleSpecimen(true);
            if(hl7SPECIMENTypeArray!=null && !hl7SPECIMENTypeArray.isEmpty()){

                if(hl7SPECIMENTypeArray.size()>1) {
                    edxLabInformationDT.setMultipleSpecimen(true);
                }
                HL7SPECIMENType hl7SPECIMENType = hl7SPECIMENTypeArray.get(0);
                if(hl7SPECIMENType!=null && hl7SPECIMENType.getSPECIMEN()!=null){
                    HL7SPMType hl7SPMType  =hl7SPECIMENType.getSPECIMEN();
                    MaterialVO materialVO = new MaterialVO();
                    MaterialDT materialDT = new MaterialDT();
                    materialVO.setTheMaterialDT(materialDT);
                    materialDT.setMaterialUid((long)(edxLabInformationDT.getNextUid()));
                    materialDT.setRiskCd(edxLabInformationDT.getDangerCode());

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
                    processMaterialVO(labResultProxyVO,collectorVO, materialVO, edxLabInformationDT);
                    //use  Filler Specimen ID (SPM.2.2.1) is present for specimen ID - Defect #14343 Jira
                    if (hl7SPMType.getSpecimenID() != null
                            && hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier() != null
                            && hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier() != null) {
                        String specimenID = hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier();
                        materialVO.getTheEntityIdDTCollection().get(0).setRootExtensionTxt(specimenID);
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7UniversalID() != null) {
                            materialVO.getTheEntityIdDTCollection().get(0).setAssigningAuthorityCd(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7UniversalID());
                        }
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7NamespaceID() != null) {
                            materialVO.getTheEntityIdDTCollection().get(0).setAssigningAuthorityDescTxt(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7NamespaceID());
                        }
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier()
                                .getHL7UniversalIDType() != null) {
                            materialVO.getTheEntityIdDTCollection().get(0).setAssigningAuthorityIdType(hl7SPMType.getSpecimenID()
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


    private static void processMaterialVO(LabResultProxyVO labResultProxyVO,PersonVO collectorVO,MaterialVO materialVO,
                                   EdxLabInformationDT edxLabInformationDT) throws DataProcessingException{
        try {
            EntityIdDT matEntityIdDT = new EntityIdDT();
            matEntityIdDT.setAssigningAuthorityIdType(edxLabInformationDT.getUniversalIdType());
            matEntityIdDT.setEntityUid((long)(edxLabInformationDT.getNextUid()));
            matEntityIdDT.setAddTime(edxLabInformationDT.getAddTime());
            matEntityIdDT.setRootExtensionTxt(edxLabInformationDT.getFillerNumber());
            matEntityIdDT.setTypeCd(EdxELRConstant.ELR_SPECIMEN_CD);
            matEntityIdDT.setTypeDescTxt(EdxELRConstant.ELR_SPECIMEN_DESC);
            matEntityIdDT.setEntityIdSeq(1);
            matEntityIdDT.setAssigningAuthorityCd(edxLabInformationDT.getSendingFacilityClia());
            matEntityIdDT.setAssigningAuthorityDescTxt(edxLabInformationDT.getSendingFacilityName());
            matEntityIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            matEntityIdDT.setAsOfDate(edxLabInformationDT.getAddTime());
            matEntityIdDT.setItNew(true);
            matEntityIdDT.setItDirty(false);
            materialVO.getTheEntityIdDTCollection().add(matEntityIdDT);
            edxLabInformationDT.setRole(EdxELRConstant.ELR_PROVIDER_CD);



            RoleDT roleDT = new RoleDT();
            roleDT.setSubjectEntityUid(materialVO.getTheMaterialDT().getMaterialUid());
            roleDT.setCd(EdxELRConstant.ELR_NO_INFO_CD);
            roleDT.setCdDescTxt(EdxELRConstant.ELR_NO_INFO_DESC);
            roleDT.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
            roleDT.setRoleSeq((long)(1));
            roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
            roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT);
            roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT);
            roleDT.setScopingRoleSeq(1);
            roleDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDT.setItNew(true);
            roleDT.setItDirty(false);
            labResultProxyVO.getTheRoleDTCollection().add(roleDT);

            if(collectorVO!=null){
                RoleDT role2DT = new RoleDT();
                role2DT.setSubjectEntityUid(materialVO.getTheMaterialDT().getMaterialUid());
                role2DT.setItNew(true);
                role2DT.setItDirty(false);
                role2DT.setCd(EdxELRConstant.ELR_NO_INFO_CD);
                role2DT.setCdDescTxt(EdxELRConstant.ELR_NO_INFO_DESC);
                role2DT.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
                role2DT.setRoleSeq((long)(2));
                role2DT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                role2DT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                role2DT.setScopingEntityUid(collectorVO.getThePersonDT().getPersonUid());
                role2DT.setScopingClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                role2DT.setScopingRoleCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
                role2DT.setScopingRoleSeq(1);
                labResultProxyVO.getTheRoleDTCollection().add(role2DT);
            }


            ParticipationDT participationDT = new ParticipationDT();
            participationDT.setSubjectEntityUid(materialVO.getTheMaterialDT().getMaterialUid());
            participationDT.setCd(EdxELRConstant.ELR_NO_INFO_CD);
            participationDT.setTypeCd(EdxELRConstant.ELR_SPECIMEN_CD);
            participationDT.setTypeDescTxt(EdxELRConstant.ELR_SPECIMEN_DESC);
            participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDT.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
            participationDT.setActUid(edxLabInformationDT.getRootObserbationUid());
            participationDT = NBSObjectConverter.defaultParticipationDT(participationDT,edxLabInformationDT);

            labResultProxyVO.getTheParticipationDTCollection().add(participationDT);
            labResultProxyVO.getTheMaterialVOCollection().add(materialVO);
        } catch (Exception e) {
            logger.error("HL7SpecimenProcessor.processSpecimen error thrown "+ e.getMessage(), e);
            throw new DataProcessingException("HL7SpecimenProcessor.processSpecimen error thrown "+ e);
        }

    }



}
