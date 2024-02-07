package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabIdentiferDT;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import gov.cdc.dataprocessing.utilities.CommonLabUtil;
import gov.cdc.dataprocessing.utilities.HL7SpecimenHandler;
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

    public LabResultProxyVO getObservationRequest(HL7OBRType hl7OBRType, HL7PatientResultSPMType hl7PatientResultSPMType,
                                                         LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
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
                    edxLabInformationDT.setObsStatusTranslated(false);
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.TRANSLATE_OBS_STATUS);
                }
            }else{
                edxLabInformationDT.setObsStatusTranslated(false);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.TRANSLATE_OBS_STATUS);
            }
            //observationDT.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD);
            observationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);

            if(hl7OBRType.getSetIDOBR()!=null && hl7OBRType.getSetIDOBR().getHL7SequenceID()!=null
                    && hl7OBRType.getSetIDOBR().getHL7SequenceID().equalsIgnoreCase("1")) {
                observationDT.setObservationUid((long)(edxLabInformationDT.getRootObserbationUid()));
            }
            else if(!hl7OBRType.getSetIDOBR().getHL7SequenceID().equalsIgnoreCase("1")){
                observationDT.setObservationUid((long)(edxLabInformationDT.getNextUid()));
            }else{
                observationDT.setObservationUid((long)(edxLabInformationDT.getRootObserbationUid()));
            }
            observationDT.setItNew(true);
            observationDT.setItDirty(false);
            observationVO.setItNew(true);
            observationVO.setItDirty(false);
            observationDT.setObsDomainCdSt1(EdxELRConstant.ELR_ORDER_CD);

            if(hl7OBRType.getDangerCode()!=null) {
                edxLabInformationDT.setDangerCode(hl7OBRType.getDangerCode().getHL7Identifier());
            }

            OrganizationVO sendingOrgVO = null;
            EntityIdDT sendingFacilityId = null;
            Collection<OrganizationVO> orgCollection = labResultProxyVO.getTheOrganizationVOCollection();
            Iterator<OrganizationVO> it = orgCollection.iterator();
            while(it.hasNext()){
                OrganizationVO organizationVO= it.next();
                if(organizationVO.getRole()!=null && organizationVO.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD)) {
                    sendingOrgVO= organizationVO;
                }
                Collection<EntityIdDT> entityCollection = sendingOrgVO.getTheEntityIdDTCollection();
                Iterator<EntityIdDT> entityIterator =  entityCollection.iterator();
                while(entityIterator.hasNext()){
                    EntityIdDT entityIdDT =  entityIterator.next();
                    if(entityIdDT.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_FACILITY_CD)) {
                        sendingFacilityId = entityIdDT;
                    }
                }
            }


            Collection<ActIdDT> actIdDTColl =  new ArrayList<>();
            ActIdDT actIdDT= new ActIdDT();
            actIdDT.setActIdSeq(1);
            actIdDT.setActUid( (Long)(edxLabInformationDT.getRootObserbationUid()));
            actIdDT.setRootExtensionTxt(edxLabInformationDT.getMessageControlID());
            actIdDT.setAssigningAuthorityCd(sendingFacilityId.getAssigningAuthorityCd());
            actIdDT.setAssigningAuthorityDescTxt(sendingFacilityId.getAssigningAuthorityDescTxt());
            actIdDT.setTypeCd(EdxELRConstant.ELR_MESSAGE_CTRL_CD);
            actIdDT.setTypeDescTxt(EdxELRConstant.ELR_MESSAGE_CTRL_DESC);
            actIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDTColl.add(actIdDT);

            HL7EIType fillerType =hl7OBRType.getFillerOrderNumber();
            if(hl7OBRType.getParent()==null ){
                if(fillerType==null || (fillerType!= null && fillerType.getHL7EntityIdentifier()==null)){
                    edxLabInformationDT.setFillerNumberPresent(false);
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.FILLER_FAIL);
                }
                else{
                    edxLabInformationDT.setFillerNumber(fillerType.getHL7EntityIdentifier());
                }
            }
            ActIdDT act2IdDT = new ActIdDT();
            act2IdDT.setActUid((long)(edxLabInformationDT.getRootObserbationUid()));
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
                edxLabInformationDT.setUniversalServiceIdMissing(true);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
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
                    edxLabInformationDT.setOrderTestNameMissing(true);
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                    //TODO: This convert to XML and store as string
                    String xmlElementName = CommonLabUtil.getXMLElementNameForOBR(hl7OBRType)+".UniversalServiceIdentifier";
                    throw new DataProcessingException(EdxELRConstant.NO_ORDTEST_NAME+" XMLElementName: "+xmlElementName);
                }

            }
            observationDT.setPriorityCd(hl7OBRType.getPriorityOBR());
            observationDT.setActivityFromTime(edxLabInformationDT.getOrderEffectiveDate());
            observationDT.setActivityToTime(NBSObjectConverter.processHL7TSType(hl7OBRType.getResultsRptStatusChngDateTime(), EdxELRConstant.DATE_VALIDATION_OBR_RESULTS_RPT_STATUS_CHNG_TO_TIME_MSG));
            observationDT.setEffectiveFromTime(NBSObjectConverter.processHL7TSType(hl7OBRType.getObservationDateTime(),EdxELRConstant.DATE_VALIDATION_OBR_OBSERVATION_DATE_MSG));
            observationDT.setEffectiveToTime(NBSObjectConverter.processHL7TSType(hl7OBRType.getObservationEndDateTime(),EdxELRConstant.DATE_VALIDATION_OBR_OBSERVATION_END_DATE_MSG));

            List<HL7CWEType> reasonArray =hl7OBRType.getReasonforStudy();
            Collection<ObservationReasonDT> obsReasonDTColl = new ArrayList<>();
            for(int i=0; i<reasonArray.size(); i++){
                ObservationReasonDT obsReasonDT= new ObservationReasonDT();
                HL7CWEType reason = reasonArray.get(i);
                if(reason.getHL7Identifier()!=null){
                    obsReasonDT.setReasonCd(reason.getHL7Identifier());
                    obsReasonDT.setReasonDescTxt(reason.getHL7Text());
                }else if(reason.getHL7AlternateIdentifier()!=null){
                    obsReasonDT.setReasonCd(reason.getHL7AlternateIdentifier());
                    obsReasonDT.setReasonDescTxt(reason.getHL7AlternateText());
                }

                if((reason.getHL7Identifier()==null || reason.getHL7Identifier().trim().equalsIgnoreCase("")) &&
                        (reason.getHL7AlternateIdentifier()==null || reason.getHL7AlternateIdentifier().trim().equalsIgnoreCase(""))){
                    edxLabInformationDT.setReasonforStudyCdMissing(true);
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                    //TODO: This convert to XML and store as string
                    String xmlElementName = CommonLabUtil.getXMLElementNameForOBR(hl7OBRType)+".ReasonforStudy";
                    throw new DataProcessingException(EdxELRConstant.NO_REASON_FOR_STUDY+" XMLElementName: "+xmlElementName);
                }

                obsReasonDTColl.add(obsReasonDT);
            }
            if(edxLabInformationDT.getLastChgTime()==null) {
                observationDT.setRptToStateTime(edxLabInformationDT.getAddTime());
            }
            else {
                observationDT.setRptToStateTime(edxLabInformationDT.getLastChgTime());
            }
            observationVO.setTheObservationDT(observationDT);
            observationVO.setTheObservationReasonDTCollection(obsReasonDTColl);
            labResultProxyVO.getTheObservationVOCollection().add(observationVO);
            if(edxLabInformationDT.getRootObservationVO()==null) {
                edxLabInformationDT.setRootObservationVO(observationVO);
            }


            if(hl7OBRType.getParent()==null){
                processRootOBR(hl7OBRType, observationDT, labResultProxyVO, hl7PatientResultSPMType, edxLabInformationDT);
            }

            if(hl7OBRType.getParent()!=null){
                processSusOBR(hl7OBRType, observationDT, labResultProxyVO, edxLabInformationDT);
            }
            if(hl7OBRType.getParentResult()==null){
                edxLabInformationDT.setParentObservationUid(0L);
                edxLabInformationDT.setParentObsInd(false);
            }

            if(hl7OBRType.getResultCopiesTo()!=null){
                for(int i=0; i<hl7OBRType.getResultCopiesTo().size(); i++){
                    HL7XCNType providerType =hl7OBRType.getResultCopiesTo().get(i);
                    edxLabInformationDT.setRole(EdxELRConstant.ELR_COPY_TO_CD);
                    PersonVO personVO = getProviderVO(providerType,null, labResultProxyVO,edxLabInformationDT);
                    labResultProxyVO.getThePersonVOCollection().add(personVO);

                }

            }

        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getObservationRequest:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getObservationRequest:"+ e.getMessage());
        }



        return labResultProxyVO;

    }

    private static void processSusOBR(HL7OBRType hl7OBRType,ObservationDT observationDT,
                               LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {
            EdxLabIdentiferDT edxLabIdentiferDT= null;
            if(hl7OBRType.getParentResult()== null ||
                    hl7OBRType.getParentResult().getParentObservationIdentifier()==null||
                    (hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7Identifier()==null &&
                            hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier()==null)){
                edxLabInformationDT.setReflexOrderedTestCdMissing(true);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            }
            Long parentObservation = null;
            boolean fillerMatch = false;
            if(edxLabInformationDT.getEdxLabIdentiferDTColl()!=null){
                Iterator<EdxLabIdentiferDT> iter = edxLabInformationDT.getEdxLabIdentiferDTColl().iterator();
                while(iter.hasNext()){
                    edxLabIdentiferDT= (EdxLabIdentiferDT)iter.next();
                    if(edxLabIdentiferDT.getIdentifer()!=null
                            && (edxLabIdentiferDT.getIdentifer().equals(hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7Identifier())|| edxLabIdentiferDT.getIdentifer().equals(hl7OBRType.getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier()))
                            && edxLabIdentiferDT.getSubMapID()!=null
                            && edxLabIdentiferDT.getSubMapID().equals(hl7OBRType.getParentResult().getParentObservationSubidentifier())
                            && edxLabIdentiferDT.getObservationValues()!=null
                            && hl7OBRType.getParentResult().getParentObservationValueDescriptor()!=null
                            && edxLabIdentiferDT.getObservationValues().indexOf(hl7OBRType.getParentResult().getParentObservationValueDescriptor().getHL7String())>0)
                    {
                        parentObservation= edxLabIdentiferDT.getObservationUid();
                    }
                    if(edxLabInformationDT.getFillerNumber()!=null
                            && hl7OBRType.getParent().getHL7FillerAssignedIdentifier()!=null
                            && edxLabInformationDT.getFillerNumber().equals(hl7OBRType.getParent().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier())){
                        fillerMatch = true;
                    }
                }
                if(parentObservation == null || !fillerMatch){
                    edxLabInformationDT.setChildSuscWithoutParentResult(true);
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.CHILD_SUSC_WITH_NO_PARENT_RESULT);
                }
            }
            if(edxLabInformationDT.getEdxSusLabDTMap()== null || edxLabInformationDT.getEdxSusLabDTMap().get(parentObservation)!=null){

                ObservationVO obsVO= new ObservationVO();
                ObservationDT obsDT= new ObservationDT();
                obsDT.setCd(EdxELRConstant.ELR_LAB222_CD);
                obsDT.setCdDescTxt(EdxELRConstant.ELR_NO_INFO_DESC);
                obsDT.setObsDomainCdSt1(EdxELRConstant.ELR_REF_ORDER_CD);
                obsDT.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
                obsDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
                obsDT.setStatusTime(edxLabInformationDT.getAddTime());
                obsDT.setObservationUid((long)(edxLabInformationDT.getNextUid()));
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
                actRelationshipDT.setTargetActUid(edxLabInformationDT.getRootObserbationUid());
                actRelationshipDT.setTargetClassCd(EdxELRConstant.ELR_OBS);
                actRelationshipDT.setSourceClassCd(EdxELRConstant.ELR_OBS);
                actRelationshipDT.setAddTime(edxLabInformationDT.getAddTime());
                actRelationshipDT.setLastChgTime(edxLabInformationDT.getAddTime());
                actRelationshipDT.setRecordStatusTime(edxLabInformationDT.getAddTime());
                if(labResultProxyVO.getTheActRelationshipDTCollection()==null) {
                    labResultProxyVO.setTheActRelationshipDTCollection(new ArrayList<>());
                }
                labResultProxyVO.getTheActRelationshipDTCollection().add(actRelationshipDT);

                observationDT.setObsDomainCdSt1(EdxELRConstant.ELR_REF_RESULT_CD);
                ActRelationshipDT arDT = new ActRelationshipDT();
                arDT.setTypeCd(EdxELRConstant.ELR_REFER_CD);
                arDT.setTypeDescTxt(EdxELRConstant.ELR_REFER_DESC);
                arDT.setSourceActUid(obsVO.getTheObservationDT().getObservationUid());
                arDT.setTargetActUid(parentObservation);
                arDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                arDT.setRecordStatusTime(edxLabInformationDT.getAddTime());
                arDT.setTargetClassCd(EdxELRConstant.ELR_OBS);
                arDT.setSourceClassCd(EdxELRConstant.ELR_OBS);
                arDT.setItNew(true);
                arDT.setItDirty(false);
                labResultProxyVO.getTheActRelationshipDTCollection().add(arDT);
                if(hl7OBRType.getParent()!=null){
                    labResultProxyVO.getTheObservationVOCollection().add(obsVO);
                }
                edxLabInformationDT.getEdxSusLabDTMap().put(parentObservation, obsVO.getTheObservationDT().getObservationUid());
            }

            if(parentObservation!=null){
                Long uid = (Long)edxLabInformationDT.getEdxSusLabDTMap().get(parentObservation);
                if(uid!=null){
                    edxLabInformationDT.setParentObservationUid(uid);
                    edxLabInformationDT.setParentObsInd(true);
                }
            }
        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.processSusOBR:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.processSusOBR:"+ e);
        }

    }

    private static void processRootOBR(HL7OBRType hl7OBRType,ObservationDT observationDT,
                                       LabResultProxyVO labResultProxyVO,  HL7PatientResultSPMType hl7PatientResultSPMType,
                                       EdxLabInformationDT edxLabInformationDT) throws DataProcessingException{


        try {

            hl7OBRType.getMedicallyNecessaryDuplicateProcedureReason();
            PersonVO collectorVO = null;
            List<HL7XCNType> collectorArray = hl7OBRType.getCollectorIdentifier();
            if(collectorArray!=null && collectorArray.size() > 1) {
                edxLabInformationDT.setMultipleCollector(true);
            }
            if(collectorArray!=null){
                for(int i=0; i<collectorArray.size();){
                    HL7XCNType collector= collectorArray.get(i);
                    collectorVO = getCollectorVO(collector, labResultProxyVO,edxLabInformationDT);
                    labResultProxyVO.getThePersonVOCollection().add(collectorVO);
                    break;
                }
            }
            if(hl7OBRType.getRelevantClinicalInformation()!=null) {
                observationDT.setTxt(hl7OBRType.getRelevantClinicalInformation());
            }
            if(hl7PatientResultSPMType!=null){
                logger.debug("ObservationRequest.getObservationRequest specimen is being processes for 2.5.1 message type");
                HL7SpecimenHandler.process251Specimen( hl7PatientResultSPMType,  labResultProxyVO,  observationDT,  collectorVO,  edxLabInformationDT);
            }
            List<HL7XCNType> orderingProviderArray = hl7OBRType.getOrderingProvider();
            if(orderingProviderArray!=null && orderingProviderArray.size()  >1){
                edxLabInformationDT.setMultipleOrderingProvider(true);
            }
            PersonVO orderingProviderVO= null;
            if(orderingProviderArray!=null && orderingProviderArray.size() >0){
                for(int i=0; i<orderingProviderArray.size();){
                    HL7XCNType orderingProvider=orderingProviderArray.get(i);
                    Collection<EntityLocatorParticipationDT> entitylocatorColl =null;

                    PersonVO providerVO =null;
                    if(edxLabInformationDT.getOrderingProviderVO()!=null){
                        providerVO =edxLabInformationDT.getOrderingProviderVO();
                        entitylocatorColl=providerVO.getTheEntityLocatorParticipationDTCollection();
                        if(labResultProxyVO.getThePersonVOCollection().contains(providerVO)) {
                            labResultProxyVO.getThePersonVOCollection().remove(providerVO);
                        }
                    }
                    edxLabInformationDT.setRole(EdxELRConstant.ELR_OP_CD);
                    orderingProviderVO= getProviderVO(orderingProvider,entitylocatorColl,labResultProxyVO, edxLabInformationDT);
                    edxLabInformationDT.setOrderingProvider(true);
                    break;
                }
                if(hl7OBRType.getOrderCallbackPhoneNumber()!=null && orderingProviderVO!=null){
                    for(int i=0; i<hl7OBRType.getOrderCallbackPhoneNumber().size(); ){
                        HL7XTNType orderingProvPhone  =hl7OBRType.getOrderCallbackPhoneNumber().get(i);
                        EntityLocatorParticipationDT elpt = NBSObjectConverter.personTelePhoneType(orderingProvPhone, EdxELRConstant.ELR_PROVIDER_CD, orderingProviderVO);
                        elpt.setUseCd(EdxELRConstant.ELR_WORKPLACE_CD);
                        break;
                    }
                }
                if(labResultProxyVO.getThePersonVOCollection()==null) {
                    labResultProxyVO.setThePersonVOCollection(new ArrayList<>());
                }
                if(orderingProviderVO!=null) {
                    labResultProxyVO.getThePersonVOCollection().add(orderingProviderVO);
                }

            }else{
                if(edxLabInformationDT.getOrderingProviderVO()!=null){
                    edxLabInformationDT.setOrderingProvider(false);
                    PersonVO providerVO =edxLabInformationDT.getOrderingProviderVO();
                    if(labResultProxyVO.getThePersonVOCollection().contains(providerVO)) {
                        labResultProxyVO.getThePersonVOCollection().remove(providerVO);
                    }
                }
            }
            if(edxLabInformationDT.isMissingOrderingProvider() && edxLabInformationDT.isMissingOrderingFacility()){
                edxLabInformationDT.setMissingOrderingProviderandFacility(true);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                throw new DataProcessingException("HL7ORCProcessorget.getORCProcessing: Both Ordering Provider and Ordering facility are null. Please check!!!");
            }

            HL7NDLType  princResultInterpretor = hl7OBRType.getPrincipalResultInterpreter();
            edxLabInformationDT.setMultiplePrincipalInterpreter(false);
            edxLabInformationDT.setRole(EdxELRConstant.ELR_LAB_PROVIDER_CD);
            getOtherProviderVO( princResultInterpretor,labResultProxyVO,  edxLabInformationDT);



            if( hl7OBRType.getAssistantResultInterpreter() !=null){
                for(int i = 0; i<hl7OBRType.getAssistantResultInterpreter().size(); i++){
                    HL7NDLType  assPrincResultInterpretor =hl7OBRType.getAssistantResultInterpreter().get(i);
                    edxLabInformationDT.setRole(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                    getOtherProviderVO( assPrincResultInterpretor,labResultProxyVO,  edxLabInformationDT);
                }
            }


            if( hl7OBRType.getTechnician()!=null){
                for(int i = 0; i<hl7OBRType.getTechnician().size(); i++){
                    HL7NDLType  technician =hl7OBRType.getTechnician().get(i);
                    edxLabInformationDT.setRole(EdxELRConstant.ELR_LAB_PERFORMER_CD);
                    getOtherProviderVO( technician,labResultProxyVO,  edxLabInformationDT);
                }
            }
            if( hl7OBRType.getTranscriptionist()!=null){
                for(int i = 0; i<hl7OBRType.getTranscriptionist().size(); i++){
                    HL7NDLType  technician =hl7OBRType.getTranscriptionist().get(1);
                    edxLabInformationDT.setRole(EdxELRConstant.ELR_LAB_ENTERER_CD);
                    getOtherProviderVO( technician,labResultProxyVO,  edxLabInformationDT);
                }
            }
        } catch (Exception e) {
            logger.error(" Exception thrown at ObservationRequest.processRootOBR:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.processRootOBR:"+ e.getMessage() +e);
        }

    }

    private static PersonVO  getCollectorVO(HL7XCNType collector, LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        PersonVO personVO;
        try {
            edxLabInformationDT.setRole(EdxELRConstant.ELR_PROVIDER_CD);
            EntityIdDT entityIdDT = new EntityIdDT();

            entityIdDT.setEntityUid((long)edxLabInformationDT.getNextUid());
            entityIdDT.setAddTime(edxLabInformationDT.getAddTime());
            entityIdDT.setEntityIdSeq(1);
            entityIdDT.setRootExtensionTxt(collector.getHL7IDNumber());
            entityIdDT.setTypeCd(EdxELRConstant.ELR_EMP_IDENT_CD);
            entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_EMP_IDENT_DESC);
            entityIdDT.setAssigningAuthorityCd(edxLabInformationDT.getSendingFacilityClia());
            entityIdDT.setAssigningAuthorityDescTxt(edxLabInformationDT.getSendingFacilityName());
            entityIdDT.setAssigningAuthorityIdType(edxLabInformationDT.getUniversalIdType());
            entityIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDT.setAsOfDate(edxLabInformationDT.getAddTime());
            entityIdDT.setItNew(true);
            entityIdDT.setItDirty(false);
            edxLabInformationDT.setRole(EdxELRConstant.ELR_PROVIDER_CD);

            personVO = HL7PatientHandler.parseToPersonObject(labResultProxyVO,edxLabInformationDT);
            
            personVO.getTheEntityIdDTCollection().add(entityIdDT);

            PersonNameDT personNameDT = new PersonNameDT();
            if(collector.getHL7FamilyName()!=null && collector.getHL7FamilyName().getHL7Surname()!=null) {
                personNameDT.setLastNm(collector.getHL7FamilyName().getHL7Surname());
            }
            personNameDT.setFirstNm(collector.getHL7GivenName());
            personNameDT.setMiddleNm(collector.getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
            personNameDT.setNmPrefix(collector.getHL7Prefix());
            personNameDT.setNmSuffix(collector.getHL7Suffix());
            personNameDT.setNmDegree(collector.getHL7Degree());
            personNameDT.setPersonNameSeq(1);
            personNameDT.setNmUseCd(collector.getHL7NameTypeCode());
            //Defect 5542 Transcriptionist causing issue
            if (personNameDT.getNmUseCd() == null) {
                personNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            }
            personNameDT.setAddTime(edxLabInformationDT.getAddTime());
            personNameDT.setLastChgTime(edxLabInformationDT.getAddTime());
            personNameDT.setAddUserId(edxLabInformationDT.getUserId());
            personNameDT.setLastChgUserId(edxLabInformationDT.getUserId());
            personVO.getThePersonNameDTCollection().add(personNameDT);

            RoleDT roleDT = new RoleDT();
            roleDT.setSubjectEntityUid(personVO.getThePersonDT().getPersonUid());
            roleDT.setCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
            roleDT.setCdDescTxt(EdxELRConstant.ELR_SPECIMEN_PROCURER_DESC);
            roleDT.setSubjectClassCd(EdxELRConstant.ELR_PROV_CD);
            roleDT.setRoleSeq(1L);
            roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
            roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDT.setScopingRoleSeq(1);
            roleDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDT.setItNew(true);
            roleDT.setItDirty(false);
            labResultProxyVO.getTheRoleDTCollection().add(roleDT);
            
        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getCollectorVO:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getCollectorVO:"+ e);
        }

        return personVO;
    }

    private static PersonVO  getOtherProviderVO(HL7NDLType providerType, LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {

        PersonVO personVO;
        try {
            if(providerType==null) {
                return null;
            }
            personVO = HL7PatientHandler.parseToPersonObject(labResultProxyVO,edxLabInformationDT);

            if(providerType.getHL7Name()!=null && providerType.getHL7Name().getHL7IDNumber()!=null){
                Collection<EntityIdDT> entityColl = new ArrayList<>();
                EntityIdDT entityIdDT = new EntityIdDT();
                entityIdDT.setEntityUid((long)(edxLabInformationDT.getNextUid()));
                entityIdDT.setEntityIdSeq(1);
                entityIdDT.setAddTime(edxLabInformationDT.getAddTime());
                entityIdDT.setRootExtensionTxt(providerType.getHL7Name().getHL7IDNumber());
                entityIdDT.setTypeCd(EdxELRConstant.ELR_EMP_IDENT_CD);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_EMP_IDENT_DESC);
                entityIdDT.setAssigningAuthorityCd(edxLabInformationDT.getSendingFacilityClia());
                entityIdDT.setAssigningAuthorityDescTxt(edxLabInformationDT.getSendingFacilityName());
                entityIdDT.setAssigningAuthorityIdType(edxLabInformationDT.getUniversalIdType());
                entityIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                entityIdDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
                entityIdDT.setAsOfDate(edxLabInformationDT.getAddTime());
                entityIdDT.setItNew(true);
                entityIdDT.setItDirty(false);
                entityColl.add(entityIdDT);
                if(entityIdDT.getEntityUid()!=null) {
                    personVO.getTheEntityIdDTCollection().add(entityIdDT);
                }
            }

            ParticipationDT participationDT= new ParticipationDT();
            participationDT.setSubjectEntityUid(personVO.getThePersonDT().getPersonUid());

            if(edxLabInformationDT.getRole().equals(EdxELRConstant.ELR_LAB_PROVIDER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_VERIFIER_DESC);
            }
            else if(edxLabInformationDT.getRole().equals(EdxELRConstant.ELR_LAB_VERIFIER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_VERIFIER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_VERIFIER_DESC);
            }
            else if(edxLabInformationDT.getRole().equals(EdxELRConstant.ELR_LAB_PERFORMER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_PERFORMER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_PERFORMER_DESC);
            }
            else if(edxLabInformationDT.getRole().equals(EdxELRConstant.ELR_LAB_ENTERER_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_ENTERER_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_ENTERER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_ENTERER_DESC);
            }
            else if(edxLabInformationDT.getRole().equals(EdxELRConstant.ELR_LAB_ASSISTANT_CD)){
                participationDT.setCd(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                participationDT.setTypeCd(EdxELRConstant.ELR_LAB_ASSISTANT_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_ASSISTANT_DESC);
            }
            participationDT = NBSObjectConverter.defaultParticipationDT(participationDT,edxLabInformationDT);


            participationDT.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
            participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDT.setActUid(edxLabInformationDT.getRootObserbationUid());
            labResultProxyVO.getTheParticipationDTCollection().add(participationDT);
            edxLabInformationDT.setRole(EdxELRConstant.ELR_PROVIDER_CD);
            personVO = NBSObjectConverter.processCNNPersonName(providerType.getHL7Name(),personVO);
            if(labResultProxyVO.getThePersonVOCollection()==null) {
                labResultProxyVO.setThePersonVOCollection(new ArrayList<>());
            }
            labResultProxyVO.getThePersonVOCollection().add(personVO);
        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getCollectorVO:"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getCollectorVO:"+ e);
        }
        return personVO;
    }

    private static PersonVO  getProviderVO(HL7XCNType orderingProvider, Collection<EntityLocatorParticipationDT> entitylocatorColl, LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        PersonVO personVO =null;

        try {
            EntityIdDT entityIdDT = new EntityIdDT();
            entityIdDT.setEntityUid((long)(edxLabInformationDT.getNextUid()));
            entityIdDT.setAddTime(edxLabInformationDT.getAddTime());
            entityIdDT.setEntityIdSeq(1);
            entityIdDT.setRootExtensionTxt(orderingProvider.getHL7IDNumber());
            entityIdDT.setTypeCd(EdxELRConstant.ELR_PROVIDER_REG_NUM_CD);
            entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_PROVIDER_REG_NUM_DESC);
            entityIdDT.setAssigningAuthorityCd(edxLabInformationDT.getSendingFacilityClia());
            entityIdDT.setAssigningAuthorityDescTxt(edxLabInformationDT.getSendingFacilityName());
            entityIdDT.setAssigningAuthorityIdType(edxLabInformationDT.getUniversalIdType());
            entityIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDT.setAsOfDate(edxLabInformationDT.getAddTime());
            entityIdDT.setItNew(true);
            entityIdDT.setItDirty(false);
            personVO =HL7PatientHandler.parseToPersonObject(labResultProxyVO,edxLabInformationDT);
            if(entitylocatorColl!=null) {
                personVO.setTheEntityLocatorParticipationDTCollection(entitylocatorColl);
            }
            if(entityIdDT.getEntityUid()!=null) {
                personVO.getTheEntityIdDTCollection().add(entityIdDT);
            }

            if(edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)){
                ParticipationDT participationDT = new ParticipationDT();
                participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
                participationDT.setCd(EdxELRConstant.ELR_OP_CD);
                participationDT.setActUid(edxLabInformationDT.getRootObserbationUid());
                participationDT.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
                participationDT.setTypeDescTxt(EdxELRConstant.ELR_ORDERER_DESC);
                participationDT = NBSObjectConverter.defaultParticipationDT(participationDT,edxLabInformationDT);
                participationDT.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
                participationDT.setSubjectEntityUid(personVO.getThePersonDT().getPersonUid());
                labResultProxyVO.getTheParticipationDTCollection().add(participationDT);
                personVO.setRole(EdxELRConstant.ELR_OP_CD);

            }
            PersonNameDT personNameDT = new PersonNameDT();
            if(orderingProvider.getHL7FamilyName()!=null && orderingProvider.getHL7FamilyName().getHL7Surname()!=null ) {
                personNameDT.setLastNm(orderingProvider.getHL7FamilyName().getHL7Surname());
            }
            personNameDT.setFirstNm(orderingProvider.getHL7GivenName());
            personNameDT.setMiddleNm(orderingProvider.getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
            personNameDT.setNmPrefix(orderingProvider.getHL7Prefix());
            personNameDT.setNmSuffix(orderingProvider.getHL7Suffix());
            personNameDT.setNmDegree(orderingProvider.getHL7Degree());
            personNameDT.setNmUseCd(orderingProvider.getHL7NameTypeCode());

            personNameDT.setAddTime(edxLabInformationDT.getAddTime());
            personNameDT.setLastChgTime(edxLabInformationDT.getAddTime());
            personNameDT.setAddUserId(edxLabInformationDT.getUserId());
            personNameDT.setPersonNameSeq(1);
            personNameDT.setLastChgUserId(edxLabInformationDT.getUserId());
            personVO.setThePersonNameDTCollection(new ArrayList<>());
            personNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            personVO.getThePersonNameDTCollection().add(personNameDT);

        } catch (Exception e) {
            logger.error("Exception thrown at ObservationRequest.getOrderingProviderVO:"+e);
            throw new DataProcessingException("Exception thrown at ObservationRequest.getOrderingProviderVO:"+ e);

        }

        return personVO;
    }




}
