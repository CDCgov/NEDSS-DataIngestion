package gov.cdc.dataprocessing.utilities.component.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dt.EdxLabIdentiferDT;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import gov.cdc.dataprocessing.utilities.data_extraction.CommonLabUtil;
import gov.cdc.dataprocessing.utilities.data_extraction.HL7SpecimenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ObservationRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObservationRequestHandler.class);

    private final ICheckingValueService checkingValueService;

    public ObservationRequestHandler(ICheckingValueService checkingValueService) {
        this.checkingValueService = checkingValueService;
    }

    public LabResultProxyContainer getObservationRequest(HL7OBRType hl7OBRType, HL7PatientResultSPMType hl7PatientResultSPMType,
                                                         LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            ObservationVO observationVO = new ObservationVO();
            ObservationDT observationDT= new ObservationDT();
            observationDT.setObsDomainCd(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
            observationDT.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);

            if(hl7OBRType.getResultStatus()!=null){
                String toCode = checkingValueService.findToCode("ELR_LCA_STATUS", hl7OBRType.getResultStatus(), "ACT_OBJ_ST");
                if (toCode != null && !toCode.equals("") && !toCode.equals(" ")){
                    observationDT.setStatusCd(toCode.trim());

                }else{
                    edxLabInformationDto.setObsStatusTranslated(false);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.TRANSLATE_OBS_STATUS);
                }
            }else{
                edxLabInformationDto.setObsStatusTranslated(false);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.TRANSLATE_OBS_STATUS);
            }
            //observationDT.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD);
            observationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);

            if(hl7OBRType.getSetIDOBR()!=null && hl7OBRType.getSetIDOBR().getHL7SequenceID()!=null
                    && hl7OBRType.getSetIDOBR().getHL7SequenceID().equalsIgnoreCase("1")) {
                observationDT.setObservationUid(edxLabInformationDto.getRootObserbationUid());
            }
            else if(!hl7OBRType.getSetIDOBR().getHL7SequenceID().equalsIgnoreCase("1")){
                observationDT.setObservationUid((long)(edxLabInformationDto.getNextUid()));
            }else{
                observationDT.setObservationUid(edxLabInformationDto.getRootObserbationUid());
            }
            observationDT.setItNew(true);
            observationDT.setItDirty(false);
            observationVO.setItNew(true);
            observationVO.setItDirty(false);
            observationDT.setObsDomainCdSt1(EdxELRConstant.ELR_ORDER_CD);

            if(hl7OBRType.getDangerCode()!=null) {
                edxLabInformationDto.setDangerCode(hl7OBRType.getDangerCode().getHL7Identifier());
            }

            OrganizationVO sendingOrgVO = null;
            EntityIdDto sendingFacilityId = null;
            Collection<OrganizationVO> orgCollection = labResultProxyContainer.getTheOrganizationVOCollection();
            for (OrganizationVO organizationVO : orgCollection) {
                if (organizationVO.getRole() != null && organizationVO.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD)) {
                    sendingOrgVO = organizationVO;
                }
                Collection<EntityIdDto> entityCollection = sendingOrgVO.getTheEntityIdDtoCollection();
                for (EntityIdDto entityIdDto : entityCollection) {
                    if (entityIdDto.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_FACILITY_CD)) {
                        sendingFacilityId = entityIdDto;
                    }
                }
            }


            Collection<ActIdDT> actIdDTColl =  new ArrayList<>();
            ActIdDT actIdDT= new ActIdDT();
            actIdDT.setActIdSeq(1);
            actIdDT.setActUid(edxLabInformationDto.getRootObserbationUid());
            actIdDT.setRootExtensionTxt(edxLabInformationDto.getMessageControlID());
            actIdDT.setAssigningAuthorityCd(sendingFacilityId.getAssigningAuthorityCd());
            actIdDT.setAssigningAuthorityDescTxt(sendingFacilityId.getAssigningAuthorityDescTxt());
            actIdDT.setTypeCd(EdxELRConstant.ELR_MESSAGE_CTRL_CD);
            actIdDT.setTypeDescTxt(EdxELRConstant.ELR_MESSAGE_CTRL_DESC);
            actIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDTColl.add(actIdDT);

            HL7EIType fillerType =hl7OBRType.getFillerOrderNumber();
            if(hl7OBRType.getParent()==null ){
                if(fillerType == null || fillerType.getHL7EntityIdentifier() == null){
                    edxLabInformationDto.setFillerNumberPresent(false);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.FILLER_FAIL);
                }
                else{
                    edxLabInformationDto.setFillerNumber(fillerType.getHL7EntityIdentifier());
                }
            }
            ActIdDT act2IdDT = new ActIdDT();
            act2IdDT.setActUid(edxLabInformationDto.getRootObserbationUid());
            act2IdDT.setActIdSeq(2);
            act2IdDT.setAssigningAuthorityCd(sendingFacilityId.getAssigningAuthorityCd());
            act2IdDT.setAssigningAuthorityDescTxt(sendingFacilityId.getAssigningAuthorityDescTxt());
            act2IdDT.setRootExtensionTxt(fillerType.getHL7EntityIdentifier());
            act2IdDT.setTypeCd(EdxELRConstant.ELR_FILLER_NUMBER_CD);
            act2IdDT.setTypeDescTxt(EdxELRConstant.ELR_FILLER_NUMBER_DESC);
            act2IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDTColl.add(act2IdDT);

            observationVO.setTheActIdDTCollection(actIdDTColl);
            if(hl7OBRType.getUniversalServiceIdentifier()==null){
                edxLabInformationDto.setUniversalServiceIdMissing(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.UNIVSRVCID);
            }
            else{

                if(hl7OBRType.getUniversalServiceIdentifier()!= null
                        && hl7OBRType.getUniversalServiceIdentifier().getHL7NameofCodingSystem()!=null
                        && hl7OBRType.getUniversalServiceIdentifier().getHL7NameofCodingSystem().equals(EdxELRConstant.ELR_LOINC_CD)){
                    observationDT.setCdSystemCd(EdxELRConstant.ELR_LOINC_CD);
                    observationDT.setCdSystemDescTxt(EdxELRConstant.ELR_LOINC_DESC);
                }

                if(hl7OBRType.getUniversalServiceIdentifier().getHL7Identifier()!=null) {
                    observationDT.setCd(hl7OBRType.getUniversalServiceIdentifier().getHL7Identifier());
                }

                if(hl7OBRType.getUniversalServiceIdentifier().getHL7Text()!=null) {
                    observationDT.setCdDescTxt(hl7OBRType.getUniversalServiceIdentifier().getHL7Text());
                }


                if(observationDT.getCd()!=null) {
                    observationDT.setAltCd(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateIdentifier());
                }
                else {
                    observationDT.setCd(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateIdentifier());
                }

                if(observationDT.getCdDescTxt()!=null) {
                    observationDT.setAltCdDescTxt(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateText());
                }
                else {
                    observationDT.setCdDescTxt(hl7OBRType.getUniversalServiceIdentifier().getHL7AlternateText());
                }

                if(observationDT.getCdSystemCd()!=null
                        && observationDT.getCdSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_LOINC_CD)
                        && (observationDT.getAltCd()!=null) ){
                    observationDT.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }else if((observationDT.getCd()!=null || observationDT.getCdDescTxt()!=null) && observationDT.getCdSystemCd()==null){
                    observationDT.setCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDT.setCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }
                if(
                    (
                        observationDT.getCd()==null
                        || observationDT.getCd().trim().equalsIgnoreCase("")
                    )
                    && (observationDT.getAltCd()==null
                        || observationDT.getAltCd().trim().equalsIgnoreCase("")
                    )
                )
                {
                    edxLabInformationDto.setOrderTestNameMissing(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                    //TODO: This convert to XML and store as string
                    String xmlElementName = CommonLabUtil.getXMLElementNameForOBR(hl7OBRType)+".UniversalServiceIdentifier";
                    throw new DataProcessingException(EdxELRConstant.NO_ORDTEST_NAME+" XMLElementName: "+xmlElementName);
                }

            }
            observationDT.setPriorityCd(hl7OBRType.getPriorityOBR());
            observationDT.setActivityFromTime(edxLabInformationDto.getOrderEffectiveDate());
            observationDT.setActivityToTime(NBSObjectConverter.processHL7TSType(hl7OBRType.getResultsRptStatusChngDateTime(), EdxELRConstant.DATE_VALIDATION_OBR_RESULTS_RPT_STATUS_CHNG_TO_TIME_MSG));
            observationDT.setEffectiveFromTime(NBSObjectConverter.processHL7TSType(hl7OBRType.getObservationDateTime(),EdxELRConstant.DATE_VALIDATION_OBR_OBSERVATION_DATE_MSG));
            observationDT.setEffectiveToTime(NBSObjectConverter.processHL7TSType(hl7OBRType.getObservationEndDateTime(),EdxELRConstant.DATE_VALIDATION_OBR_OBSERVATION_END_DATE_MSG));

            List<HL7CWEType> reasonArray =hl7OBRType.getReasonforStudy();
            Collection<ObservationReasonDT> obsReasonDTColl = new ArrayList<>();
            for (HL7CWEType hl7CWEType : reasonArray) {
                ObservationReasonDT obsReasonDT = new ObservationReasonDT();
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
                    //TODO: This convert to XML and store as string
                    String xmlElementName = CommonLabUtil.getXMLElementNameForOBR(hl7OBRType) + ".ReasonforStudy";
                    throw new DataProcessingException(EdxELRConstant.NO_REASON_FOR_STUDY + " XMLElementName: " + xmlElementName);
                }

                obsReasonDTColl.add(obsReasonDT);
            }
            if(edxLabInformationDto.getLastChgTime()==null) {
                observationDT.setRptToStateTime(edxLabInformationDto.getAddTime());
            }
            else {
                observationDT.setRptToStateTime(edxLabInformationDto.getLastChgTime());
            }
            observationVO.setTheObservationDT(observationDT);
            observationVO.setTheObservationReasonDTCollection(obsReasonDTColl);
            labResultProxyContainer.getTheObservationVOCollection().add(observationVO);
            if(edxLabInformationDto.getRootObservationVO()==null) {
                edxLabInformationDto.setRootObservationVO(observationVO);
            }


            if(hl7OBRType.getParent()==null){
                processRootOBR(hl7OBRType, observationDT, labResultProxyContainer, hl7PatientResultSPMType, edxLabInformationDto);
            }

            if(hl7OBRType.getParent()!=null){
                processSusOBR(hl7OBRType, observationDT, labResultProxyContainer, edxLabInformationDto);
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

    private static void processSusOBR(HL7OBRType hl7OBRType, ObservationDT observationDT,
                                      LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            EdxLabIdentiferDT edxLabIdentiferDT;
            if(hl7OBRType.getParentResult()== null ||
                    hl7OBRType.getParentResult().getParentObservationIdentifier()==null||
                    (hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7Identifier()==null &&
                            hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier()==null)){
                edxLabInformationDto.setReflexOrderedTestCdMissing(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            }
            Long parentObservation = null;
            boolean fillerMatch = false;
            if(edxLabInformationDto.getEdxLabIdentiferDTColl()!=null){
                for (EdxLabIdentiferDT labIdentiferDT : edxLabInformationDto.getEdxLabIdentiferDTColl()) {
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

                ObservationVO obsVO= new ObservationVO();
                ObservationDT obsDT= new ObservationDT();
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
                ObsValueCodedDT obsValueCodedDT = new ObsValueCodedDT();
                obsValueCodedDT.setObservationUid(observationDT.getObservationUid());
                obsValueCodedDT.setCode(EdxELRConstant.ELR_YES_CD);

                if(obsVO.getTheObsValueCodedDTCollection()==null) {
                    obsVO.setTheObsValueCodedDTCollection(new ArrayList<>());
                }
                obsVO.getTheObsValueCodedDTCollection().add(obsValueCodedDT);
                obsVO.setTheObservationDT(obsDT);
                ActRelationshipDT actRelationshipDT = new ActRelationshipDT();
                actRelationshipDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                actRelationshipDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                actRelationshipDT.setTypeCd(EdxELRConstant.ELR_SUPPORT_CD);
                actRelationshipDT.setItNew(true);
                actRelationshipDT.setItDirty(false);
                actRelationshipDT.setTypeDescTxt(EdxELRConstant.ELR_SUPPORT_DESC);
                actRelationshipDT.setSourceActUid(obsVO.getTheObservationDT().getObservationUid());
                actRelationshipDT.setTargetActUid(edxLabInformationDto.getRootObserbationUid());
                actRelationshipDT.setTargetClassCd(EdxELRConstant.ELR_OBS);
                actRelationshipDT.setSourceClassCd(EdxELRConstant.ELR_OBS);
                actRelationshipDT.setAddTime(edxLabInformationDto.getAddTime());
                actRelationshipDT.setLastChgTime(edxLabInformationDto.getAddTime());
                actRelationshipDT.setRecordStatusTime(edxLabInformationDto.getAddTime());
                if(labResultProxyContainer.getTheActRelationshipDTCollection()==null) {
                    labResultProxyContainer.setTheActRelationshipDTCollection(new ArrayList<>());
                }
                labResultProxyContainer.getTheActRelationshipDTCollection().add(actRelationshipDT);

                observationDT.setObsDomainCdSt1(EdxELRConstant.ELR_REF_RESULT_CD);
                ActRelationshipDT arDT = new ActRelationshipDT();
                arDT.setTypeCd(EdxELRConstant.ELR_REFER_CD);
                arDT.setTypeDescTxt(EdxELRConstant.ELR_REFER_DESC);
                arDT.setSourceActUid(obsVO.getTheObservationDT().getObservationUid());
                arDT.setTargetActUid(parentObservation);
                arDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                arDT.setRecordStatusTime(edxLabInformationDto.getAddTime());
                arDT.setTargetClassCd(EdxELRConstant.ELR_OBS);
                arDT.setSourceClassCd(EdxELRConstant.ELR_OBS);
                arDT.setItNew(true);
                arDT.setItDirty(false);
                labResultProxyContainer.getTheActRelationshipDTCollection().add(arDT);
                if(hl7OBRType.getParent()!=null){
                    labResultProxyContainer.getTheObservationVOCollection().add(obsVO);
                }
                edxLabInformationDto.getEdxSusLabDTMap().put(parentObservation, obsVO.getTheObservationDT().getObservationUid());
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

    private static void processRootOBR(HL7OBRType hl7OBRType, ObservationDT observationDT,
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
                observationDT.setTxt(hl7OBRType.getRelevantClinicalInformation());
            }
            if(hl7PatientResultSPMType!=null){
                logger.debug("ObservationRequest.getObservationRequest specimen is being processes for 2.5.1 message type");
                HL7SpecimenHandler.process251Specimen( hl7PatientResultSPMType, labResultProxyContainer,  observationDT,  collectorVO, edxLabInformationDto);
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
                    EntityLocatorParticipationDto elpt = NBSObjectConverter.personTelePhoneType(orderingProvPhone, EdxELRConstant.ELR_PROVIDER_CD, orderingProviderVO);
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
                    HL7NDLType  technician =hl7OBRType.getTranscriptionist().get(1);
                    edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_ENTERER_CD);
                    getOtherProviderVO( technician, labResultProxyContainer, edxLabInformationDto);
                }
            }
        } catch (Exception e) {
            logger.error(" Exception thrown at ObservationRequest.processRootOBR:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.processRootOBR:"+ e.getMessage() +e);
        }

    }

    private static PersonContainer getCollectorVO(HL7XCNType collector, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
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

            personContainer = HL7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);
            
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

    private static PersonContainer getOtherProviderVO(HL7NDLType providerType, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        PersonContainer personContainer;
        try {
            if(providerType==null) {
                return null;
            }
            personContainer = HL7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

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

            ParticipationDT participationDT= new ParticipationDT();
            participationDT.setSubjectEntityUid(personContainer.getThePersonDto().getPersonUid());

            if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_PROVIDER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_VERIFIER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_VERIFIER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_VERIFIER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_PERFORMER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_PERFORMER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_PERFORMER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_ENTERER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_ENTERER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_ENTERER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_ENTERER_DESC);
            }
            else if(edxLabInformationDto.getRole().equals(EdxELRConstant.ELR_LAB_ASSISTANT_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_ASSISTANT_DESC);
            }
            NBSObjectConverter.defaultParticipationDT(participationDT, edxLabInformationDto);


            participationDT.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
            participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDT.setActUid(edxLabInformationDto.getRootObserbationUid());
            labResultProxyContainer.getTheParticipationDTCollection().add(participationDT);
            edxLabInformationDto.setRole(EdxELRConstant.ELR_PROVIDER_CD);
            NBSObjectConverter.processCNNPersonName(providerType.getHL7Name(), personContainer);
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

    private static PersonContainer getProviderVO(HL7XCNType orderingProvider, Collection<EntityLocatorParticipationDto> entitylocatorColl, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
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
            personContainer =HL7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);
            if(entitylocatorColl!=null) {
                personContainer.setTheEntityLocatorParticipationDtoCollection(entitylocatorColl);
            }
            if(entityIdDto.getEntityUid()!=null) {
                personContainer.getTheEntityIdDtoCollection().add(entityIdDto);
            }

            if(edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)){
                ParticipationDT participationDT = new ParticipationDT();
                participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
                participationDT.setCd(EdxELRConstant.ELR_OP_CD);
                participationDT.setActUid(edxLabInformationDto.getRootObserbationUid());
                participationDT.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_ORDERER_DESC);
                NBSObjectConverter.defaultParticipationDT(participationDT, edxLabInformationDto);
                participationDT.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
                participationDT.setSubjectEntityUid(personContainer.getThePersonDto().getPersonUid());
                labResultProxyContainer.getTheParticipationDTCollection().add(participationDT);
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
