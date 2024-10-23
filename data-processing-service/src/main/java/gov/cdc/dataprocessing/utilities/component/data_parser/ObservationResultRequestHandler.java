package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.CommonLabUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

@Component
public class ObservationResultRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObservationResultRequestHandler.class);

    private final ICatchingValueService checkingValueService;
    private final NBSObjectConverter nbsObjectConverter;
    private final CommonLabUtil commonLabUtil;


    public ObservationResultRequestHandler(
            ICatchingValueService checkingValueService,
            NBSObjectConverter nbsObjectConverter, CommonLabUtil commonLabUtil) {
        this.checkingValueService = checkingValueService;
        this.nbsObjectConverter = nbsObjectConverter;
        this.commonLabUtil = commonLabUtil;
    }

    public LabResultProxyContainer getObservationResultRequest(List<HL7OBSERVATIONType> observationRequestArray,
                                                               LabResultProxyContainer labResultProxyContainer,
                                                               EdxLabInformationDto edxLabInformationDto) throws DataProcessingException{
        try {
            for (HL7OBSERVATIONType hl7OBSERVATIONType : observationRequestArray) {
                try {
                    ObservationContainer observationContainer = getObservationResult(hl7OBSERVATIONType.getObservationResult(), labResultProxyContainer, edxLabInformationDto);
                    getObsReqNotes(hl7OBSERVATIONType.getNotesAndComments(), observationContainer);
                    labResultProxyContainer.getTheObservationContainerCollection().add(observationContainer);
                } catch (Exception e) {
                    logger.error("ObservationResultRequest.getObservationResultRequest Exception thrown while processing observationRequestArray. Please check!!!" + e.getMessage(), e);
                    throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObservationResultRequest while oricessing observationRequestArray:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("ObservationResultRequest.getObservationResultRequest Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObservationResultRequest:"+ e.getMessage());
        }
        return labResultProxyContainer;
    }

    @SuppressWarnings({"java:S6541","java:S3776"})
    private ObservationContainer getObservationResult(HL7OBXType hl7OBXType,
                                                      LabResultProxyContainer labResultProxyContainer,
                                                      EdxLabInformationDto edxLabInformationDto)
            throws DataProcessingException {
        ObservationContainer observationContainer;
        try {
            observationContainer = new ObservationContainer();

            ObservationDto observationDto = new ObservationDto();
            observationDto.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
            observationDto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
            observationDto.setObservationUid((long)(edxLabInformationDto.getNextUid()));
            observationDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            if(edxLabInformationDto.isParentObsInd()){
                observationDto.setObsDomainCdSt1(EdxELRConstant.ELR_REF_RESULT_CD);
            }else{
                observationDto.setObsDomainCdSt1(EdxELRConstant.ELR_RESULT_CD);
            }


            observationDto.setItNew(true);
            observationDto.setItDirty(false);
            observationContainer.setItNew(true);
            observationContainer.setItDirty(false);
            observationContainer.setTheObservationDto(observationDto);
            EdxLabIdentiferDto edxLabIdentiferDT = new EdxLabIdentiferDto();

            if(!edxLabInformationDto.isParentObsInd() && (hl7OBXType.getObservationIdentifier()== null
                    || (hl7OBXType.getObservationIdentifier().getHL7Identifier()==null && hl7OBXType.getObservationIdentifier().getHL7AlternateIdentifier()==null))){
                edxLabInformationDto.setResultedTestNameMissing(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                String xmlElementName = commonLabUtil.getXMLElementNameForOBX(hl7OBXType)+".ObservationIdentifier";
                throw new DataProcessingException(EdxELRConstant.NO_RESULT_NAME+" XMLElementName: "+xmlElementName);
            }

            if(hl7OBXType.getObservationIdentifier().getHL7Identifier()!=null){
                edxLabIdentiferDT.setIdentifer(hl7OBXType.getObservationIdentifier().getHL7Identifier());
            }
            else if(hl7OBXType.getObservationIdentifier().getHL7AlternateIdentifier()!=null) {
                edxLabIdentiferDT.setIdentifer(hl7OBXType.getObservationIdentifier().getHL7AlternateIdentifier());
            }
            edxLabIdentiferDT.setSubMapID(hl7OBXType.getObservationSubID());
            edxLabIdentiferDT.setObservationValues(hl7OBXType.getObservationValue()) ;
            edxLabIdentiferDT.setObservationUid(observationDto.getObservationUid());
            edxLabInformationDto.getEdxSusLabDTMap().put(edxLabIdentiferDT.getObservationUid(),edxLabIdentiferDT);
            if(edxLabInformationDto.getEdxLabIdentiferDTColl()==null) {
                edxLabInformationDto.setEdxLabIdentiferDTColl(new ArrayList<>());
            }

            edxLabInformationDto.getEdxLabIdentiferDTColl().add(edxLabIdentiferDT);
            Collection<ActIdDto> actIdDtoColl =  new ArrayList<>();
            ActIdDto actIdDto = new ActIdDto();
            actIdDto.setActIdSeq(1);
            actIdDto.setActUid(observationDto.getObservationUid());
            actIdDto.setRootExtensionTxt(edxLabInformationDto.getMessageControlID());
            actIdDto.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            actIdDto.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            actIdDto.setTypeCd(EdxELRConstant.ELR_MESSAGE_CTRL_CD);
            actIdDto.setTypeDescTxt(EdxELRConstant.ELR_MESSAGE_CTRL_DESC);
            actIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDtoColl.add(actIdDto);

            ActIdDto act2IdDT = new ActIdDto();
            act2IdDT.setActUid(observationDto.getObservationUid());
            act2IdDT.setActIdSeq(2);
            act2IdDT.setRootExtensionTxt(edxLabInformationDto.getFillerNumber());
            act2IdDT.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            act2IdDT.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            act2IdDT.setTypeCd(EdxELRConstant.ELR_FILLER_NUMBER_CD);
            act2IdDT.setTypeDescTxt(EdxELRConstant.ELR_FILLER_NUMBER_DESC);
            act2IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDtoColl.add(act2IdDT);

            /*
             * ND-18349 HHS ELR Updates for COVID: Updates Required to Process OBX 18
             */
            List<HL7EIType> equipmentIdType = hl7OBXType.getEquipmentInstanceIdentifier();
            actIdDtoColl =  setEquipments(equipmentIdType, observationDto, actIdDtoColl);


            observationContainer.setTheActIdDtoCollection(actIdDtoColl);

            ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
            actRelationshipDto.setItNew(true);
            actRelationshipDto.setItDirty(false);
            actRelationshipDto.setAddTime(edxLabInformationDto.getAddTime());
            actRelationshipDto.setLastChgTime(edxLabInformationDto.getAddTime());
            actRelationshipDto.setRecordStatusTime(edxLabInformationDto.getAddTime());
            actRelationshipDto.setTypeCd(EdxELRConstant.ELR_COMP_CD);
            actRelationshipDto.setTypeDescTxt(EdxELRConstant.ELR_COMP_DESC);
            actRelationshipDto.setSourceActUid(observationContainer.getTheObservationDto().getObservationUid());
            if(edxLabInformationDto.isParentObsInd()){
                actRelationshipDto.setTargetActUid(edxLabInformationDto.getParentObservationUid());
            }
            else {
                actRelationshipDto.setTargetActUid(edxLabInformationDto.getRootObserbationUid());
            }
            actRelationshipDto.setTargetClassCd(EdxELRConstant.ELR_OBS);
            actRelationshipDto.setSourceClassCd(EdxELRConstant.ELR_OBS);
            actRelationshipDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actRelationshipDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            actRelationshipDto.setSequenceNbr(1);
            actRelationshipDto.setItNew(true);
            actRelationshipDto.setItDirty(false);
            if(labResultProxyContainer.getTheActRelationshipDtoCollection()==null) {
                labResultProxyContainer.setTheActRelationshipDtoCollection(new ArrayList<>());
            }
            labResultProxyContainer.getTheActRelationshipDtoCollection().add(actRelationshipDto);

            HL7CWEType obsIdentifier= hl7OBXType.getObservationIdentifier();
            if(obsIdentifier!=null){
                if(obsIdentifier.getHL7Identifier()!=null)
                {
                    observationDto.setCd(obsIdentifier.getHL7Identifier());
                }
                if(obsIdentifier.getHL7Text()!=null)
                {
                    observationDto.setCdDescTxt(obsIdentifier.getHL7Text());
                }

                if(observationDto.getCd()==null && obsIdentifier.getHL7AlternateIdentifier()!=null)
                {
                    observationDto.setCd(obsIdentifier.getHL7AlternateIdentifier());
                }
                else if(observationDto.getCd()!=null && obsIdentifier.getHL7AlternateIdentifier()!=null)
                {
                    observationDto.setAltCd(obsIdentifier.getHL7AlternateIdentifier());
                }
                if(obsIdentifier.getHL7AlternateText()!=null && observationDto.getCdDescTxt()==null)
                {
                    observationDto.setCdDescTxt(obsIdentifier.getHL7AlternateText());
                }
                else if(obsIdentifier.getHL7AlternateText()!=null && observationDto.getCdDescTxt()!=null)
                {
                    observationDto.setAltCdDescTxt(obsIdentifier.getHL7AlternateText());
                }


                if(observationDto.getCd()!=null || observationDto.getCdDescTxt()!=null){
                    observationDto.setCdSystemCd(obsIdentifier.getHL7NameofCodingSystem());
                    observationDto.setCdSystemDescTxt(obsIdentifier.getHL7NameofCodingSystem());
                }
                if(observationDto.getAltCd()!=null || observationDto.getAltCdDescTxt()!=null){
                    observationDto.setAltCdSystemCd(obsIdentifier.getHL7NameofAlternateCodingSystem());
                    observationDto.setAltCdSystemDescTxt(obsIdentifier.getHL7NameofAlternateCodingSystem());
                }else if(observationDto.getCdSystemCd()==null){
                    observationDto.setCdSystemCd(obsIdentifier.getHL7NameofAlternateCodingSystem());
                    observationDto.setCdSystemDescTxt(obsIdentifier.getHL7NameofAlternateCodingSystem());
                }
                if (observationDto.getCdSystemCd() != null
                        && observationDto.getCdSystemCd().equals(EdxELRConstant.ELR_LOINC_CD)) {
                    observationDto.setCdSystemCd(EdxELRConstant.ELR_LOINC_CD);
                    observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_LOINC_DESC);

                    var aOELOINCs = SrteCache.loincCodesMap;
                    if (aOELOINCs != null && aOELOINCs.containsKey(observationDto.getCd())) {
                        observationDto.setMethodCd(NEDSSConstant.AOE_OBS);
                    }
                }else if(observationDto.getCdSystemCd()!=null && observationDto.getCdSystemCd().equals(EdxELRConstant.ELR_SNOMED_CD)){
                    observationDto.setCdSystemCd(EdxELRConstant.ELR_SNOMED_CD);
                    observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                }else if(observationDto.getCdSystemCd()!=null && observationDto.getCdSystemCd().equals(EdxELRConstant.ELR_LOCAL_CD)){
                    observationDto.setCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }

                if(observationDto.getAltCd()!=null && observationDto.getAltCdSystemCd()!=null && observationDto.getAltCdSystemCd().equals(EdxELRConstant.ELR_SNOMED_CD)){
                    observationDto.setAltCdSystemCd(EdxELRConstant.ELR_SNOMED_CD);
                    observationDto.setAltCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                }else if(observationDto.getAltCd()!=null){
                    observationDto.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDto.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }

                if(edxLabInformationDto.isParentObsInd()){
                    if(observationContainer.getTheObservationDto().getCd() == null || observationContainer.getTheObservationDto().getCd().trim().equals("")) {
                        edxLabInformationDto.setDrugNameMissing(true);
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                        throw new DataProcessingException(EdxELRConstant.NO_DRUG_NAME);
                    }
                }
            }
            else{
                logger.error("ObservationResultRequest.getObservationResult The Resulted Test ObservationCd  can't be set to null. Please check." + observationDto.getCd());
                throw new DataProcessingException("ObservationResultRequest.getObservationResult The Resulted Test ObservationCd  can't be set to null. Please check." + observationDto.getCd());
            }


            List<String>  obsValueArray =hl7OBXType.getObservationValue();
            String elementName = "ObservationValue";
            for (String text : obsValueArray) {
                formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);
                if (!(hl7OBXType.getValueType().equals(EdxELRConstant.ELR_STRING_CD)
                        || hl7OBXType.getValueType().equals(EdxELRConstant.ELR_TEXT_CD)
                        || hl7OBXType.getValueType().equals(EdxELRConstant.ELR_TEXT_DT)
                        || hl7OBXType.getValueType().equals(EdxELRConstant.ELR_TEXT_TS))) {
                    break;
                }
            }

            observationContainer = processingReferringRange(hl7OBXType, observationContainer);

            var abnormalFlag = hl7OBXType.getAbnormalFlags();
            observationContainer = processingAbnormalFlag(abnormalFlag, observationDto, observationContainer);


            if(hl7OBXType.getObservationResultStatus()!=null)
            {
                String toCode = checkingValueService.findToCode("ELR_LCA_STATUS", hl7OBXType.getObservationResultStatus(), "ACT_OBJ_ST");
                if (toCode != null && !toCode.equals("") && !toCode.equals(" ")){
                    observationDto.setStatusCd(toCode.trim());

                }else{
                    observationDto.setStatusCd(hl7OBXType.getObservationResultStatus());
                }
            }
            // It was decided to use only OBX19 for this field instead of OBX14(as in 2.3.1) - ER 1085 in Rel4.4
            if(hl7OBXType.getDateTimeOftheAnalysis()!=null){
                observationDto.setActivityToTime(nbsObjectConverter.processHL7TSType(hl7OBXType.getDateTimeOftheAnalysis(),EdxELRConstant.DATE_VALIDATION_OBX_LAB_PERFORMED_DATE_MSG));
            }

            observationDto.setRptToStateTime(edxLabInformationDto.getLastChgTime());
            //2.3.1 to 2.5.1 translation copies this filed from OBX-15(CWE data type) to OBX-23(XON data type) which is required, so always reading it from OBX-23.
            HL7XONType hl7XONTypeName = hl7OBXType.getPerformingOrganizationName();
            if(hl7XONTypeName!=null){
                OrganizationContainer producerOrg = getPerformingFacility(hl7OBXType, observationContainer.getTheObservationDto().getObservationUid(), labResultProxyContainer, edxLabInformationDto);
                labResultProxyContainer.getTheOrganizationContainerCollection().add(producerOrg);
            }
            List<HL7CEType> methodArray = hl7OBXType.getObservationMethod();
            observationContainer = processingObservationMethod(methodArray, edxLabInformationDto, observationContainer);

        } catch (Exception e) {
            logger.error("ObservationResultRequest.getObservationResult Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObservationResult:"+ e.getMessage());
        }

        return observationContainer;
    }

    private OrganizationContainer getPerformingFacility(HL7OBXType hl7OBXType, long observationUid,
                                                        LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        HL7XONType hl7XONTypeName = hl7OBXType.getPerformingOrganizationName();

        OrganizationContainer organizationContainer;
        try {
            organizationContainer = new OrganizationContainer();
            OrganizationDto organizationDto = new OrganizationDto();
            organizationContainer.setItNew(true);
            organizationContainer.setItDirty(false);


            organizationDto.setCd(EdxELRConstant.ELR_SENDING_LAB_CD);
            organizationDto.setCdDescTxt(EdxELRConstant.ELR_LABORATORY_DESC);
            organizationDto.setStandardIndustryClassCd(EdxELRConstant.ELR_STANDARD_INDUSTRY_CLASS_CD);
            organizationDto.setStandardIndustryDescTxt(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
            organizationDto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
            organizationDto.setOrganizationUid((long)(edxLabInformationDto.getNextUid()));
            organizationDto.setAddUserId(edxLabInformationDto.getUserId());
            organizationDto.setItNew(true);
            organizationDto.setItDirty(false);
            organizationContainer.setTheOrganizationDto(organizationDto);
            
            EntityIdDto entityIdDto = new EntityIdDto();
            entityIdDto.setEntityUid(organizationDto.getOrganizationUid());
            entityIdDto.setRootExtensionTxt(hl7XONTypeName.getHL7OrganizationIdentifier());
            entityIdDto.setTypeCd(EdxELRConstant.ELR_FACILITY_CD);
            entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_FACILITY_DESC);
            entityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDto.setAsOfDate(edxLabInformationDto.getAddTime());

            entityIdDto.setEntityIdSeq(1);
            if(hl7XONTypeName.getHL7AssigningAuthority()!=null){
                entityIdDto.setAssigningAuthorityCd(hl7XONTypeName.getHL7AssigningAuthority().getHL7UniversalID());
                entityIdDto.setAssigningAuthorityIdType(hl7XONTypeName.getHL7AssigningAuthority().getHL7UniversalIDType());
            }
            if(hl7XONTypeName.getHL7AssigningAuthority()!=null && hl7XONTypeName.getHL7AssigningAuthority().getHL7NamespaceID()!=null 
                    && hl7XONTypeName.getHL7AssigningAuthority().getHL7NamespaceID().equals(EdxELRConstant.ELR_CLIA_CD)) {
                entityIdDto.setAssigningAuthorityDescTxt(EdxELRConstant.ELR_CLIA_DESC);
            }
            
            organizationContainer.getTheEntityIdDtoCollection().add(entityIdDto);

            ParticipationDto participationDto = new ParticipationDto();
            participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDto.setCd(EdxELRConstant.ELR_REPORTING_ENTITY_CD);
            participationDto.setTypeCd(EdxELRConstant.ELR_LAB_PERFORMER_CD);
            participationDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());

            participationDto.setItNew(true);
            participationDto.setItDirty(false);
            participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_PERFORMER_DESC);
            participationDto.setSubjectClassCd(EdxELRConstant.ELR_ORG);
            participationDto.setSubjectEntityUid(organizationDto.getOrganizationUid());
            participationDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            participationDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            participationDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
            participationDto.setActUid(observationUid);

            labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);

            RoleDto roleDto = new RoleDto();
            roleDto.setCd(EdxELRConstant.ELR_REPORTING_ENTITY_CD);
            roleDto.setCdDescTxt(EdxELRConstant.ELR_REPORTING_ENTITY_DESC);
            roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setRoleSeq((long)(1));
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            roleDto.setAddReasonCd("");
            roleDto.setAddTime(organizationContainer.getTheOrganizationDto().getAddTime());
            roleDto.setAddUserId(organizationContainer.getTheOrganizationDto().getAddUserId());
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            roleDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
            roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDto.setLastChgTime(organizationContainer.getTheOrganizationDto().getAddTime());
            roleDto.setSubjectClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
            roleDto.setSubjectEntityUid(organizationContainer.getTheOrganizationDto().getOrganizationUid());
            roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
            labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);

            Collection<OrganizationNameDto> orgNameColl = new ArrayList<>();
            OrganizationNameDto organizationNameDto = new OrganizationNameDto();
            organizationNameDto.setNmTxt(hl7XONTypeName.getHL7OrganizationName());
            organizationNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            organizationNameDto.setOrganizationNameSeq(1);
            organizationDto.setDisplayNm(organizationNameDto.getNmTxt());
            orgNameColl.add(organizationNameDto);

            organizationContainer.setTheOrganizationNameDtoCollection(orgNameColl);
            HL7XADType addressType = hl7OBXType.getPerformingOrganizationAddress();
            Collection<EntityLocatorParticipationDto> addressCollection = new ArrayList<>();

            if (addressType != null) {
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.organizationAddressType(addressType, EdxELRConstant.ELR_OP_CD, organizationContainer);
                addressCollection.add(elpDT);
            }

        } catch (Exception e) {
            logger.error("ObservationResultRequest.getPerformingFacility Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getPerformingFacility:"+ e.getMessage());

        }
        return organizationContainer;
    }

    @SuppressWarnings({"java:S3776", "java:S6541", "java:S125"})
    protected void formatValue(String text, HL7OBXType hl7OBXType, ObservationContainer observationContainer, EdxLabInformationDto edxLabInformationDto, String elementName) throws DataProcessingException{
        String type = "";
        try {
            type = hl7OBXType.getValueType();
            HL7CEType cEUnit = hl7OBXType.getUnits();
            if(type!=null){
                if(type.equals(EdxELRConstant.ELR_CODED_WITH_EXC_CD) ||type.equals(EdxELRConstant.ELR_CODED_EXEC_CD))
                {
                    if(text!=null){
                        ObsValueCodedDto obsvalueDT= new ObsValueCodedDto();
                        obsvalueDT.setItNew(true);
                        obsvalueDT.setItDirty(false);
                        String[] textValue = text.split("\\^");

                        if (!text.isEmpty() && textValue.length>0) {
                            obsvalueDT.setCode(textValue[0]);
                            obsvalueDT.setDisplayName(textValue[1]);
                            if(textValue.length==2){
                                obsvalueDT.setCodeSystemCd(EdxELRConstant.ELR_SNOMED_CD);
                            }else if(textValue.length==3){
                                obsvalueDT.setCodeSystemCd(textValue[2]);
                            }

                            if (textValue.length >= 6) {
                                obsvalueDT.setAltCd(textValue[3]);
                                obsvalueDT.setAltCdDescTxt(textValue[4]);
                                /*
                                if(textValue.length==4){
                                    obsvalueDT.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                                }else if(textValue.length==5 || textValue.length>5){
                                    obsvalueDT.setAltCdSystemCd(textValue[5]);
                                }
                                */
                            }
                        }
                        if((obsvalueDT.getCode()==null || obsvalueDT.getCode().trim().equals(""))
                                && (obsvalueDT.getAltCd()==null || obsvalueDT.getAltCd().trim().equals("")))
                        {
                            edxLabInformationDto.setReflexResultedTestCdMissing(true);
                            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                            String xmlElementName = commonLabUtil.getXMLElementNameForOBX(hl7OBXType)+"."+elementName;
                            throw new DataProcessingException(EdxELRConstant.NO_REFLEX_RESULT_NM+" XMLElementName: "+xmlElementName);
                        }

                        if(obsvalueDT.getCode() == null || obsvalueDT.getCode().trim().equals(""))
                        {
                            obsvalueDT.setCode(obsvalueDT.getAltCd());
                            obsvalueDT.setDisplayName(obsvalueDT.getAltCdDescTxt());
                            obsvalueDT.setCodeSystemCd(obsvalueDT.getAltCdSystemCd());
                            obsvalueDT.setAltCd(null);
                            obsvalueDT.setAltCdDescTxt(null);
                            obsvalueDT.setAltCdSystemCd(null);
                        }

                        if(obsvalueDT.getCodeSystemCd()!=null && obsvalueDT.getCodeSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_SNOMED_CD))
                        {
                            obsvalueDT.setCodeSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                        }
                        else if(obsvalueDT.getCodeSystemCd()!=null && obsvalueDT.getCodeSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_LOCAL_CD))
                        {
                            obsvalueDT.setCodeSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                        }
                        if(obsvalueDT.getAltCdSystemCd()!=null && obsvalueDT.getAltCdSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_SNOMED_CD))
                        {
                            obsvalueDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                        }
                        else if(obsvalueDT.getAltCdSystemCd()!=null && obsvalueDT.getAltCdSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_LOCAL_CD))
                        {
                            obsvalueDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                        }

                        obsvalueDT.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());

                        if(observationContainer.getTheObsValueCodedDtoCollection()==null) {
                            observationContainer.setTheObsValueCodedDtoCollection(new ArrayList<>());
                        }
                        observationContainer.getTheObsValueCodedDtoCollection().add(obsvalueDT);
                    }
                }
                else if (type.equals(EdxELRConstant.ELR_STUCTURED_NUMERIC_CD))
                {
                    ObsValueNumericDto obsValueNumericDto = new ObsValueNumericDto();
                    obsValueNumericDto.setObsValueNumericSeq(1);
                    StringTokenizer st = new StringTokenizer(text, "^");
                    obsValueNumericDto.setItNew(true);
                    obsValueNumericDto.setItDirty(false);
                    int i = 0;
                    if (text.indexOf("^") == 0) {
                        i = 1;
                    }
                    while (st.hasMoreTokens()) {
                        i++;
                        String token = st.nextToken();
                        if (i == 1) {
                            if (token != null && token.equals("&lt;"))
                                obsValueNumericDto.setComparatorCd1("<");
                            else if (token != null && token.equals("&gt;"))
                                obsValueNumericDto.setComparatorCd1(">");
                            else
                                obsValueNumericDto.setComparatorCd1(token);
                        } else if (i == 2) {
                            obsValueNumericDto.setNumericValue1(new BigDecimal(token));
                        }
                        else if (i == 3) {
                            obsValueNumericDto.setSeparatorCd(token);
                        }
                        else if (i == 4) {
                            obsValueNumericDto.setNumericValue2(new BigDecimal(token));
                        }
                    }
                    if (cEUnit != null)
                        obsValueNumericDto.setNumericUnitCd(cEUnit.getHL7Identifier());
                    obsValueNumericDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                    if (observationContainer.getTheObsValueNumericDtoCollection() == null) {
                        observationContainer.setTheObsValueNumericDtoCollection(new ArrayList<>());
                    }
                    observationContainer.getTheObsValueNumericDtoCollection().add(obsValueNumericDto);

                }
                else if (type.equals(EdxELRConstant.ELR_NUMERIC_CD))
                {
                    ObsValueNumericDto obsValueNumericDto = new ObsValueNumericDto();
                    obsValueNumericDto.setObsValueNumericSeq(1);
                    obsValueNumericDto.setItNew(true);
                    obsValueNumericDto.setItDirty(false);

                    obsValueNumericDto.setNumericValue1(new BigDecimal(text));


                    if (cEUnit != null) {
                        obsValueNumericDto.setNumericUnitCd(cEUnit.getHL7Identifier());
                    }
                    obsValueNumericDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                    if (observationContainer.getTheObsValueNumericDtoCollection() == null) {
                        observationContainer.setTheObsValueNumericDtoCollection(new ArrayList<>());
                    }
                    observationContainer.getTheObsValueNumericDtoCollection().add(obsValueNumericDto);

                }
                else if (type.equals(EdxELRConstant.ELR_STRING_CD) || type.equals(EdxELRConstant.ELR_TEXT_CD)
                        || type.equals(EdxELRConstant.ELR_TEXT_DT) || type.equals(EdxELRConstant.ELR_TEXT_TS))
                {
                    ObsValueTxtDto obsValueTxtDto = new ObsValueTxtDto();
                    StringTokenizer st = new StringTokenizer(text, "^");
                    int i;
                    if (observationContainer.getTheObsValueTxtDtoCollection() == null)
                        observationContainer.setTheObsValueTxtDtoCollection(new ArrayList<>());

                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        i = observationContainer.getTheObsValueTxtDtoCollection().size() + 1;
                        obsValueTxtDto.setValueTxt(token);
                        obsValueTxtDto.setObsValueTxtSeq(i++);
                        obsValueTxtDto.setTxtTypeCd(EdxELRConstant.ELR_OFFICE_CD);
                        obsValueTxtDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                        obsValueTxtDto.setItNew(true);
                        obsValueTxtDto.setItDirty(false);

                    }
                    observationContainer.getTheObsValueTxtDtoCollection().add(obsValueTxtDto);

                }
                else
                {
                    edxLabInformationDto.setUnexpectedResultType(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.UNEXPECTED_RESULT_TYPE);
                }
            }
        } catch (Exception e) {
            logger.error("ObservationResultRequest.formatValue Exception thrown while observation value. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.formatValue for text:\""+text+"\" and for type:\""+ type+"\"."+e.getMessage());
        }

    }

    @SuppressWarnings("java:S3776")
    protected ObservationContainer getObsReqNotes(List<HL7NTEType> noteArray, ObservationContainer observationContainer) throws DataProcessingException {
        try {
            for (HL7NTEType notes : noteArray) {
                if (notes.getHL7Comment() != null && notes.getHL7Comment().size() > 0) {
                    for (int j = 0; j < notes.getHL7Comment().size(); j++) {
                        String note = notes.getHL7Comment().get(j);
                        ObsValueTxtDto obsValueTxtDto = new ObsValueTxtDto();
                        obsValueTxtDto.setItNew(true);
                        obsValueTxtDto.setItDirty(false);
                        obsValueTxtDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                        obsValueTxtDto.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);

                        obsValueTxtDto.setValueTxt(note);
                        if (observationContainer.getTheObsValueTxtDtoCollection() == null)
                        {
                            observationContainer.setTheObsValueTxtDtoCollection(new ArrayList<>());
                        }
                        int seq = observationContainer.getTheObsValueTxtDtoCollection().size();
                        obsValueTxtDto.setObsValueTxtSeq(++seq);
                        observationContainer.getTheObsValueTxtDtoCollection().add(obsValueTxtDto);
                    }
                } else {
                    ObsValueTxtDto obsValueTxtDto = new ObsValueTxtDto();
                    obsValueTxtDto.setItNew(true);
                    obsValueTxtDto.setItDirty(false);
                    obsValueTxtDto.setValueTxt("\r");
                    obsValueTxtDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
                    obsValueTxtDto.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);

                    if (observationContainer.getTheObsValueTxtDtoCollection() == null)
                        observationContainer.setTheObsValueTxtDtoCollection(new ArrayList<>());
                    int seq = observationContainer.getTheObsValueTxtDtoCollection().size();
                    obsValueTxtDto.setObsValueTxtSeq(++seq);
                    observationContainer.getTheObsValueTxtDtoCollection().add(obsValueTxtDto);

                }

            }
        } catch (Exception e) {
            logger.error("ObservationResultRequest.getObsReqNotes Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObsReqNotes:"+ e.getMessage());

        }
        return observationContainer;


    }

    protected Collection<ActIdDto> setEquipments(List<HL7EIType> equipmentIdType, ObservationDto observationDto, Collection<ActIdDto> actIdDtoColl) {
        int seq = 3;
        for (HL7EIType equipmentId : equipmentIdType) {
            ActIdDto act3IdDT = new ActIdDto();
            act3IdDT.setActUid(observationDto.getObservationUid());
            act3IdDT.setActIdSeq(seq);
            act3IdDT.setRootExtensionTxt(equipmentId.getHL7EntityIdentifier());
            act3IdDT.setAssigningAuthorityCd(equipmentId.getHL7UniversalID());
            act3IdDT.setAssigningAuthorityDescTxt(equipmentId.getHL7UniversalIDType());
            act3IdDT.setTypeCd(EdxELRConstant.ELR_EQUIPMENT_INSTANCE_CD);
            act3IdDT.setTypeDescTxt(EdxELRConstant.ELR_EQUIPMENT_INSTANCE_DESC);
            act3IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDtoColl.add(act3IdDT);
            seq = seq + 1;
        }

        return actIdDtoColl;

    }

    protected ObservationContainer processingAbnormalFlag(List<HL7CWEType> abnormalFlag, ObservationDto observationDto,
                                          ObservationContainer observationContainer) throws DataProcessingException {
        if(abnormalFlag !=null && !abnormalFlag.isEmpty())
        {
            ObservationInterpDto observationIntrepDT = new ObservationInterpDto();
            observationIntrepDT.setObservationUid(observationDto.getObservationUid());
            observationIntrepDT.setInterpretationCd(abnormalFlag.get(0).getHL7Identifier());

            String str= checkingValueService.getCodeDescTxtForCd("OBS_INTRP",observationIntrepDT.getInterpretationCd());
            if(str==null || str.trim().length()==0) {
                observationIntrepDT.setInterpretationDescTxt(abnormalFlag.get(0).getHL7Text());
            }
            else {
                observationIntrepDT.setInterpretationDescTxt(str);
            }
            observationIntrepDT.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
            observationContainer.setTheObservationInterpDtoCollection(new ArrayList<>());
            observationContainer.getTheObservationInterpDtoCollection().add(observationIntrepDT);
        }
        return observationContainer;
    }
    @SuppressWarnings("java:S3776")
    protected ObservationContainer processingReferringRange(HL7OBXType hl7OBXType, ObservationContainer observationContainer) {
        if(hl7OBXType.getReferencesRange()!=null){
            String range =hl7OBXType.getReferencesRange();
            ObsValueNumericDto obsValueNumericDto;
            if(observationContainer.getTheObsValueNumericDtoCollection()!=null)
            {
                var arrlist = new ArrayList<>(observationContainer.getTheObsValueNumericDtoCollection());
                obsValueNumericDto = arrlist.get(0);
            }
            else
            {
                obsValueNumericDto = new ObsValueNumericDto();
                obsValueNumericDto.setItNew(true);
                obsValueNumericDto.setItDirty(false);
                obsValueNumericDto.setObsValueNumericSeq(1);
                obsValueNumericDto.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
            }

            if(range.contains("^"))
            {
                int i=0;
                if(range.indexOf("^")==0){
                    i=1;
                }
                StringTokenizer st = new StringTokenizer(range, "^");
                while (st.hasMoreTokens()) {
                    i++;
                    String token = st.nextToken();
                    if(i==1)
                        obsValueNumericDto.setLowRange(token);
                    else if(i==2)
                        obsValueNumericDto.setHighRange(token);
                }
            }
            else
            {
                obsValueNumericDto.setLowRange(range);
                if(observationContainer.getTheObsValueNumericDtoCollection()==null){
                    observationContainer.setTheObsValueNumericDtoCollection(new ArrayList<>());
                    observationContainer.getTheObsValueNumericDtoCollection().add(obsValueNumericDto);
                }
            }
        }
        return observationContainer;
    }
    @SuppressWarnings("java:S3776")
    protected ObservationContainer processingObservationMethod(List<HL7CEType> methodArray , EdxLabInformationDto edxLabInformationDto, ObservationContainer observationContainer) throws DataProcessingException {
        StringBuilder methodCd = null;
        StringBuilder methodDescTxt = null;
        final String delimiter = "**";
        for (HL7CEType method : methodArray) {
            if (method.getHL7Identifier() != null) {
                if (methodCd == null)
                {
                    methodCd = new StringBuilder(method.getHL7Identifier() + delimiter);
                }
                else
                {
                    methodCd.append(method.getHL7Identifier()).append(delimiter);
                }

                String str = checkingValueService.getCodeDescTxtForCd("OBS_METH", method.getHL7Identifier());
                if (str == null || str.trim().equals("")) {

                    logger.warn(
                            "ObservationResultRequest.getObservationResult warning: Method code could not be teranslated. Please check!!!");
                    edxLabInformationDto.setObsMethodTranslated(false);
                }
                if (method.getHL7Text() != null) {
                    if (methodDescTxt == null)
                    {
                        methodDescTxt = new StringBuilder(method.getHL7Text() + delimiter);
                    }
                    else
                    {
                        methodDescTxt.append(method.getHL7Text()).append(delimiter);
                    }
                }
            }
        }

        if (methodCd != null && methodCd.lastIndexOf(delimiter) > 0)
        {
            methodCd = new StringBuilder(methodCd.substring(0, methodCd.lastIndexOf(delimiter)));
        }
        if (methodDescTxt != null && methodDescTxt.lastIndexOf(delimiter) > 0)
        {
            methodDescTxt = new StringBuilder(methodDescTxt.substring(0, methodDescTxt.lastIndexOf(delimiter)));
        }
        observationContainer.getTheObservationDto().setMethodCd(methodCd != null ? methodCd.toString() : "");
        observationContainer.getTheObservationDto().setMethodDescTxt(methodDescTxt != null ? methodDescTxt.toString() : "");

        return observationContainer;
    }

}
