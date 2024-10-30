package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.MaterialContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.phdc.HL7PatientResultSPMType;
import gov.cdc.dataprocessing.model.phdc.HL7SPECIMENType;
import gov.cdc.dataprocessing.model.phdc.HL7SPMType;
import gov.cdc.dataprocessing.utilities.component.data_parser.NBSObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
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
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class HL7SpecimenUtil {
    private static final Logger logger = LoggerFactory.getLogger(HL7SpecimenUtil.class);
    private final NBSObjectConverter nbsObjectConverter;

    public HL7SpecimenUtil(NBSObjectConverter nbsObjectConverter) {
        this.nbsObjectConverter = nbsObjectConverter;
    }

    @SuppressWarnings("java:S3776")
    public void process251Specimen(HL7PatientResultSPMType hL7PatientResultSPMType, LabResultProxyContainer labResultProxyContainer,
                                   ObservationDto observationDto, PersonContainer collectorVO, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
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
                    MaterialContainer materialContainer = new MaterialContainer();
                    MaterialDto materialDto = new MaterialDto();
                    materialContainer.setTheMaterialDto(materialDto);
                    materialDto.setMaterialUid((long)(edxLabInformationDto.getNextUid()));
                    materialDto.setRiskCd(edxLabInformationDto.getDangerCode());

                    if(hl7SPMType.getSpecimenCollectionAmount()!=null && hl7SPMType.getSpecimenCollectionAmount().getHL7Quantity()!=null){
                        materialDto.setQty(String.valueOf(hl7SPMType.getSpecimenCollectionAmount().getHL7Quantity().getHL7Numeric()));
                        if(hl7SPMType.getSpecimenCollectionAmount().getHL7Units()!=null)
                            materialDto.setQtyUnitCd(hl7SPMType.getSpecimenCollectionAmount().getHL7Units().getHL7Identifier());
                    }
                    if(hl7SPMType.getSpecimenType()!=null){
                        materialDto.setCd(hl7SPMType.getSpecimenType().getHL7Identifier());
                        materialDto.setCdDescTxt(hl7SPMType.getSpecimenType().getHL7Text());
                    }

                    List<String> specimenDec = hl7SPMType.getSpecimenDescription();
                    if (specimenDec!=null && specimenDec.size()>0) {
                        materialDto.setDescription(specimenDec.get(0));
                    }
                    if(hl7SPMType.getSpecimenSourceSite()!=null){
                        observationDto.setTargetSiteCd(hl7SPMType.getSpecimenSourceSite().getHL7Identifier());
                        observationDto.setTargetSiteDescTxt(hl7SPMType.getSpecimenSourceSite().getHL7Text());
                    }
                    if(hl7SPMType.getSpecimenCollectionDateTime()!=null) {
                        observationDto.setEffectiveFromTime(nbsObjectConverter.processHL7TSTypeWithMillis(hl7SPMType.getSpecimenCollectionDateTime().getHL7RangeStartDateTime(), EdxELRConstant.DATE_VALIDATION_SPM_SPECIMEN_COLLECTION_DATE_MSG));
                    }
                    processMaterialVO(labResultProxyContainer,collectorVO, materialContainer, edxLabInformationDto);
                    //use  Filler Specimen ID (SPM.2.2.1) is present for specimen ID - Defect #14343 Jira
                    if (hl7SPMType.getSpecimenID() != null
                            && hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier() != null
                            && hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier() != null) {
                        String specimenID = hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier();
                        ArrayList<EntityIdDto> entityIdArrList = new ArrayList<>(materialContainer.getTheEntityIdDtoCollection());
                        entityIdArrList.get(0).setRootExtensionTxt(specimenID);
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7UniversalID() != null) {
                            entityIdArrList.get(0).setAssigningAuthorityCd(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7UniversalID());
                        }
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier().getHL7NamespaceID() != null) {
                            entityIdArrList.get(0).setAssigningAuthorityDescTxt(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7NamespaceID());
                        }
                        if (hl7SPMType.getSpecimenID().getHL7FillerAssignedIdentifier()
                                .getHL7UniversalIDType() != null) {
                            entityIdArrList.get(0).setAssigningAuthorityIdType(hl7SPMType.getSpecimenID()
                                    .getHL7FillerAssignedIdentifier().getHL7UniversalIDType());
                        }

                        materialContainer.setTheEntityIdDtoCollection(entityIdArrList);

                    }
                }
            }
        } catch (Exception e) {
            logger.error("HL7SpecimenProcessor.process251Specimen error thrown {}", e.getMessage());
            throw new DataProcessingException( "HL7SpecimenProcessor.process251Specimen error thrown "+ e.getMessage() + e);
        }
    }


    private void processMaterialVO(LabResultProxyContainer labResultProxyContainer, PersonContainer collectorVO, MaterialContainer materialContainer,
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
            materialContainer.getTheEntityIdDtoCollection().add(matEntityIdDto);
            edxLabInformationDto.setRole(EdxELRConstant.ELR_PROVIDER_CD);



            RoleDto roleDto = new RoleDto();
            roleDto.setSubjectEntityUid(materialContainer.getTheMaterialDto().getMaterialUid());
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
                role2DT.setSubjectEntityUid(materialContainer.getTheMaterialDto().getMaterialUid());
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


            ParticipationDto participationDto = new ParticipationDto();
            participationDto.setSubjectEntityUid(materialContainer.getTheMaterialDto().getMaterialUid());
            participationDto.setCd(EdxELRConstant.ELR_NO_INFO_CD);
            participationDto.setTypeCd(EdxELRConstant.ELR_SPECIMEN_CD);
            participationDto.setTypeDescTxt(EdxELRConstant.ELR_SPECIMEN_DESC);
            participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDto.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
            participationDto.setActUid(edxLabInformationDto.getRootObserbationUid());
            participationDto = nbsObjectConverter.defaultParticipationDT(participationDto, edxLabInformationDto);

            labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);
            labResultProxyContainer.getTheMaterialContainerCollection().add(materialContainer);
        } catch (Exception e) {
            logger.error("HL7SpecimenProcessor.processSpecimen error thrown {}", e.getMessage());
            throw new DataProcessingException("HL7SpecimenProcessor.processSpecimen error thrown "+ e);
        }

    }



}
