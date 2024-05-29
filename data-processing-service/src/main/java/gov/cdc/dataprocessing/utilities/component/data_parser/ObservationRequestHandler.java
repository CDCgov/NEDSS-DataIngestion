package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationReasonDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.CommonLabUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.HL7SpecimenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ObservationRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObservationRequestHandler.class);

    private final ICatchingValueService checkingValueService;
    private final CommonLabUtil commonLabUtil;
    private final NBSObjectConverter nbsObjectConverter;
    private final HL7SpecimenUtil hl7SpecimenUtil;
    private final HL7PatientHandler hl7PatientHandler;

    public ObservationRequestHandler(ICatchingValueService checkingValueService,
                                     CommonLabUtil commonLabUtil,
                                     NBSObjectConverter nbsObjectConverter,
                                     HL7SpecimenUtil hl7SpecimenUtil,
                                     HL7PatientHandler hl7PatientHandler) {
        this.checkingValueService = checkingValueService;
        this.commonLabUtil = commonLabUtil;
        this.nbsObjectConverter = nbsObjectConverter;
        this.hl7SpecimenUtil = hl7SpecimenUtil;
        this.hl7PatientHandler = hl7PatientHandler;
    }

    /**
     * Description: This method parsing OBR and SPM into Lab result Object.
     * OBR & SPM.
     * */
    public LabResultProxyContainer getObservationRequest(HL7OBRType hl7OBRType, HL7PatientResultSPMType hl7PatientResultSPMType,
                                                         LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            ObservationContainer observationContainer = new ObservationContainer();
            ObservationDto observationDto = new ObservationDto();
            observationDto.setObsDomainCd(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
            observationDto.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);

            if(hl7OBRType.getResultStatus()!=null){
                String toCode = checkingValueService.findToCode("ELR_LCA_STATUS", hl7OBRType.getResultStatus(), "ACT_OBJ_ST");
                if (toCode != null && !toCode.equals("") && !toCode.equals(" ")){
                    observationDto.setStatusCd(toCode.trim());

                }
                else{
                    edxLabInformationDto.setObsStatusTranslated(false);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.TRANSLATE_OBS_STATUS);
                }
            }
            else{
                edxLabInformationDto.setObsStatusTranslated(false);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.TRANSLATE_OBS_STATUS);
            }
            //observationDto.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD);
            observationDto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);

            if(hl7OBRType.getSetIDOBR()!=null && hl7OBRType.getSetIDOBR().getHL7SequenceID()!=null
                    && hl7OBRType.getSetIDOBR().getHL7SequenceID().equalsIgnoreCase("1")) {
                observationDto.setObservationUid(edxLabInformationDto.getRootObserbationUid());
            }
            else if(!hl7OBRType.getSetIDOBR().getHL7SequenceID().equalsIgnoreCase("1")){
                observationDto.setObservationUid((long)(edxLabInformationDto.getNextUid()));
            }else{
                observationDto.setObservationUid(edxLabInformationDto.getRootObserbationUid());
            }
            observationDto.setItNew(true);
            observationDto.setItDirty(false);
            observationContainer.setItNew(true);
            observationContainer.setItDirty(false);
            observationDto.setObsDomainCdSt1(EdxELRConstant.ELR_ORDER_CD);

            if(hl7OBRType.getDangerCode()!=null) {
                edxLabInformationDto.setDangerCode(hl7OBRType.getDangerCode().getHL7Identifier());
            }

            OrganizationContainer sendingOrgVO = null;
            EntityIdDto sendingFacilityId = null;
            Collection<OrganizationContainer> orgCollection = labResultProxyContainer.getTheOrganizationContainerCollection();
            for (OrganizationContainer organizationContainer : orgCollection) {
                if (organizationContainer.getRole() != null && organizationContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD)) {
                    sendingOrgVO = organizationContainer;
                }
                Collection<EntityIdDto> entityCollection = sendingOrgVO.getTheEntityIdDtoCollection();
                for (EntityIdDto entityIdDto : entityCollection) {
                    if (entityIdDto.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_FACILITY_CD)) {
                        sendingFacilityId = entityIdDto;
                    }
                }
            }


            Collection<ActIdDto> actIdDtoColl =  new ArrayList<>();
            ActIdDto actIdDto = new ActIdDto();
            actIdDto.setActIdSeq(1);
            actIdDto.setActUid(edxLabInformationDto.getRootObserbationUid());
            actIdDto.setRootExtensionTxt(edxLabInformationDto.getMessageControlID());
            actIdDto.setAssigningAuthorityCd(sendingFacilityId.getAssigningAuthorityCd());
            actIdDto.setAssigningAuthorityDescTxt(sendingFacilityId.getAssigningAuthorityDescTxt());
            actIdDto.setTypeCd(EdxELRConstant.ELR_MESSAGE_CTRL_CD);
            actIdDto.setTypeDescTxt(EdxELRConstant.ELR_MESSAGE_CTRL_DESC);
            actIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDtoColl.add(actIdDto);

            HL7EIType fillerType =hl7OBRType.getFillerOrderNumber();
            if(hl7OBRType.getParent()==null ){
                if(fillerType == null || fillerType.getHL7EntityIdentifier() == null){
                    edxLabInformationDto.setFillerNumberPresent(false);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.FILLER_FAIL);
                }
                else{
                    edxLabInformationDto.setFillerNumber(fillerType.getHL7EntityIdentifier());
                    edxLabInformationDto.setFillerNumberPresent(true);
                }
            }
            ActIdDto act2IdDT = new ActIdDto();
            act2IdDT.setActUid(edxLabInformationDto.getRootObserbationUid());
            act2IdDT.setActIdSeq(2);
            act2IdDT.setAssigningAuthorityCd(sendingFacilityId.getAssigningAuthorityCd());
            act2IdDT.setAssigningAuthorityDescTxt(sendingFacilityId.getAssigningAuthorityDescTxt());
            act2IdDT.setRootExtensionTxt(fillerType.getHL7EntityIdentifier());
            act2IdDT.setTypeCd(EdxELRConstant.ELR_FILLER_NUMBER_CD);
            act2IdDT.setTypeDescTxt(EdxELRConstant.ELR_FILLER_NUMBER_DESC);
            act2IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDtoColl.add(act2IdDT);

            observationContainer.setTheActIdDtoCollection(actIdDtoColl);
            if(hl7OBRType.getUniversalServiceIdentifier()==null){
                edxLabInformationDto.setUniversalServiceIdMissing(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.UNIVSRVCID);
            }
            else{

                if(hl7OBRType.getUniversalServiceIdentifier()!= null
                        && hl7OBRType.getUniversalServiceIdentifier().getHL7NameofCodingSystem()!=null
                        && hl7OBRType.getUniversalServiceIdentifier().getHL7NameofCodingSystem().equals(EdxELRConstant.ELR_LOINC_CD)){
                    observationDto.setCdSystemCd(EdxELRConstant.ELR_LOINC_CD);
                    observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_LOINC_DESC);
                }

                if(hl7OBRType.getUniversalServiceIdentifier().getHL7Identifier()!=null) {
                    observationDto.setCd(hl7OBRType.getUniversalServiceIdentifier().getHL7Identifier());
                }

                if(hl7OBRType.getUniversalServiceIdentifier().getHL7Text()!=null) {
                    observationDto.setCdDescTxt(hl7OBRType.getUniversalServiceIdentifier().getHL7Text());
                }


                if(observationDto.getCd()!=null) {
                    observationDto.setAltCd(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateIdentifier());
                }
                else {
                    observationDto.setCd(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateIdentifier());
                }

                if(observationDto.getCdDescTxt()!=null) {
                    observationDto.setAltCdDescTxt(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateText());
                }
                else {
                    observationDto.setCdDescTxt(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateText());
                }

                if(observationDto.getCdSystemCd()!=null
                        && observationDto.getCdSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_LOINC_CD)
                        && (observationDto.getAltCd()!=null) ){
                    observationDto.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDto.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }else if((observationDto.getCd()!=null || observationDto.getCdDescTxt()!=null) && observationDto.getCdSystemCd()==null){
                    observationDto.setCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }
                if(
                    (
                        observationDto.getCd()==null
                        || observationDto.getCd().trim().equalsIgnoreCase("")
                    )
                    && (observationDto.getAltCd()==null
                        || observationDto.getAltCd().trim().equalsIgnoreCase("")
                    )
                )
                {
                    edxLabInformationDto.setOrderTestNameMissing(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                    String xmlElementName = commonLabUtil.getXMLElementNameForOBR(hl7OBRType)+".UniversalServiceIdentifier";
                    throw new DataProcessingException(EdxELRConstant.NO_ORDTEST_NAME+" XMLElementName: "+xmlElementName);
                }

            }
            observationDto.setPriorityCd(hl7OBRType.getPriorityOBR());
            observationDto.setActivityFromTime(edxLabInformationDto.getOrderEffectiveDate());
            observationDto.setActivityToTime(nbsObjectConverter.processHL7TSType(hl7OBRType.getResultsRptStatusChngDateTime(), EdxELRConstant.DATE_VALIDATION_OBR_RESULTS_RPT_STATUS_CHNG_TO_TIME_MSG));
            observationDto.setEffectiveFromTime(nbsObjectConverter.processHL7TSType(hl7OBRType.getObservationDateTime(),EdxELRConstant.DATE_VALIDATION_OBR_OBSERVATION_DATE_MSG));
            observationDto.setEffectiveToTime(nbsObjectConverter.processHL7TSType(hl7OBRType.getObservationEndDateTime(),EdxELRConstant.DATE_VALIDATION_OBR_OBSERVATION_END_DATE_MSG));

            List<HL7CWEType> reasonArray =hl7OBRType.getReasonforStudy();
            Collection<ObservationReasonDto> obsReasonDTColl = new ArrayList<>();
            for (HL7CWEType hl7CWEType : reasonArray) {
                ObservationReasonDto obsReasonDT = new ObservationReasonDto();
                if (hl7CWEType.getHL7Identifier() != null) {
                    obsReasonDT.setReasonCd(hl7CWEType.getHL7Identifier());
                    obsReasonDT.setReasonDescTxt(hl7CWEType.getHL7Text());
                } else if (hl7CWEType.getHL7AlternateIdentifier() != null) {
                    obsReasonDT.setReasonCd(hl7CWEType.getHL7AlternateIdentifier());
                    obsReasonDT.setReasonDescTxt(hl7CWEType.getHL7AlternateText());
                }

                if ((hl7CWEType.getHL7Identifier() == null || hl7CWEType.getHL7Identifier().trim().equalsIgnoreCase("")) &&
                        (hl7CWEType.getHL7AlternateIdentifier() == null || hl7CWEType.getHL7AlternateIdentifier().trim().equalsIgnoreCase(""))) {
                    edxLabInformationDto.setReasonforStudyCdMissing(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                    String xmlElementName = commonLabUtil.getXMLElementNameForOBR(hl7OBRType) + ".ReasonforStudy";
                    throw new DataProcessingException(EdxELRConstant.NO_REASON_FOR_STUDY + " XMLElementName: " + xmlElementName);
                }

                obsReasonDTColl.add(obsReasonDT);
            }
            if(edxLabInformationDto.getLastChgTime()==null) {
                observationDto.setRptToStateTime(edxLabInformationDto.getAddTime());
            }
            else {
                observationDto.setRptToStateTime(edxLabInformationDto.getLastChgTime());
            }
            observationContainer.setTheObservationDto(observationDto);
            observationContainer.setTheObservationReasonDtoCollection(obsReasonDTColl);
            labResultProxyContainer.getTheObservationContainerCollection().add(observationContainer);
            if(edxLabInformationDto.getRootObservationContainer()==null) {
                edxLabInformationDto.setRootObservationContainer(observationContainer);
            }


            if(hl7OBRType.getParent()==null){
                processRootOBR(hl7OBRType, observationDto, labResultProxyContainer, hl7PatientResultSPMType, edxLabInformationDto);
            }

            if(hl7OBRType.getParent()!=null){
                processSusOBR(hl7OBRType, observationDto, labResultProxyContainer, edxLabInformationDto);
            }
            if(hl7OBRType.getParentResult()==null){
                edxLabInformationDto.setParentObservationUid(0L);
                edxLabInformationDto.setParentObsInd(false);
            }

            if(hl7OBRType.getResultCopiesTo()!=null){
                for(int i=0; i<hl7OBRType.getResultCopiesTo().size(); i++){
                    HL7XCNType providerType =hl7OBRType.getResultCopiesTo().get(i);
                    edxLabInformationDto.setRole(EdxELRConstant.ELR_COPY_TO_CD);
                    PersonContainer personContainer = getProviderVO(providerType,null, labResultProxyContainer, edxLabInformationDto);
                    labResultProxyContainer.getThePersonContainerCollection().add(personContainer);

                }

            }

        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getObservationRequest:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getObservationRequest:"+ e.getMessage());
        }



        return labResultProxyContainer;

    }

    private void processSusOBR(HL7OBRType hl7OBRType, ObservationDto observationDto,
                                      LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            EdxLabIdentiferDto edxLabIdentiferDT;
            if(hl7OBRType.getParentResult()== null ||
                    hl7OBRType.getParentResult().getParentObservationIdentifier()==null||
                    (hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7Identifier()==null &&
                            hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier()==null))
            {
                edxLabInformationDto.setReflexOrderedTestCdMissing(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            }
            Long parentObservation = null;
            boolean fillerMatch = false;
            if(edxLabInformationDto.getEdxLabIdentiferDTColl()!=null){
                for (EdxLabIdentiferDto labIdentiferDT : edxLabInformationDto.getEdxLabIdentiferDTColl()) {
                    edxLabIdentiferDT = labIdentiferDT;
                    if (edxLabIdentiferDT.getIdentifer() != null
                            && (edxLabIdentiferDT.getIdentifer().equals(hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7Identifier()) || edxLabIdentiferDT.getIdentifer().equals(hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier()))
                            && edxLabIdentiferDT.getSubMapID() != null
                            && edxLabIdentiferDT.getSubMapID().equals(hl7OBRType.getParentResult().getParentObservationSubidentifier())
                            && edxLabIdentiferDT.getObservationValues() != null
                            && hl7OBRType.getParentResult().getParentObservationValueDescriptor() != null
                            && edxLabIdentiferDT.getObservationValues().indexOf(hl7OBRType.getParentResult().getParentObservationValueDescriptor().getHL7String()) > 0) {
                        parentObservation = edxLabIdentiferDT.getObservationUid();
                    }
                    if (edxLabInformationDto.getFillerNumber() != null
                            && hl7OBRType.getParent().getHL7FillerAssignedIdentifier() != null
                            && edxLabInformationDto.getFillerNumber().equals(hl7OBRType.getParent().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier())) {
                        fillerMatch = true;
                    }
                }
                if(parentObservation == null || !fillerMatch){
                    edxLabInformationDto.setChildSuscWithoutParentResult(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.CHILD_SUSC_WITH_NO_PARENT_RESULT);
                }
            }
            if(edxLabInformationDto.getEdxSusLabDTMap()== null || edxLabInformationDto.getEdxSusLabDTMap().get(parentObservation)!=null){

                ObservationContainer obsVO= new ObservationContainer();
                ObservationDto obsDT= new ObservationDto();
                obsDT.setCd(EdxELRConstant.ELR_LAB222_CD);
                obsDT.setCdDescTxt(EdxELRConstant.ELR_NO_INFO_DESC);
                obsDT.setObsDomainCdSt1(EdxELRConstant.ELR_REF_ORDER_CD);
                obsDT.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
                obsDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
                obsDT.setStatusTime(edxLabInformationDto.getAddTime());
                obsDT.setObservationUid((long)(edxLabInformationDto.getNextUid()));
                obsDT.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD);
                obsDT.setItNew(true);
                obsDT.setItDirty(false);
                obsVO.setItNew(true);
                obsVO.setItDirty(false);
                ObsValueCodedDto obsValueCodedDto = new ObsValueCodedDto();
                obsValueCodedDto.setObservationUid(observationDto.getObservationUid());
                obsValueCodedDto.setCode(EdxELRConstant.ELR_YES_CD);

                if(obsVO.getTheObsValueCodedDtoCollection()==null) {
                    obsVO.setTheObsValueCodedDtoCollection(new ArrayList<>());
                }
                obsVO.getTheObsValueCodedDtoCollection().add(obsValueCodedDto);
                obsVO.setTheObservationDto(obsDT);
                ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
                actRelationshipDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                actRelationshipDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                actRelationshipDto.setTypeCd(EdxELRConstant.ELR_SUPPORT_CD);
                actRelationshipDto.setItNew(true);
                actRelationshipDto.setItDirty(false);
                actRelationshipDto.setTypeDescTxt(EdxELRConstant.ELR_SUPPORT_DESC);
                actRelationshipDto.setSourceActUid(obsVO.getTheObservationDto().getObservationUid());
                actRelationshipDto.setTargetActUid(edxLabInformationDto.getRootObserbationUid());
                actRelationshipDto.setTargetClassCd(EdxELRConstant.ELR_OBS);
                actRelationshipDto.setSourceClassCd(EdxELRConstant.ELR_OBS);
                actRelationshipDto.setAddTime(edxLabInformationDto.getAddTime());
                actRelationshipDto.setLastChgTime(edxLabInformationDto.getAddTime());
                actRelationshipDto.setRecordStatusTime(edxLabInformationDto.getAddTime());
                if(labResultProxyContainer.getTheActRelationshipDtoCollection()==null) {
                    labResultProxyContainer.setTheActRelationshipDtoCollection(new ArrayList<>());
                }
                labResultProxyContainer.getTheActRelationshipDtoCollection().add(actRelationshipDto);

                observationDto.setObsDomainCdSt1(EdxELRConstant.ELR_REF_RESULT_CD);
                ActRelationshipDto arDT = new ActRelationshipDto();
                arDT.setTypeCd(EdxELRConstant.ELR_REFER_CD);
                arDT.setTypeDescTxt(EdxELRConstant.ELR_REFER_DESC);
                arDT.setSourceActUid(obsVO.getTheObservationDto().getObservationUid());
                arDT.setTargetActUid(parentObservation);
                arDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                arDT.setRecordStatusTime(edxLabInformationDto.getAddTime());
                arDT.setTargetClassCd(EdxELRConstant.ELR_OBS);
                arDT.setSourceClassCd(EdxELRConstant.ELR_OBS);
                arDT.setItNew(true);
                arDT.setItDirty(false);
                labResultProxyContainer.getTheActRelationshipDtoCollection().add(arDT);
                if(hl7OBRType.getParent()!=null){
                    labResultProxyContainer.getTheObservationContainerCollection().add(obsVO);
                }
                edxLabInformationDto.getEdxSusLabDTMap().put(parentObservation, obsVO.getTheObservationDto().getObservationUid());
            }

            if(parentObservation!=null){
                Long uid = (Long) edxLabInformationDto.getEdxSusLabDTMap().get(parentObservation);
                if(uid!=null){
                    edxLabInformationDto.setParentObservationUid(uid);
                    edxLabInformationDto.setParentObsInd(true);
                }
            }
        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.processSusOBR:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.processSusOBR:"+ e);
        }

    }

    /**
     * Processing
     *  PROVIDER info
     *  other obs info
     * */
    private void processRootOBR(HL7OBRType hl7OBRType, ObservationDto observationDto,
                                       LabResultProxyContainer labResultProxyContainer, HL7PatientResultSPMType hl7PatientResultSPMType,
                                       EdxLabInformationDto edxLabInformationDto) throws DataProcessingException{


        try {

            PersonContainer collectorVO = null;
            List<HL7XCNType> collectorArray = hl7OBRType.getCollectorIdentifier();
            if(collectorArray!=null && collectorArray.size() > 1) {
                edxLabInformationDto.setMultipleCollector(true);
            }
            if(collectorArray!=null && !collectorArray.isEmpty()){
                HL7XCNType collector= collectorArray.get(0);
                collectorVO = getCollectorVO(collector, labResultProxyContainer, edxLabInformationDto);
                labResultProxyContainer.getThePersonContainerCollection().add(collectorVO);
            }
            if(hl7OBRType.getRelevantClinicalInformation()!=null) {
                observationDto.setTxt(hl7OBRType.getRelevantClinicalInformation());
            }
            if(hl7PatientResultSPMType!=null){
                logger.debug("ObservationRequest.getObservationRequest specimen is being processes for 2.5.1 message type");
                hl7SpecimenUtil.process251Specimen( hl7PatientResultSPMType, labResultProxyContainer, observationDto,  collectorVO, edxLabInformationDto);
            }
            List<HL7XCNType> orderingProviderArray = hl7OBRType.getOrderingProvider();
            if(orderingProviderArray!=null && orderingProviderArray.size()  >1){
                edxLabInformationDto.setMultipleOrderingProvider(true);
            }
            PersonContainer orderingProviderVO;
            if(orderingProviderArray!=null && !orderingProviderArray.isEmpty()){
                HL7XCNType orderingProvider=orderingProviderArray.get(0);
                Collection<EntityLocatorParticipationDto> entitylocatorColl =null;

                PersonContainer providerVO;
                if(edxLabInformationDto.getOrderingProviderVO()!=null){
                    providerVO = edxLabInformationDto.getOrderingProviderVO();
                    entitylocatorColl=providerVO.getTheEntityLocatorParticipationDtoCollection();
                    if(labResultProxyContainer.getThePersonContainerCollection().contains(providerVO)) {
                        labResultProxyContainer.getThePersonContainerCollection().remove(providerVO);
                    }
                }
                edxLabInformationDto.setRole(EdxELRConstant.ELR_OP_CD);
                orderingProviderVO= getProviderVO(orderingProvider,entitylocatorColl, labResultProxyContainer, edxLabInformationDto);
                edxLabInformationDto.setOrderingProvider(true);

                if(hl7OBRType.getOrderCallbackPhoneNumber()!=null && orderingProviderVO!=null && !hl7OBRType.getOrderCallbackPhoneNumber().isEmpty()){
                    HL7XTNType orderingProvPhone  =hl7OBRType.getOrderCallbackPhoneNumber().get(0);
                    EntityLocatorParticipationDto elpt = nbsObjectConverter.personTelePhoneType(orderingProvPhone, EdxELRConstant.ELR_PROVIDER_CD, orderingProviderVO);
                    elpt.setUseCd(EdxELRConstant.ELR_WORKPLACE_CD);
                }
                if(labResultProxyContainer.getThePersonContainerCollection()==null) {
                    labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
                }
                if(orderingProviderVO!=null) {
                    labResultProxyContainer.getThePersonContainerCollection().add(orderingProviderVO);
                }

            }else{
                if(edxLabInformationDto.getOrderingProviderVO()!=null){
                    edxLabInformationDto.setOrderingProvider(false);
                    PersonContainer providerVO = edxLabInformationDto.getOrderingProviderVO();
                    if(labResultProxyContainer.getThePersonContainerCollection().contains(providerVO)) {
                        labResultProxyContainer.getThePersonContainerCollection().remove(providerVO);
                    }
                }
            }
            if(edxLabInformationDto.isMissingOrderingProvider() && edxLabInformationDto.isMissingOrderingFacility()){
                edxLabInformationDto.setMissingOrderingProviderandFacility(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException("HL7ORCProcessorget.getORCProcessing: Both Ordering Provider and Ordering facility are null. Please check!!!");
            }

            HL7NDLType  princResultInterpretor = hl7OBRType.getPrincipalResultInterpreter();
            edxLabInformationDto.setMultiplePrincipalInterpreter(false);
            edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_PROVIDER_CD);
            getOtherProviderVO( princResultInterpretor, labResultProxyContainer, edxLabInformationDto);



            if( hl7OBRType.getAssistantResultInterpreter() !=null){
                for(int i = 0; i<hl7OBRType.getAssistantResultInterpreter().size(); i++){
                    HL7NDLType  assPrincResultInterpretor =hl7OBRType.getAssistantResultInterpreter().get(i);
                    edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                    getOtherProviderVO( assPrincResultInterpretor, labResultProxyContainer, edxLabInformationDto);
                }
            }


            if( hl7OBRType.getTechnician()!=null){
                for(int i = 0; i<hl7OBRType.getTechnician().size(); i++){
                    HL7NDLType  technician =hl7OBRType.getTechnician().get(i);
                    edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_PERFORMER_CD);
                    getOtherProviderVO( technician, labResultProxyContainer, edxLabInformationDto);
                }
            }
            if( hl7OBRType.getTranscriptionist()!=null){
                for(int i = 0; i<hl7OBRType.getTranscriptionist().size(); i++){
                    HL7NDLType  technician =hl7OBRType.getTranscriptionist().get(i);
                    edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_ENTERER_CD);
                    getOtherProviderVO( technician, labResultProxyContainer, edxLabInformationDto);
                }
            }
        } catch (Exception e) {
            logger.error(" Exception thrown at ObservationRequest.processRootOBR:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.processRootOBR:"+ e.getMessage() +e);
        }

    }

    private PersonContainer getCollectorVO(HL7XCNType collector, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        PersonContainer personContainer;
        try {
            edxLabInformationDto.setRole(EdxELRConstant.ELR_PROVIDER_CD);
            EntityIdDto entityIdDto = new EntityIdDto();

            entityIdDto.setEntityUid((long) edxLabInformationDto.getNextUid());
            entityIdDto.setAddTime(edxLabInformationDto.getAddTime());
            entityIdDto.setEntityIdSeq(1);
            entityIdDto.setRootExtensionTxt(collector.getHL7IDNumber());
            entityIdDto.setTypeCd(EdxELRConstant.ELR_EMP_IDENT_CD);
            entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_EMP_IDENT_DESC);
            entityIdDto.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            entityIdDto.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            entityIdDto.setAssigningAuthorityIdType(edxLabInformationDto.getUniversalIdType());
            entityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDto.setAsOfDate(edxLabInformationDto.getAddTime());
            entityIdDto.setItNew(true);
            entityIdDto.setItDirty(false);
            edxLabInformationDto.setRole(EdxELRConstant.ELR_PROVIDER_CD);

            personContainer = hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);
            
            personContainer.getTheEntityIdDtoCollection().add(entityIdDto);

            PersonNameDto personNameDto = new PersonNameDto();
            if(collector.getHL7FamilyName()!=null && collector.getHL7FamilyName().getHL7Surname()!=null) {
                personNameDto.setLastNm(collector.getHL7FamilyName().getHL7Surname());
            }
            personNameDto.setFirstNm(collector.getHL7GivenName());
            personNameDto.setMiddleNm(collector.getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
            personNameDto.setNmPrefix(collector.getHL7Prefix());
            personNameDto.setNmSuffix(collector.getHL7Suffix());
            personNameDto.setNmDegree(collector.getHL7Degree());
            personNameDto.setPersonNameSeq(1);
            personNameDto.setNmUseCd(collector.getHL7NameTypeCode());
            //Defect 5542 Transcriptionist causing issue
            if (personNameDto.getNmUseCd() == null) {
                personNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            }
            personNameDto.setAddTime(edxLabInformationDto.getAddTime());
            personNameDto.setLastChgTime(edxLabInformationDto.getAddTime());
            personNameDto.setAddUserId(edxLabInformationDto.getUserId());
            personNameDto.setLastChgUserId(edxLabInformationDto.getUserId());
            personContainer.getThePersonNameDtoCollection().add(personNameDto);

            RoleDto roleDto = new RoleDto();
            roleDto.setSubjectEntityUid(personContainer.getThePersonDto().getPersonUid());
            roleDto.setCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
            roleDto.setCdDescTxt(EdxELRConstant.ELR_SPECIMEN_PROCURER_DESC);
            roleDto.setSubjectClassCd(EdxELRConstant.ELR_PROV_CD);
            roleDto.setRoleSeq(1L);
            roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
            roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setScopingRoleSeq(1);
            roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);
            
        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getCollectorVO:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getCollectorVO:"+ e);
        }

        return personContainer;
    }

    private PersonContainer getOtherProviderVO(HL7NDLType providerType, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        PersonContainer personContainer;
        try {
            if(providerType==null) {
                return null;
            }
            personContainer = hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

            if(providerType.getHL7Name()!=null && providerType.getHL7Name().getHL7IDNumber()!=null){
                Collection<EntityIdDto> entityColl = new ArrayList<>();
                EntityIdDto entityIdDto = new EntityIdDto();
                entityIdDto.setEntityUid((long)(edxLabInformationDto.getNextUid()));
                entityIdDto.setEntityIdSeq(1);
                entityIdDto.setAddTime(edxLabInformationDto.getAddTime());
                entityIdDto.setRootExtensionTxt(providerType.getHL7Name().getHL7IDNumber());
                entityIdDto.setTypeCd(EdxELRConstant.ELR_EMP_IDENT_CD);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_EMP_IDENT_DESC);
                entityIdDto.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
                entityIdDto.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
                entityIdDto.setAssigningAuthorityIdType(edxLabInformationDto.getUniversalIdType());
                entityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                entityIdDto.setAsOfDate(edxLabInformationDto.getAddTime());
                entityIdDto.setItNew(true);
                entityIdDto.setItDirty(false);
                entityColl.add(entityIdDto);
                if(entityIdDto.getEntityUid()!=null) {
                    personContainer.getTheEntityIdDtoCollection().add(entityIdDto);
                }
            }

            ParticipationDto participationDto = new ParticipationDto();
            participationDto.setSubjectEntityUid(personContainer.getThePersonDto().getPersonUid());

            if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_PROVIDER_CD)){
                participationDto.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                participationDto.setTypeCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_VERIFIER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_VERIFIER_CD)){
                participationDto.setCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDto.setTypeCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_VERIFIER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_PERFORMER_CD)){
                participationDto.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                participationDto.setTypeCd(EdxELRConstant.ELR_LAB_PERFORMER_CD);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_PERFORMER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_ENTERER_CD)){
                participationDto.setCd(EdxELRConstant.ELR_LAB_ENTERER_CD);
                participationDto.setTypeCd(EdxELRConstant.ELR_LAB_ENTERER_CD);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_ENTERER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_ASSISTANT_CD)){
                participationDto.setCd(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                participationDto.setTypeCd(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_ASSISTANT_DESC);
            }
            nbsObjectConverter.defaultParticipationDT(participationDto, edxLabInformationDto);


            participationDto.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
            participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDto.setActUid(edxLabInformationDto.getRootObserbationUid());
            labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);
            edxLabInformationDto.setRole(EdxELRConstant.ELR_PROVIDER_CD);
            nbsObjectConverter.processCNNPersonName(providerType.getHL7Name(), personContainer);
            if(labResultProxyContainer.getThePersonContainerCollection()==null) {
                labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
            }
            labResultProxyContainer.getThePersonContainerCollection().add(personContainer);
        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getCollectorVO:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getCollectorVO:"+ e);
        }
        return personContainer;
    }

    private PersonContainer getProviderVO(HL7XCNType orderingProvider, Collection<EntityLocatorParticipationDto> entitylocatorColl, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        PersonContainer personContainer;

        try {
            EntityIdDto entityIdDto = new EntityIdDto();
            entityIdDto.setEntityUid((long)(edxLabInformationDto.getNextUid()));
            entityIdDto.setAddTime(edxLabInformationDto.getAddTime());
            entityIdDto.setEntityIdSeq(1);
            entityIdDto.setRootExtensionTxt(orderingProvider.getHL7IDNumber());
            entityIdDto.setTypeCd(EdxELRConstant.ELR_PROVIDER_REG_NUM_CD);
            entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_PROVIDER_REG_NUM_DESC);
            entityIdDto.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            entityIdDto.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            entityIdDto.setAssigningAuthorityIdType(edxLabInformationDto.getUniversalIdType());
            entityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDto.setAsOfDate(edxLabInformationDto.getAddTime());
            entityIdDto.setItNew(true);
            entityIdDto.setItDirty(false);
            personContainer =hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);
            if(entitylocatorColl!=null) {
                personContainer.setTheEntityLocatorParticipationDtoCollection(entitylocatorColl);
            }
            if(entityIdDto.getEntityUid()!=null) {
                personContainer.getTheEntityIdDtoCollection().add(entityIdDto);
            }

            if(edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)){
                ParticipationDto participationDto = new ParticipationDto();
                participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
                participationDto.setCd(EdxELRConstant.ELR_OP_CD);
                participationDto.setActUid(edxLabInformationDto.getRootObserbationUid());
                participationDto.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
                participationDto.setTypeDescTxt(EdxELRConstant.ELR_ORDERER_DESC);
                nbsObjectConverter.defaultParticipationDT(participationDto, edxLabInformationDto);
                participationDto.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
                participationDto.setSubjectEntityUid(personContainer.getThePersonDto().getPersonUid());
                labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);
                personContainer.setRole(EdxELRConstant.ELR_OP_CD);

            }
            PersonNameDto personNameDto = new PersonNameDto();
            if(orderingProvider.getHL7FamilyName()!=null && orderingProvider.getHL7FamilyName().getHL7Surname()!=null ) {
                personNameDto.setLastNm(orderingProvider.getHL7FamilyName().getHL7Surname());
            }
            personNameDto.setFirstNm(orderingProvider.getHL7GivenName());
            personNameDto.setMiddleNm(orderingProvider.getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
            personNameDto.setNmPrefix(orderingProvider.getHL7Prefix());
            personNameDto.setNmSuffix(orderingProvider.getHL7Suffix());
            personNameDto.setNmDegree(orderingProvider.getHL7Degree());
            personNameDto.setNmUseCd(orderingProvider.getHL7NameTypeCode());

            personNameDto.setAddTime(edxLabInformationDto.getAddTime());
            personNameDto.setLastChgTime(edxLabInformationDto.getAddTime());
            personNameDto.setAddUserId(edxLabInformationDto.getUserId());
            personNameDto.setPersonNameSeq(1);
            personNameDto.setLastChgUserId(edxLabInformationDto.getUserId());
            personContainer.setThePersonNameDtoCollection(new ArrayList<>());
            personNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            personContainer.getThePersonNameDtoCollection().add(personNameDto);

        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getOrderingProviderVO:"+e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getOrderingProviderVO:"+ e);

        }

        return personContainer;
    }




}
